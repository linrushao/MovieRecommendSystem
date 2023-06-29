package com.linrushao.scalamodel


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
  params += "mongo.uri" -> "mongodb://localhost:27017/movierecommendsystem"
  params += "mongo.db" -> "movierecommendsystem"


  //**********ElasticSearch配置参数**********
  params += "es.httpHosts" -> "localhost:9200"
  params += "es.transportHosts" -> "localhost:9300"
  params += "es.index" -> "movierecommendsystemdata"
  params += "es.cluster.name" -> "es-cluster"

  //**********Kafka配置参数**********
  params += "kafka.topic" -> "recommender"

  //**********Zookeeper集群参数**********
  params += "bootstrap.servers" -> "master:9092"
  params += "zookeeper.connect" -> "master:2181"
}
