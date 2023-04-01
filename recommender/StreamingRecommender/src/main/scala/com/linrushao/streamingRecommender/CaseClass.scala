package com.linrushao.streamingRecommender

/**
 * @Author LRS
 * @Date 2022/10/19 17:51
 *       Desc
 */

/**
 * MongoDB 配置对象
 * @param uri MongoDB连接地址
 * @param db  操作的MongoDB数据库
 */
case class MongoConfig(uri: String, db: String)

// 定义一个基准推荐对象
case class Recommendation(mid: Int, score: Double)

// 定义基于预测评分的用户推荐列表
case class UserRecs(uid: Int, recs: Seq[Recommendation])

// 定义基于LFM电影特征向量的电影相似度列表
case class MovieRecs(mid: Int, recs: Seq[Recommendation])
