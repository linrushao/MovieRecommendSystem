package com.linrushao

/**
 * @Author LRS
 * @Date 2022/10/19 14:50
 *       Desc
 */
object Constant {
  /**************加载数据的路径******************/
  // [mid,name,actors,image,directors,douban_score,douban_votes,genres,language,timelong,regions,issue,descri,tags,shoot,actor_ids,director_ids]
  val ORIGINAL_MOVIE_DATA_PATH = "E:\\CODE\\JavaCODE\\50_MovieRecommendSystem\\recommender\\dataloader\\src\\main\\resources\\movies.txt"
  // [rating_id,userId,movieId,rating,timestamp]
  val ORIGINAL_RATING_DATA_PATH = "E:\\CODE\\JavaCODE\\50_MovieRecommendSystem\\recommender\\dataloader\\src\\main\\resources\\ratings.csv"

  /**************在MongoDB中的Collection名称【表】******************/
  val MOVIES_COLLECTION_NAME = "Movies"
  val RATINGS_COLLECTION_NAME = "Ratings"

  /**************在ElasticSearch中的Index名称******************/
  val ES_MOVIE_TYPE_NAME = "Movies"

}
