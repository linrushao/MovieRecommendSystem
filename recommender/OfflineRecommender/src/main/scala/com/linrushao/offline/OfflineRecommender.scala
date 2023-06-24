package com.linrushao.offline

import com.linrushao.scalamodel.MongoConfig
import org.apache.spark.SparkConf
import com.linrushao.javamodel.Constant
import com.linrushao.scalamodel.ConfigParams.params

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

  implicit val conf = new SparkConf()
    .setAppName("OfflineRecommender")
    .setMaster(params("spark.cores").asInstanceOf[String])
    .set("spark.executor.memory", "6G")
    //将临时计算结果保存在本地
    .set("spark.local.dir","D:\\TempData")

  implicit val mongoConf = MongoConfig(params("mongo.uri").asInstanceOf[String],
    params("mongo.db").asInstanceOf[String])

  //计算推荐
  ALSTrainer.calculateRecs(Constant.MAX_RECOMMENDATIONS)
}
