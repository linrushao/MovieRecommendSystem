package com.linrushao.businessserver.service;

import com.linrushao.businessserver.entity.movieEntity.Recommendation;
import com.linrushao.businessserver.entity.movieEntity.SearchRecommendation;
import com.linrushao.businessserver.entity.movieEntity.TopGenresRecommendation;
import com.linrushao.javamodel.Constant;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Author LinRuShao
 * @Date 2022/11/10 17:21
 */
@Service
public class MovieGenresRecommendationService {
    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private RestHighLevelClient esClient;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private MovieService movieService;

    //基于电影类别推荐
    public List<Recommendation> getContentBasedGenresRecommendations(SearchRecommendation request) {
        // 创建搜索请求对象
        SearchRequest request1 = new SearchRequest();
        request1.indices(Constant.ELEASTICSEARCH_INDEX);
        // 构建查询的请求体
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(QueryBuilders.matchQuery("genres", request.getText()).fuzziness(Fuzziness.AUTO)).size(Constant.ES_MOVIES_ITEM_SIZE);
        request1.source(searchSourceBuilder);
        // 查询匹配
        SearchResponse response = null;
        try {
            response = esClient.search(request1, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  elasticsearchService.parseESResponse(response);
    }

    /**
     * 冷启动问题，用户刚注册时获取填写的类别，选择一种类别进行电影的推荐
     */
    public List<Recommendation> getTopGenresRecommendations(TopGenresRecommendation request){
        Document genresTopMovies = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_GENRES_TOP_MOVIES_COLLECTION)
                .find(Filters.eq("genres",request.getGenres())).first();
        return movieService.parseRecs(genresTopMovies);
    }
}
