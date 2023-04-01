package com.linrushao.offline

/**
 * @Author LRS
 * @Date 2022/9/13 16:29
 *       Desc
 */
/**
 * Movie数据集，数据集字段通过分割
 * 151^                          电影的ID
 * Rob Roy (1995)^               电影的名称
 * In the highlands ....^        电影的描述
 * 139 minutes^                  电影的时长
 * August 26, 1997^              电影的发行日期
 * 1995^                         电影的拍摄日期
 * English ^                     电影的语言
 * Action|Drama|Romance|War ^    电影的类型
 * Liam Neeson|Jessica Lange...  电影的演员
 * Michael Caton-Jones           电影的导演
 * tag1|tag2|tag3|....           电影的Tag
 **/

case class Movie( mid: Int,  name: String,  descri: String,  timelong: String,  issue: String,
                  shoot: String,  language: String,  genres: String,  actors: String,  directors: String)

/**
 * Rating数据集，用户对于电影的评分数据集，用，分割
 *
 * 1,           用户的ID
 * 31,          电影的ID
 * 2.5,         用户对于电影的评分
 * 1260759144   用户对于电影评分的时间
 */
/**
 *  基于LFM的离线推荐模块
 * @param uid
 * @param mid
 * @param score
 * @param timestamp
 */
case class MovieRating( uid: Int,  mid: Int,  score: Double,  timestamp: Int)

/**
 * 推荐项目
 * @param mid  项目ID
 * @param score    推荐分数
 */
case class Recommendation(mid: Int, score: Double)

/**
 * 电影相似推荐
 * @param mid 电影ID
 * @param recs 相似的电影集合
 */
case class MovieRecommendation(mid: Int, recs: Seq[Recommendation])

/**
 * 用户的电影推荐
 * @param uid 用户ID
 * @param recs 用户的推荐电影集合
 */
case class UserRecommendation(uid: Int, recs: Seq[Recommendation])

case class MongoConfig(uri:String, db:String)

// 定义基于预测评分的用户推荐列表
case class UserRecs( uid: Int, recs: Seq[Recommendation] )

// 定义基于LFM电影特征向量的电影相似度列表
case class MovieRecs( mid: Int, recs: Seq[Recommendation] )
