package com.linrushao.offline

import breeze.numerics.sqrt
import com.linrushao.offline.Constant.{MOVIES_COLLECTION_NAME, RATING_COLLECTION_NAME}
import org.apache.spark.SparkConf
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
 * @Author LRS
 * @Date 2022/10/17 8:40
 *       Desc ALS模型参数选取
 */
object ALSTrainerApp{
  /**
   * 其中 adjustALSParams 方法是模型评估的核心，输入一组训练数据和测试数据，
   * 输出计算得到最小 RMSE 的那组参数。
   * @param trainData 训练集
   * @param realRatings 真实集（测试集）
   */
  def parameterAdjust(trainData: RDD[Rating], realRatings: RDD[Rating]): (Int, Double, Double) = {
    val evaluations =
      for (rank   <- Array(10);
           iterations <-Array(10);
           lambda <- Array(1.0);
           alpha  <- Array(1.0))
      yield {
        val model = ALS.trainImplicit(trainData, rank, iterations, lambda, alpha)
        val rmse = computeRmse(model, realRatings)
        ((rank, lambda, alpha), rmse)
      }
    val ((rank, lambda, alpha), rmse) = evaluations.sortBy(_._2).head
    println("最后的参数调整，最好的参数是：" + rmse)
    (rank, lambda, alpha)
  }

  def computeRmse(model: MatrixFactorizationModel, realRatings: RDD[Rating]): Double = {
    val testingData = realRatings.map{ case Rating(user, product, rate) =>
      (user, product)
    }

    val prediction = model.predict(testingData).map{ case Rating(user, product, rate) =>
      ((user, product), rate)
    }

    val realPredict = realRatings.map{case Rating(user, product, rate) =>
      ((user, product), rate)
    }.join(prediction)

    sqrt(realPredict.map{ case ((user, product), (rate1, rate2)) =>
      val err = rate1 - rate2
      err * err
    }.mean())//mean = sum(list) / len(list)
  }

  def main(args: Array[String]): Unit = {

    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://hadoop201:27017/movierecommendsystem",
      "mongo.db" -> "movierecommendsystem"
    )

    val mongoConf = MongoConfig(config("mongo.uri"), config("mongo.db"))

    val conf = new SparkConf()
      .setAppName("ALSTrainerApp")
      .setMaster(config("spark.cores"))
      .set("spark.driver.memory","6G")

    val spark = SparkSession.builder()
      .config(conf)
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")
    import spark.implicits._

    val ratings = spark.read
      .option("uri", mongoConf.uri)
      .option("collection", RATING_COLLECTION_NAME)
      .format("com.mongodb.spark.sql")
      .load()
      .select($"mid",$"uid",$"score")
      .cache

    val users = ratings
      .select($"uid")
      .distinct
      .map(r => r.getAs[Int]("uid"))
      .cache

    val movies = spark.read
      .option("uri", mongoConf.uri)
      .option("collection", MOVIES_COLLECTION_NAME)
      .format("com.mongodb.spark.sql")
      .load()
      .select($"mid")
      .distinct
      .map(r => r.getAs[Int]("mid"))
      .cache

    val trainData = ratings.map{ line =>
      Rating(line.getAs[Int]("uid"), line.getAs[Int]("mid"), line.getAs[Double]("score"))
    }.rdd.cache()

    println(parameterAdjust(trainData,trainData))
    //去除缓存
    ratings.unpersist()
    users.unpersist()
    movies.unpersist()
    trainData.unpersist()

    spark.close()
  }
}
