package com.linrushao.scalamodel

import com.linrushao.javamodel.Constant.MAX_RECOMMENDATIONS

/**
 * @Author linrushao
 * @Date 2023-06-24
 */
object ConfigParams {
  //创建全局配置
  val params = scala.collection.mutable.Map[String, Any]()
  //**********Spark配置参数**********
  //  params += "spark.cores" -> "spark://hadoop201:7077"
  //  params += "spark.cores" -> "spark://master:7077"
  params += "spark.cores" -> "local[*]"

  //**********MongoDB配置参数**********
  params += "mongo.uri" -> "mongodb://localhost:27017/movierecommendsystem"
//  params += "mongo.uri" -> "mongodb://hadoop201:27017/movierecommendsystem"
  params += "mongo.db" -> "movierecommendsystem"


  //**********ElasticSearch配置参数**********
  params += "es.httpHosts" -> "localhost:9200"
  params += "es.transportHosts" -> "localhost:9300"
  params += "es.index" -> "movierecommendsystemdata"
  //    params += "es.index" -> "englishmovierecommendsystemdata"
  params += "es.cluster.name" -> "es-cluster"

  //**********Kafka配置参数**********
  params += "kafka.topic" -> "recommender"

}
