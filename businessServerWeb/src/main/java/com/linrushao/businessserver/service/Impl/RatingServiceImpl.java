package com.linrushao.businessserver.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linrushao.businessserver.entity.Rating;
import com.linrushao.businessserver.entity.User;
import com.linrushao.businessserver.entity.form.UserMovieRatingForm;
import com.linrushao.businessserver.service.RatingService;
import com.linrushao.businessserver.utils.Constant;
import com.linrushao.businessserver.utils.RedisUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Author linrushao
 * @Date 2023-06-02
 */
@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisUtil redisUtil;

    private MongoCollection<Document> ratingCollection;

    /**
     * 获取评分集合
     *
     * @return
     */
    @Override
    public MongoCollection<Document> getRatingCollection() {
        if (null == ratingCollection)
            ratingCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_RATING_COLLECTION);
        return ratingCollection;
    }

    /**
     * 评分电影集合转为对象
     *
     * @param document
     * @return
     */
    @Override
    public Rating documentToRating(Document document) {
        Rating rating = null;
        try {
            rating = objectMapper.readValue(JSON.serialize(document), Rating.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rating;

    }

    /**
     * 电影评分，涉及到redis，先在redis上缓存，然后再持久化到mongodb中
     *
     * @param request
     * @return
     */
    @Override
    public boolean movieRating(UserMovieRatingForm request) {
        Rating rating = new Rating(request.getUid(), request.getMid(), request.getScore());
        updateRedis(rating);
        if (ratingExist(rating.getUid(), rating.getMid())) {
            return updateRating(rating);
        } else {
            return newRating(rating);
        }
    }

    /**
     * redis更新
     *
     * @param rating
     * @return
     */
    @Override
    public void updateRedis(Rating rating) {
        if (redisUtil.exists("uid:" + rating.getUid()) && redisUtil.lLen("uid:" + rating.getUid()) >= Constant.REDIS_MOVIE_RATING_QUEUE_SIZE) {
            redisUtil.del("uid:" + rating.getUid());
        }
        redisUtil.lPush("uid:" + rating.getUid(), rating.getMid() + ":" + rating.getScore());
    }

    /**
     * 用户新的评分
     *
     * @param rating
     * @return
     */
    @Override
    public boolean newRating(Rating rating) {
        try {
            getRatingCollection().insertOne(Document.parse(objectMapper.writeValueAsString(rating)));
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查找用户uid对mid电影的评分是否存在
     *
     * @param uid
     * @param mid
     * @return
     */
    @Override
    public boolean ratingExist(int uid, int mid) {
        return null != findRating(uid, mid);
    }

    /**
     * 更新评分
     *
     * @param rating
     * @return
     */
    @Override
    public boolean updateRating(Rating rating) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("uid", rating.getUid());
        basicDBObject.append("mid", rating.getMid());
        getRatingCollection().updateOne(basicDBObject,
                new Document().append("$set", new Document("score", rating.getScore())));
        return true;
    }

    /**
     * 查找用户评分
     *
     * @param uid 用户ID
     * @param mid 电影ID
     * @return
     */
    @Override
    public Rating findRating(int uid, int mid) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("uid", uid);
        basicDBObject.append("mid", mid);
        FindIterable<Document> documents = getRatingCollection().find(basicDBObject);
        if (documents.first() == null)
            return null;
        return documentToRating(documents.first());
    }

    /**
     * 移除评分
     *
     * @param uid 用户ID
     * @param mid 电影ID
     */
    @Override
    public void removeRating(int uid, int mid) {
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("uid", uid);
        basicDBObject.append("mid", mid);
        getRatingCollection().deleteOne(basicDBObject);
    }

    /**
     * 获取用户的星星评分
     *
     * @param user
     * @return
     */
    @Override
    public int[] getMyRatingStat(User user) {
        FindIterable<Document> documents = getRatingCollection().find(new Document("uid", user.getUid()));
        int[] stats = new int[10];
        for (Document document : documents) {
            Rating rating = documentToRating(document);
            Long index = Math.round(rating.getScore() / 0.5);
            stats[index.intValue()] = stats[index.intValue()] + 1;
        }
        return stats;
    }

}
