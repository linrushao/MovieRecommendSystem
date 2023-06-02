package com.linrushao.businessserver.service;

import com.linrushao.businessserver.entity.Rating;
import com.linrushao.businessserver.entity.User;
import com.linrushao.businessserver.entity.form.UserMovieRatingForm;
import com.mongodb.client.MongoCollection;
import org.bson.Document;


/**
 * @Author linrushao
 * @Date 2023-06-02
 */
public interface RatingService {

    //获取评分集合
    MongoCollection<Document> getRatingCollection();

    //评分电影集合转为对象
    Rating documentToRating(Document document);

    //电影评分，涉及到redis，先在redis上缓存，然后再持久化到mongodb中
    boolean movieRating(UserMovieRatingForm request);

    //redis更新
    void updateRedis(Rating rating);

    //用户新的评分
    boolean newRating(Rating rating);

    //查找用户uid对mid电影的评分是否存在
    boolean ratingExist(int uid, int mid);

    //更新评分
    boolean updateRating(Rating rating);

    //查找用户评分
    Rating findRating(int uid, int mid);

    //移除评分
    void removeRating(int uid, int mid);

    //获取用户的星星评分
    int[] getMyRatingStat(User user);


}
