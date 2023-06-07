package com.linrushao.businessserver.utils;

/**
 * @Author LRS
 * @Date 2022/9/20 19:10
 * Desc 常量设置
 */
public class Constant {
    //**************拦截器中的图片路径****************
    public static final String INTERCEPTIR_IMAGES_PATH="file:E:/movieSystemImages/imagesData/";
    //************** MONGODB 表集合【表】****************
    //（英文电影）数据库名
    public static final String MONGODB_DATABASE = "movierecommendsystem";

    //用户表
    public static final String MONGODB_USER_COLLECTION= "Users";

    //电影表
    public static final String MONGODB_MOVIE_COLLECTION = "Movies";

    //评分表
    public static final String MONGODB_RATING_COLLECTION = "Ratings";

    //标签表
    public static final String MONGODB_TAG_COLLECTION = "Tag";

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
    //ES使用的index
    public static final String ELEASTICSEARCH_INDEX = "movierecommendsystemdata";

    //ES使用的type 在ElasticSearch中的Index名称
    public static final String ELEASTICSEARCH_MOVIE_TYPE = "Movies";

    //************** FOR MOVIE RATING ******************
    // 日志前缀
    public static final String MOVIE_RATING_PREFIX = "MOVIE_RATING_PREFIX";

    //*************** Redis 评分队列大小 ****************
    public static final int REDIS_MOVIE_RATING_QUEUE_SIZE = 50;

    //*************** MOVIES 推荐数目 ****************
    // 首页电影
    public static final int HOME_MOVIES_ITEM_SIZE = 100;

    //*************** ES 查询条数 ****************

    public static final int ES_MOVIES_ITEM_SIZE = 100;

    //*************** MOVIES 推荐数目结果占比【混合推荐中CF的比例】****************
    //基于ES的内容结果的占比【ES中相似度推荐】
    public static final Double ES_RATING_FACTOR = 0.5;

    //基于movie的内容结果的占比【MovieRecs表】
    public static final Double MOVIE_RATING_FACTOR = 0.5;

    //基于实时推荐结果占比【StreamRecs表】
    public static final Double STREAM_RATING_FACTOR = 0.7;

    //基于ALS的用户离线结果推荐的占比【UserRecs表】
    public static final Double USERCF_RATING_FACTOR = 0.3;

}
