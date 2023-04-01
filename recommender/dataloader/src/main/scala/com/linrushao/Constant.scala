package com.linrushao

/**
 * @Author LRS
 * @Date 2022/10/19 14:50
 *       Desc
 */
object Constant {
  /**************加载数据的路径******************/
  // [mid,name,descri,timelong,issue,shoot,language,genres,actors,directors]
  val ORIGINAL_MOVIE_DATA_PATH = "D:\\CODE\\JavaCODE\\45_MovieRecommend\\recommender\\dataloader\\src\\main\\resources\\movies.csv"
  // [userId,movieId,rating,timestamp]
  val ORIGINAL_RATING_DATA_PATH = "D:\\CODE\\JavaCODE\\45_MovieRecommend\\recommender\\dataloader\\src\\main\\resources\\ratings.csv"
  // [userId,movieId,tag,timestamp]
  val ORIGINAL_TAG_DATA_PATH = "D:\\CODE\\JavaCODE\\45_MovieRecommend\\recommender\\dataloader\\src\\main\\resources\\tags.csv"

  /**************在MongoDB中的Collection名称【表】******************/
  val MOVIES_COLLECTION_NAME = "Movie"
  val RATINGS_COLLECTION_NAME = "Rating"
  val TAGS_COLLECTION_NAME = "Tag"

  /**************在ElasticSearch中的Index名称******************/
  val ES_MOVIE_TYPE_NAME = "Movie"

}
