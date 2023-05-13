package com.linrushao.offline


import com.linrushao.offline.Constant.MAX_RECOMMENDATIONS
import org.apache.spark.SparkConf

/**
 * @Author LRS
 * @Date 2022/9/10 14:48
 *       Desc
 */


/**
 * 继承App
 * 源码
 * Here, object `Main` inherits the `main` method of `App`.
 * 就是说继承了main方法，可以直接运行该项目
 */
object OfflineRecommender extends App {

  val params = scala.collection.mutable.Map[String, Any]()
//  params += "spark.cores" -> "spark://hadoop201:7077"
//  params += "spark.cores" -> "spark://master:7077"
  params += "spark.cores" -> "local[*]"
//  params += "mongo.uri" -> "mongodb://10.144.193.51:27017/movierecommendsystem"//宿舍
//  params += "mongo.uri" -> "mongodb://10.152.193.51:27017/movierecommendsystem"//图书馆
  params += "mongo.uri" -> "mongodb://localhost:27017/movierecommendsystem"
  params += "mongo.db" -> "movierecommendsystem"
  params += "maxRecommendations" -> MAX_RECOMMENDATIONS.toString

  implicit val conf = new SparkConf()
    .setAppName("OfflineRecommender")
    .setMaster(params("spark.cores").asInstanceOf[String])
    .set("spark.executor.memory", "6G")
    //将临时计算结果保存在本地
    .set("spark.local.dir","E:\\TempData")

  implicit val mongoConf = new MongoConfig(params("mongo.uri").asInstanceOf[String],
    params("mongo.db").asInstanceOf[String])

  val maxRecommendations = params("maxRecommendations").asInstanceOf[String].toInt
  //计算推荐
  ALSTrainer.calculateRecs(maxRecommendations)
}
