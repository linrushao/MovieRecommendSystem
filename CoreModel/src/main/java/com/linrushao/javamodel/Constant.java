package com.linrushao.javamodel;

/**
 * @Author LRS
 * @Date 2022/9/20 19:10
 * Desc 常量设置
 */
public class Constant {

    /**************加载数据的路径******************/
    // [mid,name,actors,image,directors,douban_score,douban_votes,genres,language,timelong,regions,issue,descri,tags,shoot,actor_ids,director_ids]
    public static final String ORIGINAL_MOVIE_DATA_PATH = "D:\\CODE\\JavaCODE\\MovieRecommendSystem\\recommender\\dataloader\\src\\main\\resources\\movies.txt";

    // [rating_id,userId,movieId,rating,timestamp]
    public static final String ORIGINAL_RATING_DATA_PATH = "D:\\CODE\\JavaCODE\\MovieRecommendSystem\\recommender\\dataloader\\src\\main\\resources\\ratings.txt";

    //************** MONGODB 表集合【表】****************
    //电影表
    public static final String MONGODB_MOVIE_COLLECTION = "Movies";

    //评分表
    public static final String MONGODB_RATING_COLLECTION = "Ratings";

    //电影的平均评分表
    public static final String MONGODB_AVERAGE_MOVIES_SCORE_COLLECTION = "AverageMovies";

    //电影的相似度矩阵
    public static final String MONGODB_MOVIE_RECS_COLLECTION = "MovieRecommends";

    //优质电影表
    public static final String MONGODB_RATE_MORE_MOVIES_COLLECTION = "RateMoreMovies";

    //最热电影表
    public static final String MONGODB_RATE_MORE_MOVIES_RECENTLY_COLLECTION = "RateMoreRecentlyMovies";

    //实时推荐电影表
    public static final String MONGODB_STREAM_RECS_COLLECTION = "StreamRecommends";

    //用户的推荐矩阵
    public static final String MONGODB_USER_RECS_COLLECTION = "UserRecommends";

    //电影类别表
    public static final String MONGODB_GENRES_TOP_MOVIES_COLLECTION = "GenresTopMovies";

    //************** ELEASTICSEARCH ****************
    //ES使用的type 在ElasticSearch中的Index名称
    public static final String ELEASTICSEARCH_MOVIE_TYPE = "Movies";

    //************** FOR MOVIE RATING ******************
    // 日志前缀
    public static final String MOVIE_RATING_PREFIX = "MOVIE_RATING_PREFIX";

    //*************** MOVIES 推荐数目 ****************
    // 最大推荐数目
    public static final int MAX_RECOMMENDATIONS = 200;

    //获取redis用户最近的K次评分
    public static final int MAX_USER_RATINGS_NUM = 100;

    //从相似度矩阵中取出当前电影最相似的N个电影
    public static final int MAX_SIM_MOVIES_NUM = 100;
}
