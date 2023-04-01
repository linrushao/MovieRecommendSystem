package com.linrushao

/**
 * @Author LRS
 * @Date 2022/10/19 14:48
 *       Desc
 */
/**
 * MongoDB 配置对象
 * @param uri MongoDB连接地址
 * @param db  操作的MongoDB数据库
 */
case class MongoConfig(uri: String, db: String)

/**
 * Movie Class 电影类
 * @param mid       电影的ID
 * @param name      电影的名称
 * @param descri    电影的描述
 * @param timelong  电影的时长
 * @param issue     电影的发行时间
 * @param shoot     电影的拍摄时间
 * @param language  电影的语言
 * @param genres    电影的类别
 * @param actors    电影的演员
 * @param directors 电影的导演
 */
case class Movie(mid: Int,  name: String,  descri: String,  timelong: String,  issue: String,  shoot: String,  language: String,  genres: String,  actors: String,  directors: String)

/**
 * Tag Class  电影标签类
 * @param uid       用户的ID
 * @param mid       电影的ID
 * @param tag       用户为该电影打的标签
 * @param timestamp 用户为该电影打标签的时间
 */
case class Tag( uid: Int,  mid: Int,  tag: String,  timestamp: Int)

/**
 * Rating Class 电影的评分类
 * @param uid       用户的ID
 * @param mid       电影的ID
 * @param score     用户为该电影的评分
 * @param timestamp 用户为该电影评分的时间
 */
case class Rating( uid: Int,  mid: Int,  score: Double,  timestamp: Int)
/**
 *
 * @param httpHosts      http主机列表，逗号分隔
 * @param transportHosts transport主机列表
 * @param index          需要操作的索引
 * @param clustername    集群名称，默认elasticsearch
 */
case class ESConfig(httpHosts: String, transportHosts: String, index: String, clustername: String)

