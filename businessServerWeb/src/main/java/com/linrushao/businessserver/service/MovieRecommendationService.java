package com.linrushao.businessserver.service;

import com.linrushao.businessserver.entity.form.*;

import java.util.List;

/**
 * @Author linrushao
 * @Date 2023-06-02
 */
public interface MovieRecommendationService {

    //获取热门电影（热门推荐）
    List<Recommendation> getHotRecommendations();

    //获取评分数最多的电影
    List<Recommendation> getRateMoreRecommendations();

    //基于电影类别推荐
    List<Recommendation> getContentBasedGenresRecommendations(MovieSearchForm request);

    //冷启动问题，用户刚注册时获取填写的类别，选择一种类别进行电影的推荐
    List<Recommendation> getTopGenresRecommendations(MovieGenresForm request);

    // 混合推荐算法【获取ALS离线电影推荐结果+获取当前用户的实时推荐】
    List<Recommendation> getHybridRecommendations(int Uid);

    //协同过滤推荐【用户电影矩阵】【UserRecs表】【用户获取ALS算法中用户推荐矩阵】
    List<Recommendation> getUserCFRecommdations(int uid);

    //实时推荐结果【StreamRecs表】
    List<Recommendation> findStreamRecs(int uid);

    //电影相似度【电影详细页面中的相似电影】【电影相似度矩阵+ES相似内容推荐】
    List<Recommendation> getSimilarMovieRecommendations(MovieMidRecommendation request);

    // 基于内容的推荐算法[详细页面中的相似推荐]
    List<Recommendation> getContentBasedMoreLikeThisRecommendations(int mid);

    // 协同过滤推荐【电影相似性】
    List<Recommendation> findMovieCFRecs(int mid);
}
