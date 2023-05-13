package com.linrushao.offline

import com.linrushao.offline.Constant.{MOVIES_COLLECTION_NAME, MOVIE_RECS_COLLECTION_NAME, RATING_COLLECTION_NAME, USER_RECS_COLLECTION_NAME}
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import org.apache.spark.SparkConf
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.sql.{Dataset, SparkSession}
import org.jblas.DoubleMatrix

/**
 * @Author LRS
 * @Date 2022/9/10 16:02
 *       Desc ALS模型推荐
 */
object ALSTrainer extends Serializable {
  /**
   * 计算推荐数据
   * @param maxRecs
   * @param _conf
   * @param mongoConf
   */
  def calculateRecs(maxRecs: Int)(implicit _conf: SparkConf, mongoConf: MongoConfig): Unit = {

    /**
     * 创建一个sparkSession
     */
    val spark = SparkSession.builder()
      .config(_conf)
      .getOrCreate()

    import spark.implicits._

    /**
     * 加载评分数据集合并去除时间项
     */
    val ratings = spark.read
      .option("uri", mongoConf.uri)
      .option("collection", RATING_COLLECTION_NAME)
      .format("com.mongodb.spark.sql")
      .load()
      .select($"mid", $"uid", $"score")
      .cache

    /**
     * 评分集合中的用户数据，做去重处理
     */
    val users = ratings
      .select($"uid")
      .distinct
      .map(r => r.getAs[Int]("uid"))
      .cache

    /**
     * 加载电影集合
     */
    val movies = spark.read
      .option("uri", mongoConf.uri)
      .option("collection", MOVIES_COLLECTION_NAME)
      .format("com.mongodb.spark.sql")
      .load()
      .select($"mid")
      .distinct
      .map(r => r.getAs[Int]("mid"))
      .cache

    /**
     * 训练集
     */
    val trainData = ratings.map { line =>
      Rating(line.getAs[Int]("uid"), line.getAs[Int]("mid"), line.getAs[Double]("score"))
    }.rdd.cache()

    /**
     * 机器模型参数
     * train方法源码：
     *ratings    RDD of [[Rating]] objects with userID, productID, and rating
     *rank       number of features to use (also referred to as the number of latent factors)
     *iterations number of iterations of ALS
     *lambda     regularization parameter
     */
    val (rank, iterations, lambda) = (20, 5, 0.01)
    val model = ALS.train(trainData, rank, iterations, lambda)

    implicit val mongoClient = MongoClient(MongoClientURI(mongoConf.uri))

    /**
     * 基于用户推荐
     */
    calculateUserRecs(maxRecs, model, users, movies)

    /**
     * 基于电影推荐
     */
    calculateProductRecs(maxRecs, model, movies)

    mongoClient.close()

    //去除缓存
    ratings.unpersist()
    users.unpersist()
    movies.unpersist()
    trainData.unpersist()

    spark.close()
  }


  /**
   * 计算为用户推荐的电影集合矩阵 RDD[UserRecommendation(id: Int, recs: Seq[Rating])]
   * @param maxRecs 最大的推荐数目
   * @param model 模型
   * @param users 用户数据集
   * @param products 产品数据集（电影数据集）
   * @param mongoConf mongodb数据库配置
   * @param mongoClient  mongodb数据库客户端
   */
  private def calculateUserRecs(maxRecs: Int,
                                model: MatrixFactorizationModel,
                                users: Dataset[Int],
                                products: Dataset[Int])(implicit mongoConf: MongoConfig,
                                                        mongoClient: MongoClient): Unit = {

    import users.sparkSession.implicits._

    /**
     * 交叉连接（笛卡尔积）
     */
    val userProductsJoin = users.crossJoin(products)

    val userRating = userProductsJoin.map { row => (row.getAs[Int](0), row.getAs[Int](1)) }.rdd

    object RatingOrder extends Ordering[Rating] {
      def compare(x: Rating, y: Rating) = y.rating compare x.rating
    }

    val recommendations = model.predict(userRating)
      .filter(_.rating > 0)
      .groupBy(p => p.user)
      .map { case (uid, predictions) =>
        val recommendations = predictions.toSeq.sorted(RatingOrder)
          .take(maxRecs)
          .map(p => Recommendation(p.product, p.rating))
        UserRecommendation(uid, recommendations)
      }.toDF()

    /**
     * 保存数据之前先删除原先的数据库集合
     */
    mongoClient(mongoConf.db)(USER_RECS_COLLECTION_NAME).dropCollection()

    /**
     * 保存数据到mongodb中
     */
    recommendations
      .write
      .option("uri", mongoConf.uri)
      .option("collection", USER_RECS_COLLECTION_NAME)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save
    //这是下标由1开始
    mongoClient(mongoConf.db)(USER_RECS_COLLECTION_NAME).createIndex(MongoDBObject("uid" -> 1))
  }

  /**
   * 基于电影的推荐，电影推荐矩阵
   * @param maxRecs 最大推荐数目
   * @param model ALS模型
   * @param products 产品推荐（movie）
   * @param mongoConf 数据库配置
   * @param mongoClient mongodb数据库客户端
   */
  private def calculateProductRecs(maxRecs: Int, model: MatrixFactorizationModel, products: Dataset[Int])(implicit mongoConf: MongoConfig, mongoClient: MongoClient): Unit = {

    import products.sparkSession.implicits._

    object RatingOrder extends Ordering[(Int, Int, Double)] {
      def compare(x: (Int, Int, Double), y: (Int, Int, Double)) = y._3 compare x._3
    }

    /**
     * 产品矢量
     */
    val productsVectorRdd = model.productFeatures
      .map { case (movieId, factor) =>
        val factorVector = new DoubleMatrix(factor)
        (movieId, factorVector)
      }

    /**
     * 最小相似度（大于06的）太小的说明两者相似度很低
     */
    val minSimilarity = 0.6

    /**
     * cartesian 笛卡尔积
     */
    val movieRecommendation = productsVectorRdd.cartesian(productsVectorRdd)
      .filter {
        case ((movieId1, vector1), (movieId2, vector2))
        => movieId1 != movieId2
      }.map {
        case ((movieId1, vector1), (movieId2, vector2))
          //计算余弦相似度
        => val sim = cosineSimilarity(vector1, vector2)
        (movieId1, movieId2, sim)
     }.filter(_._3 >= minSimilarity)
      .groupBy(p => p._1)
      .map { case (mid: Int, predictions: Iterable[(Int, Int, Double)]) =>
        val recommendations = predictions.toSeq.sorted(RatingOrder)
          .take(maxRecs)
          .map(p => Recommendation(p._2, p._3.toDouble))
        MovieRecommendation(mid, recommendations)
      }.toDF()

    /**
     * 保存数据之前先判断删除原先的数据库集合
     */
    mongoClient(mongoConf.db)(MOVIE_RECS_COLLECTION_NAME).dropCollection()
    /**
     * 保存数据到mongodb中
     */
    movieRecommendation.write
      .option("uri", mongoConf.uri)
      .option("collection", MOVIE_RECS_COLLECTION_NAME)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save
    //这是下标由1开始
    mongoClient(mongoConf.db)(MOVIE_RECS_COLLECTION_NAME).createIndex(MongoDBObject("mid" -> 1))
  }

  /**
   * 余弦相似度（计算两个产品之间的距离）
   * @param vec1
   * @param vec2
   * @return
   */
  private def cosineSimilarity(vec1: DoubleMatrix, vec2: DoubleMatrix): Double = {
    vec1.dot(vec2) / (vec1.norm2() * vec2.norm2())
  }
}
