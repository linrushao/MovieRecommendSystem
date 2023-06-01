package com.linrushao.businessserver.service;

import com.linrushao.businessserver.entity.movieEntity.Recommendation;
import com.linrushao.businessserver.entity.movieEntity.UserRecommendation;
import com.linrushao.businessserver.utils.Constant;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author LinRuShao
 * @Date 2022/11/10 17:11
 */
@Service
public class RealTimeRecommendationService {
    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private MovieService movieService;

    //混合推荐
    public List<Recommendation> getHybridRecommendations(UserRecommendation request) {
        return findHybridRecommendations(request.getUid());
    }
    // 混合推荐算法【获取ALS离线电影推荐结果+获取当前用户的实时推荐】
    private List<Recommendation> findHybridRecommendations(int Uid) {
        List<Recommendation> hybridRecommendations = new ArrayList<>();

        //获取电影相似度集合【获取ALS离线电影推荐结果】
        List<Recommendation> cfRecs = findUserCFRecs(Uid);
        for (Recommendation recommendation : cfRecs) {
            hybridRecommendations.add(new Recommendation(recommendation.getMid(),
                    recommendation.getScore() * Constant.USERCF_RATING_FACTOR));
        }

        //获得实时推荐结果【获取当前用户的实时推荐 】
        List<Recommendation> streamRecs = findStreamRecs(Uid);
        for (Recommendation recommendation : streamRecs) {
            hybridRecommendations.add(new Recommendation(recommendation.getMid(), recommendation.getScore() * Constant.STREAM_RATING_FACTOR));
        }

        Collections.sort(hybridRecommendations, new Comparator<Recommendation>() {
            @Override
            public int compare(Recommendation o1, Recommendation o2) {
                return o1.getScore() > o2.getScore() ? -1 : 1;
            }
        });
        /**
         * 1，该方法返回的是父list的一个视图，从fromIndex（包含），
         * 到toIndex（不包含）。fromIndex=toIndex 表示子list为空
         * 2，父子list做的非结构性修改（non-structural changes）都会影响到彼此：
         * 所谓的“非结构性修改”，是指不涉及到list的大小改变的修改。相反，结构性修改，指改变了list大小的修改。
         * 3，对于结构性修改，子list的所有操作都会反映到父list上。但父list的修改将会导致返回的子list失效。
         */
        return hybridRecommendations.subList(0, hybridRecommendations.size());
    }

    // 协同过滤推荐【用户电影矩阵】【UserRecs表】【用户获取ALS算法中用户推荐矩阵】
    private List<Recommendation> findUserCFRecs(int uid) {
        MongoCollection<Document> movieRecsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_USER_RECS_COLLECTION);
        Document userRecs = movieRecsCollection.find(new Document("uid", uid)).first();
        return movieService.parseRecs(userRecs);
    }

    // 实时推荐结果【StreamRecs表】
    private List<Recommendation> findStreamRecs(int uid){
        MongoCollection<Document> streamRecsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_STREAM_RECS_COLLECTION);
        Document streamRecs = streamRecsCollection.find(new Document("uid", uid)).first();
        return movieService.parseRecs(streamRecs);
    }

    // 协同过滤推荐【用户电影矩阵】
    public List<Recommendation> getUserCFilteringRecommendations(UserRecommendation request) {
        return findUserCFRecs(request.getUid());
    }

}
