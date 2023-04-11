package com.linrushao.streamingRecommender

/**
 * @Author LRS
 * @Date 2022/10/19 14:50
 *       Desc
 */
object Constant {

  /**************集合名称（表）******************/
  val MONGODB_STREAM_RECS_COLLECTION = "StreamRecommends"
  val MONGODB_RATING_COLLECTION = "Ratings"
  val MONGODB_MOVIE_RECS_COLLECTION = "MovieRecommends"

  /**************最大推荐数目******************/
  val MAX_USER_RATINGS_NUM = 100
  val MAX_SIM_MOVIES_NUM = 100

}
