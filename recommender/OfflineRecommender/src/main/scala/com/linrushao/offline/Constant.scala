package com.linrushao.offline

/**
 * @Author LRS
 * @Date 2022/10/16 17:11
 *       Desc 常量定义
 */
object Constant {
  /**************加载数据表的表名******************/
  val RATING_COLLECTION_NAME = "Ratings"
  val MOVIES_COLLECTION_NAME = "Movies"

  /**************统计的表的名称******************/
  val USER_RECS_COLLECTION_NAME = "UserRecommends"
  val MOVIE_RECS_COLLECTION_NAME = "MovieRecommends"

  /**************电影的最多推荐数目******************/
  val MAX_RATING = 5.0F
  val MAX_RECOMMENDATIONS = 200
}
