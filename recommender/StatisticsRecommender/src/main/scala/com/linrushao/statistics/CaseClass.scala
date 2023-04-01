package com.linrushao.statistics

/**
 * @Author LRS
 * @Date 2022/10/16 17:06
 *       Desc
 */

/**
 * 定义样例类
 *
 * @param mid       电影ID，mid
 * @param name      电影名称，name
 * @param descri    详情描述，descri
 * @param timelong  时长，timelong
 * @param issue     发行时间，issue
 * @param shoot     拍摄时间，shoot
 * @param language  语言，language
 * @param genres    类型，genres
 * @param actors    演员表，actors
 * @param directors 导演，directors
 */
case class Movies(mid: Int, name: String, descri: String, timelong: String, issue: String,
                  shoot: String, language: String, genres: String, actors: String, directors: String)

/**
 * Rating数据集
 *
 * 1,31,2.5,1260759144
 *
 * @param uid
 * @param mid
 * @param score
 * @param timestamp
 */
case class Ratings(uid: Int, mid: Int, score: Double, timestamp: Long)

// 定义一个基准推荐对象
case class Recommendation(mid: Int, score: Double)

// 定义电影类别top10推荐对象
case class GenresRecommendation(genres: String, recs: Seq[Recommendation])

/**
 * MongoDB的连接配置
 *
 * @param uri MongoDB的连接
 * @param db  MongoDB要操作数据库
 */
case class MongoConfig(uri: String, db: String)

