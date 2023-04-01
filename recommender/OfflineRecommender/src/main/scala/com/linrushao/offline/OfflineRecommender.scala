package com.linrushao.offline


import com.linrushao.offline.Constant.MAX_RECOMMENDATIONS
import org.apache.spark.SparkConf

/**
 * @Author LRS
 * @Date 2022/9/10 14:48
 *       Desc
 */

/**
 * 集群运行命令：
./spark-submit  --master spark://hadoop201:7077
--class com.linrushao.offline.OfflineRecommender
--packages org.mongodb.spark:mongo-spark-connector_2.12:2.4.1
--jars /opt/module/spark-standalone/jars/jblas-1.2.4-SNAPSHOT.jar
,/opt/module/spark-standalone/jars/casbah-core_2.12-3.1.1.jar
,/opt/module/spark-standalone/jars/mongo-java-driver-3.12.10.jar
,/opt/module/spark-standalone/jars/mongo-hadoop-core-2.0.0.jar
/opt/module/spark-standalone/test/OfflineRecommender-1.0-SNAPSHOT.jar

 */

/**
 * 继承App
 * 源码
 * Here, object `Main` inherits the `main` method of `App`.
 * 就是说继承了main方法，可以直接运行该项目
 */
object OfflineRecommender extends App {

  val params = scala.collection.mutable.Map[String, Any]()
  params += "spark.cores" -> "spark://hadoop201:7077"
//  params += "spark.cores" -> "local[*]"
  params += "mongo.uri" -> "mongodb://hadoop201:27017/movierecommendsystem"
  params += "mongo.db" -> "movierecommendsystem"
  params += "maxRecommendations" -> MAX_RECOMMENDATIONS.toString

  implicit val conf = new SparkConf()
    .setAppName("OfflineRecommender")
    .setMaster(params("spark.cores").asInstanceOf[String])
    .set("spark.executor.memory", "2G")

  implicit val mongoConf = new MongoConfig(params("mongo.uri").asInstanceOf[String],
    params("mongo.db").asInstanceOf[String])

  val maxRecommendations = params("maxRecommendations").asInstanceOf[String].toInt
  //计算推荐
  ALSTrainer.calculateRecs(maxRecommendations)
}
