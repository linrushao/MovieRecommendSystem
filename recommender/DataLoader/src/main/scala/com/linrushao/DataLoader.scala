package com.linrushao

import com.linrushao.scalamodel.{ESConfig, MongoConfig, Movies, Ratings}
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient
import com.linrushao.javamodel.Constant._
import com.linrushao.javamodel.SQLUtils
import com.linrushao.scalamodel.ConfigParams.params

import java.math.BigInteger
import java.net.InetAddress
import java.security.{MessageDigest, NoSuchAlgorithmException}


object DataLoader {

  /** ************配置主机名:端口号的正则表达式***************** */
  val ES_HOST_PORT_REGEX = "(.+):(\\d+)".r

  /**
   * 保存数据到MongoDB
   *
   * @param movies    电影数据集
   * @param ratings   评分数据集
   * @param mongoConf MongoDB的配置
   */
  def storeDataInMongo(movies: DataFrame, ratings: DataFrame)(implicit mongoConf: MongoConfig): Unit = {

    // 创建到MongoDB的连接
    val mongoClient = MongoClient(MongoClientURI(mongoConf.uri))

    // 删除Movies的Collection
    mongoClient(mongoConf.db)(MONGODB_MOVIE_COLLECTION).dropCollection()

    // 删除Ratings的Collection
    mongoClient(mongoConf.db)(MONGODB_RATING_COLLECTION).dropCollection()

    //将Movies数据集写入到MongoDB
    movies
      .write
      .option("uri", mongoConf.uri)
      .option("collection", MONGODB_MOVIE_COLLECTION)
      .mode("overwrite")
      .format(SQLUtils.SPARK_MONGODB_SQL)
      .save()

    //将Ratings数据集写入到MongoDB
    ratings
      .write.option("uri", mongoConf.uri)
      .option("collection", MONGODB_RATING_COLLECTION)
      .mode("overwrite")
      .format(SQLUtils.SPARK_MONGODB_SQL)
      .save()

    //创建索引
    mongoClient(mongoConf.db)(MONGODB_MOVIE_COLLECTION).createIndex(MongoDBObject("mid" -> 1))
    mongoClient(mongoConf.db)(MONGODB_RATING_COLLECTION).createIndex(MongoDBObject("mid" -> 1))
    mongoClient(mongoConf.db)(MONGODB_RATING_COLLECTION).createIndex(MongoDBObject("uid" -> 1))

    print("成功将数据保存到MongoDB中！！！")
    //关闭MongoDB的连接
    mongoClient.close()
  }

  /**
   * 保存数据到ElasticSearch
   *
   * @param movies 电影数据集
   * @param esConf ElasticSearch的配置对象
   */
  def storeMoiveDataInES(movies: DataFrame)(implicit esConf: ESConfig): Unit = {
    System.setProperty("es.set.netty.runtime.available.processors", "false")

    // 需要操作的Index名称
    val indexName = esConf.index

    // 新建一个到ES的连接配置
    var settings: Settings = Settings
      .builder()
      .put("cluster.name", esConf.clustername)
      .build()

    // 创建到ES的连接客户端
    val esClient = new PreBuiltTransportClient(settings)

    //对于设定的多个Node分别通过正则表达式进行模式匹配，并添加到客户端实例
    esConf.transportHosts.split(";")
      .foreach {
        case ES_HOST_PORT_REGEX(host: String, port: String) =>
          esClient.addTransportAddress(new TransportAddress(InetAddress.getByName(host), port.toInt))
      }

    // 检查如果Index存在，那么删除Index
    if (
      esClient
        .admin()
        .indices()
        .exists(new IndicesExistsRequest(indexName))
        .actionGet()
        .isExists) {
      // 删除Index
      esClient
        .admin()
        .indices()
        .delete(new DeleteIndexRequest(indexName))
        .actionGet()
    }

    // 声明写出时的ES配置信息
    val movieOptions = Map(
      "es.nodes" -> esConf.httpHosts,
      "es.mapping.date.rich" -> "false", //在命令行提交时设置spark.es.mapping.date.rich为false生效，可以不解析为date，直接返回string
      "es.http.timeout" -> "100m",
      "es.mapping.id" -> "mid")
    //     创建Index
    esClient.admin().indices().create(new CreateIndexRequest(indexName)
      //设置默认分词器为ik，可以模糊查询中文
      .settings(Settings.builder()
        .put("index.number_of_shards", 1)
        .put("index.number_of_replicas", 1)
        .put("analysis.analyzer.default.type", "ik_smart")
        .put("analysis.analyzer.default.use_smart", "false")
        .put("analysis.analyzer.default.tokenizer", "ik_smart")
      )).actionGet()

    // 电影数据写出时的Type名称【表】
    val movieTypeName = s"$indexName/$ELEASTICSEARCH_MOVIE_TYPE"

    // 将Movie信息保存到ES
    movies
      .write
      .options(movieOptions)
      .format(SQLUtils.SPARK_ELASTICSEARCH_SQL)
      .mode("overwrite")
      .save(movieTypeName)
    esClient.close()
    print("成功将数据保存到ES中！！！")
  }

