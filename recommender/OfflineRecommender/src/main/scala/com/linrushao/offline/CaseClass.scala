package com.linrushao.offline

/**
 * @Author LRS
 * @Date 2022/9/13 16:29
 *       Desc
 */



/**
 * Movie Class 电影类
 * @param mid             电影的ID
 * @param name            电影的名称
 * @param actors          电影的演员
 * @param image           电影封面
 * @param directors       电影的导演
 * @param douban_score    豆瓣评分
 * @param douban_votes    豆瓣投票数
 * @param genres          电影类别
 * @param language        电影语言
 * @param timelong        电影片长
 * @param regions         制片国家/地区
 * @param issue           上映日期
 * @param descri          电影的描述
 * @param tags            电影标签
 * @param shoot           年份
 * @param actor_ids       演员与PERSON_ID的对应关系,多个演员采用“\|”符号分割，格式“演员A:ID\|演员
 * @param director_ids    导演与PERSON_ID的对应关系,多个导演采用“\|”符号分割，格式“导演A:ID\|导演B:ID”；
 */
case class Movies(mid: Int,  name: String, actors:String,image:String,directors:String
                  , douban_score:Double,douban_votes:Int,genres:String,language:String
                  ,timelong:String,regions:String,issue:String,descri: String, tags:String
                  , shoot:Int,  actor_ids: String,  director_ids: String)

/**
 * 基于LFM的离线推荐模块
 * Rating Class 电影的评分类
 * @param rating_id 评分ID
 * @param user_md5  用户的加密ID
 * @param uid       用户的ID
 * @param mid       电影的ID
 * @param score     用户为该电影的评分
 * @param timestamp 用户为该电影评分的时间
 */
case class Ratings( rating_id:Int,user_md5: String,uid:Int,  mid: Int,  score: Double,  timestamp: String)

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

/**
 * MongoDB 配置对象
 * @param uri MongoDB连接地址
 * @param db  操作的MongoDB数据库
 */
case class MongoConfig(uri:String, db:String)

// 定义基于预测评分的用户推荐列表
case class UserRecs( uid: Int, recs: Seq[Recommendation] )

// 定义基于LFM电影特征向量的电影相似度列表
case class MovieRecs( mid: Int, recs: Seq[Recommendation] )
