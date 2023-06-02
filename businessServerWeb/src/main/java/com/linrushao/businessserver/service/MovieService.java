package com.linrushao.businessserver.service;

import com.linrushao.businessserver.entity.Movie;
import com.linrushao.businessserver.entity.Rating;
import com.linrushao.businessserver.entity.form.Recommendation;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.List;

/**
 * @Author linrushao
 * @Date 2023-06-02
 */
public interface MovieService {
    //获取电影的相关信息【Movie】集合
    MongoCollection<Document> getMovieCollection();

    //平均评分电影集合【AverageMovies】集合
    MongoCollection<Document> getAverageMoviesScoreCollection();

    //评分数据集合【Rating】集合
    MongoCollection<Document> getRateCollection();

    //获取推荐电影
    List<Movie> getRecommendeMovies(List<Recommendation> recommendations);

    //获取混合推荐电影
    List<Movie> getHybirdRecommendeMovies(List<Recommendation> recommendations);

    //根据电影的mid查找电影
    List<Movie> getMovies(List<Integer> mids);

    //集合文档转换为Movie对象
    Movie documentToMovie(Document document);

    //集合文档转换为评分对象
    Rating documentToRating(Document document);

    //查看电影是否存在
    boolean movieExist(int mid);

    //根据电影mid查询电影内容
    Movie findByMID(int mid);

    //移除电影
    void removeMovie(int mid);

    //获取用户评分的电影
    List<Movie> getMyRateMovies(int uid);

    //获取新添加的电影
    List<Movie> getNewMovies();

    //获取mongdb表中内嵌的Recs中的数据，解析Document
    List<Recommendation> parseRecs(Document document);

}