  def main(args: Array[String]): Unit = {


    // 声明Spark的配置信息
    val conf = new SparkConf().setAppName("DataLoader")
      .setMaster(params("spark.cores").asInstanceOf[String])

    /**
     * 创建一个sparkSession
     */
    val spark = SparkSession
      .builder()
      .config(conf)
      .getOrCreate()
    // 定义ElasticSearch的配置对象
    implicit val esConf = new ESConfig(
      params("es.httpHosts").asInstanceOf[String],
      params("es.transportHosts").asInstanceOf[String],
      params("es.index").asInstanceOf[String],
      params("es.cluster.name").asInstanceOf[String])

    // 引入SparkSession内部的隐式转换
    import spark.implicits._

    // 定义MongoDB的配置对象
    implicit val mongoConf = new MongoConfig(params("mongo.uri").asInstanceOf[String], params("mongo.db").asInstanceOf[String])

    // 加载Movies数据集
    val movieRDD = spark.sparkContext.textFile(ORIGINAL_MOVIE_DATA_PATH)

    // 加载Ratings数据集
    val ratingRDD = spark.sparkContext.textFile(ORIGINAL_RATING_DATA_PATH)

    //mid: Int,  name: String, actors:String,image:String,directors:String, douban_score:Double,douban_votes:Int,genres:String,language:String,timelong:String,regions:String,issue:String,descri: String, tags:String, shoot:Int,  actor_ids: String,  director_ids: String
    // 将movie电影RDD转换为DataFrame
    val moviesDF = movieRDD.map(line => {
      val x = line.split("\\^\\^")
      val moviesName: String = x(1).trim.split(" - ")(0)
      Movies(x(0).trim.toInt, moviesName, x(2).trim, x(3).trim, x(4).trim, x(5).trim.toDouble, x(6).trim.toInt, x(7).trim, x(8).trim, x(9).trim, x(10).trim, x(11).trim, x(12).trim, x(13).trim, x(14).trim.toInt, x(15).trim, x(16).trim)
    }).toDF()

    //rating_id:Int,uid: String,  mid: Int,  score: Double,  timestamp: String
    // 将rating评分RDD转换为DataFrame
    val ratingsDF = ratingRDD.map(line => {
      val x = line.split(",")
      val uidHashCode: Int = x(1).trim.hashCode
      //将MD5码转换为int类型
      //      var uid: Int = MD5ToIntConverter(x(1))
      Ratings(x(0).toInt, x(1).trim, uidHashCode, x(2).trim.toInt, x(3).toInt, x(4).trim)
    }).toDF()

    //缓存,可以让程序能快速运行
    moviesDF.cache()

    // 将数据保存到MongoDB
    storeDataInMongo(moviesDF, ratingsDF)

    // 保存数据到ES
    storeMoiveDataInES(moviesDF)

    //去除缓存
    moviesDF.unpersist()

    print("所有数据保存成功！！！")
    //关闭Spark
    spark.close()
  }

  //将md5码转换为int类型
  def MD5ToIntConverter(md5: String): Int = {
    try {
      // 创建MD5哈希算法实例
      val messageDigest = MessageDigest.getInstance("MD5")

      // 计算MD5哈希值
      messageDigest.update(md5.getBytes)

      // 将MD5哈希值转换为BigInteger
      val bigInteger = new BigInteger(1, messageDigest.digest)

      // 将BigInteger转换为int类型
      bigInteger.intValue()
    } catch {
      case e: NoSuchAlgorithmException =>
        e.printStackTrace()
        0 // 如果发生异常，返回默认值
    }
  }
}


