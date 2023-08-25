package com.linrushao.scalamodel

import com.linrushao.javamodel.Constant.MAX_RECOMMENDATIONS

/**
 * @Author linrushao
 * @Date 2023-06-24
 */
object ConfigParams {
  //创建全局配置
  //mutable 可变集合
  val params = scala.collection.mutable.Map[String, Any]()
  //**********Spark配置参数**********
  params += "spark.cores" -> "local[*]"

  //**********MongoDB配置参数**********
  params += "mongo.uri" -> "mongodb://120.79.35.91:27017/movierecommendsystem"
  params += "mongo.db" -> "movierecommendsystem"


  //**********ElasticSearch配置参数**********
  params += "es.httpHosts" -> "120.79.35.91:9200"
  params += "es.transportHosts" -> "120.79.35.91:9300"
  params += "es.index" -> "movierecommendsystemdata"
  params += "es.cluster.name" -> "elasticsearch"

  //**********Kafka配置参数**********
  params += "kafka.topic" -> "recommender"

}
