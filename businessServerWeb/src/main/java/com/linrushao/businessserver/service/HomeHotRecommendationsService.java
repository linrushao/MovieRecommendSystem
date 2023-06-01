package com.linrushao.businessserver.service;

import com.linrushao.businessserver.entity.movieEntity.Recommendation;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.linrushao.businessserver.utils.Constant.*;

/**
 * @Author LinRuShao
 * @Date 2022/10/22 15:26
 */
@Service
public class HomeHotRecommendationsService {
    @Autowired
    private MongoClient mongoClient;

    /**
     * 获取热门电影（热门推荐）
     * @return
     */
    public List<Recommendation> getHotRecommendations() {
        // 获取热门电影的条目
        MongoCollection<Document> rateMoreMoviesRecentlyCollection = mongoClient.getDatabase(MONGODB_DATABASE).getCollection(MONGODB_RATE_MORE_MOVIES_RECENTLY_COLLECTION);
        FindIterable<Document> documents = rateMoreMoviesRecentlyCollection.find().sort(Sorts.descending("yeahmonth")).limit(REDIS_MOVIE_RATING_QUEUE_SIZE);
        List<Recommendation> recommendations = new ArrayList<>();
        for (Document document : documents) {
            recommendations.add(new Recommendation(document.getInteger("mid"), 0D));
        }
        return recommendations;
    }
}
