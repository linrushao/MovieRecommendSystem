package com.linrushao.streamingRecommender

import com.linrushao.scalamodel.{MongoConfig, MovieRecs}
import com.mongodb.casbah.Imports.{MongoClient, MongoClientURI}
import com.mongodb.casbah.commons.MongoDBObject
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis
import com.linrushao.javamodel.Constant._

/**
 * @Author LRS
 * @Date 2022/9/11 15:16
 *       Desc
 */
// 连接助手对象
object ConnHelper extends Serializable {
  lazy val jedis = new Jedis("master")
  lazy val mongoClient = MongoClient(MongoClientURI("mongodb://localhost:27017/movierecommendsystem"))
}

object StreamingRecommender {

  //入口方法
  def main(args: Array[String]): Unit = {

    //Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://localhost:27017/movierecommendsystem",
      "mongo.db" -> "movierecommendsystem",
      "kafka.topic" -> "recommender"
    )

    val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("StreamingRecommender")

    // 创建一个SparkSession
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    // 拿到streaming context
    val sc = spark.sparkContext
    val ssc = new StreamingContext(sc, Seconds(2)) // batch duration

    import spark.implicits._

    implicit val mongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))

    // 加载电影相似度矩阵数据，把它广播出去
    val simMovieMatrix = spark.read
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_MOVIE_RECS_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[MovieRecs]
      .rdd
      .map { movieRecs => // 为了查询相似度方便，转换成map
        (movieRecs.mid, movieRecs.recs.map(x => (x.mid, x.score)).toMap)
      }.collectAsMap()

    //广播变量，将数据放到内存中，exector需要的时候直接在内存中取。
    val simMovieMatrixBroadCast = sc.broadcast(simMovieMatrix)

    // 定义kafka连接参数
    val kafkaParam = Map(
      "bootstrap.servers" -> "master:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "recommender",
      "auto.offset.reset" -> "latest"
    )
    // 通过kafka创建一个DStream
    val kafkaStream = KafkaUtils.createDirectStream[String, String](ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Array(config("kafka.topic")), kafkaParam)
    )

    //从Kafka中读取数据（消费者），将读取到的数据进行处理
    // 把原始数据UID|MID|SCORE|TIMESTAMP 转换成评分流
    val ratingStream = kafkaStream.map { case msg =>
      val dataArr: Array[String] = msg.value().split("\\|")
      val uid = dataArr(0).toInt
      val mid = dataArr(1).toInt
      val score = dataArr(2).toDouble
      val timestamp = dataArr(3).toLong
      (uid, mid, score, timestamp)
    }.cache

    // 继续做流式处理，核心实时算法部分
    ratingStream.foreachRDD {
      rdds =>
        rdds.foreach {
              //匹配算法匹配到一样类型的就执行下一步
          case (uid, mid, score, timestamp) => {
            println("评分数据来了! >>>>>>>>>>>>>>>>")
            println("用户ID："+uid+"电影ID:"+mid+" "+"\n"+"评分分数：" + score)

            // 1. 从redis里获取当前用户最近的K次评分，保存成Array[(mid, score)]
            val userRecentlyRatings = getUserRecentlyRating(MAX_USER_RATINGS_NUM, uid, ConnHelper.jedis)

            // 2. 从相似度矩阵中取出当前电影最相似的N个电影，作为备选列表，Array[mid]
            val candidateMovies = getTopSimMovies(MAX_SIM_MOVIES_NUM, mid, uid, simMovieMatrixBroadCast.value)

            // 3. 对每个备选电影，计算推荐优先级，得到当前用户的实时推荐列表，Array[(mid, score)]
            val streamRecs = computeMovieScores(candidateMovies, userRecentlyRatings, simMovieMatrixBroadCast.value)

            println("推荐前10部电影：")
            streamRecs.take(10).foreach(
              x=>println("电影ID："+x._1,"电影评分："+x._2)
            )

            // 4. 把推荐数据保存到mongodb
            saveDataToMongoDB(uid, streamRecs)
          }
        }
    }
    // 开始接收和处理数据
    ssc.start()

    println("Spark Stream流式计算开始!>>>>>>>>>>>>>>> ")

    ssc.awaitTermination()

  }

  // redis操作返回的是java类，为了用map操作需要引入转换类
  import scala.collection.JavaConversions._

  /**
   * 获取近时间来用户的评分数据
   * @param num  评分数量
   * @param uid  用户ID
   * @param jedis  redis的配置，连接redis
   * @return
   */
  def getUserRecentlyRating(num: Int, uid: Int, jedis: Jedis): Array[(Int, Double)] = {
    // 从redis读取数据，用户评分数据保存在 uid:UID 为key的队列里，value是 MID:SCORE
    jedis.lrange("uid:" + uid, 0, num - 1)
      .map {
        item => // 具体每个评分又是以冒号分隔的两个值
          val attr = item.split(":")
          (attr(0).trim.toInt, attr(1).trim.toDouble)
      }.toArray
  }

  /**
   * 获取跟当前电影做相似的num个电影，作为备选电影
   * @param num       相似电影的数量
   * @param mid       当前电影ID
   * @param uid       当前评分用户ID
   * @param simMovies 相似度矩阵
   * @return 过滤之后的备选电影列表
   */
  def getTopSimMovies(num: Int,
                      mid: Int,
                      uid: Int,
                      simMovies: scala.collection.Map[Int, scala.collection.immutable.Map[Int, Double]])
                     (implicit mongoConfig: MongoConfig): Array[Int] = {



    // 1. 从相似度矩阵中拿到所有相似的电影
    val allSimMovies = simMovies(mid).toArray

    // 2. 从mongodb中查询用户已看过的电影
    val ratingExist = ConnHelper.mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION)
      .find(MongoDBObject("uid" -> uid))
      .toArray
      .map {
        item => item.get("mid").toString.toInt
      }



    // 3. 把看过的过滤，得到输出列表
    allSimMovies.filter(x => !ratingExist.contains(x._1))
      .sortWith(_._2 > _._2)
      .take(num)
      .map(x => x._1)
  }

  def computeMovieScores(candidateMovies: Array[Int],
                         userRecentlyRatings: Array[(Int, Double)],
                         simMovies: scala.collection.Map[Int, scala.collection.immutable.Map[Int, Double]]): Array[(Int, Double)] = {
    // 定义一个ArrayBuffer，用于保存每一个备选电影的基础得分
    val scores = scala.collection.mutable.ArrayBuffer[(Int, Double)]()
    // 定义一个HashMap，保存每一个备选电影的增强减弱因子
    val increMap = scala.collection.mutable.HashMap[Int, Int]()
    val decreMap = scala.collection.mutable.HashMap[Int, Int]()

    for (candidateMovie <- candidateMovies; userRecentlyRating <- userRecentlyRatings) {
      // 拿到备选电影和最近评分电影的相似度
      val simScore = getMoviesSimScore(candidateMovie, userRecentlyRating._1, simMovies)
      // 相似度大于7的才要
      if (simScore > 0.7) {
        // 计算备选电影的基础推荐得分
        scores += ((candidateMovie, simScore * userRecentlyRating._2))
        if (userRecentlyRating._2 > 3) {
          increMap(candidateMovie) = increMap.getOrDefault(candidateMovie, 0) + 1
        } else {
          decreMap(candidateMovie) = decreMap.getOrDefault(candidateMovie, 0) + 1
        }
      }
    }
    // 根据备选电影的mid做groupby，根据公式去求最后的推荐评分
    scores.groupBy(_._1).map {
      // groupBy之后得到的数据 Map( mid -> ArrayBuffer[(mid, score)] )
      case (mid, scoreList) =>
        (mid, scoreList.map(_._2).sum / scoreList.length + log(increMap.getOrDefault(mid, 1)) - log(decreMap.getOrDefault(mid, 1)))
    }.toArray.sortWith(_._2 > _._2)
  }

  // 获取两个电影之间的相似度
  def getMoviesSimScore(mid1: Int,
                        mid2: Int,
                        simMovies: scala.collection.Map[Int,
                          scala.collection.immutable.Map[Int, Double]]): Double = {

    simMovies.get(mid1) match {
      case Some(sims) => sims.get(mid2) match {
        case Some(score) => score
        case None => 0.0
      }
      case None => 0.0
    }
  }

  // 求一个数的对数，利用换底公式，底数默认为10
  def log(m: Int): Double = {
    val N = 10
    math.log(m) / math.log(N)
  }

  //保存数据到mongodb中
  def saveDataToMongoDB(uid: Int, streamRecs: Array[(Int, Double)])(implicit mongoConfig: MongoConfig): Unit = {
    // 定义到StreamRecs表的连接
    val streamRecsCollection = ConnHelper.mongoClient(mongoConfig.db)(MONGODB_STREAM_RECS_COLLECTION)

    // 如果表中已有uid对应的数据，则删除
    streamRecsCollection.findAndRemove(MongoDBObject("uid" -> uid))
    // 将streamRecs数据存入表中
    streamRecsCollection.insert(MongoDBObject("uid" -> uid,
      "recs" -> streamRecs.map(x => MongoDBObject("mid" -> x._1, "score" -> x._2))))
  }
}
