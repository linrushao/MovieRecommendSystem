package com.linrushao.statistics

/**
 * @Author LRS
 * @Date 2022/10/16 17:11
 *       Desc
 */
object Constant {

  /**************加载数据表的表名******************/
  val MONGODB_MOVIE_COLLECTION = "Movies"
  val MONGODB_RATING_COLLECTION = "Ratings"

  /**************统计的表的名称******************/
  //评分最多
  val RATE_MORE_MOVIES = "RateMoreMovies"
  //近期热门统计
  val RATE_MORE_RECENTLY_MOVIES = "RateMoreRecentlyMovies"
  //优质电影统计，统计电影的平均评分
  val AVERAGE_MOVIES = "AverageMovies"
  //电影Top
  val GENRES_TOP_MOVIES = "GenresTopMovies"

  /**************电影的每个类别genres中设置的Top条数******************/
  val MOVIE_GENRES_TOP = 30


}
