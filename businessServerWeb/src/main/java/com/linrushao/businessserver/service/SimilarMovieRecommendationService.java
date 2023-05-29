package com.linrushao.businessserver.service;

import com.linrushao.businessserver.entity.movieEntity.MovieMidRecommendation;
import com.linrushao.businessserver.entity.movieEntity.Recommendation;
import com.linrushao.javamodel.Constant;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author LinRuShao
 * @Date 2022/11/10 17:48
 */
@Service
public class SimilarMovieRecommendationService {

    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private RestHighLevelClient esClient;
    @Autowired
    private MovieService movieService;

    //电影相似度【电影详细页面中的相似电影】【电影相似度矩阵+ES相似内容推荐】
    public List<Recommendation> getSimilarMovieRecommendations(MovieMidRecommendation request){
        List<Recommendation> SimilarMovieRecommendations = new ArrayList<>();
        // 获取基于内容推荐的推荐结果
        List<Recommendation> cbRecs = findContentBasedMoreLikeThisRecommendations(request.getMid());
        for (Recommendation recommendation : cbRecs) {
            SimilarMovieRecommendations.add(new Recommendation(recommendation.getMid(), recommendation.getScore() * Constant.ES_RATING_FACTOR));
        }

        //获取电影相似度集合【获取ALS离线电影推荐结果】
        List<Recommendation> cfRecs = findMovieCFRecs(request.getMid());
        for (Recommendation recommendation : cfRecs) {
            SimilarMovieRecommendations.add(new Recommendation(recommendation.getMid(),
                    recommendation.getScore() * Constant.MOVIE_RATING_FACTOR));
        }

        return SimilarMovieRecommendations.subList(0, SimilarMovieRecommendations.size());
    }

    // 基于内容的推荐算法[详细页面中的相似推荐]
    public List<Recommendation> getContentBasedMoreLikeThisRecommendations(MovieMidRecommendation request) {
        return findContentBasedMoreLikeThisRecommendations(request.getMid());
    }


    /**
     * 基于内容的推荐算法[moreLikeThisQuery实现基于内容的推荐]
     * 此查询查找与指定的文本，文档或文档集类似的文档
     * 基于内容的推荐通常是给定一篇文档信息
     * 然后给用户推荐与该文档相识的文档
     * Lucene的api中有实现查询文章相似度的接口
     * 叫MoreLikeThis。Elasticsearch封装了该接口
     * 通过Elasticsearch的More like this查询接口
     * 我们可以非常方便的实现基于内容的推荐。
     * @param mid
     * @return
     */
    private List<Recommendation> findContentBasedMoreLikeThisRecommendations(int mid) {
        // 创建搜索请求对象
        SearchRequest request = new SearchRequest(Constant.ELEASTICSEARCH_INDEX);
        // 构建查询的请求体
        SearchSourceBuilder searchBuilder = new SearchSourceBuilder();

        MoreLikeThisQueryBuilder moreLikeThisQueryBuilder = QueryBuilders.moreLikeThisQuery(new MoreLikeThisQueryBuilder.Item[]{new MoreLikeThisQueryBuilder.Item(Constant.ELEASTICSEARCH_INDEX, Constant.ELEASTICSEARCH_MOVIE_TYPE, String.valueOf(mid))});

        searchBuilder.query(moreLikeThisQueryBuilder);
        searchBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        request.source(searchBuilder);

        // 查询匹配
        SearchResponse response = null;
        try {
            response = esClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  elasticsearchService.parseESResponse(response);
    }

    // 协同过滤推荐【电影相似性】
    private List<Recommendation> findMovieCFRecs(int mid) {
        MongoCollection<Document> movieRecsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_MOVIE_RECS_COLLECTION);
        Document movieRecs = movieRecsCollection.find(new Document("mid", mid)).first();
        return movieService.parseRecs(movieRecs);
    }

    // 协同过滤推荐【电影相似性】
    public List<Recommendation> getCollaborativeFilteringRecommendations(MovieMidRecommendation request) {
        return findMovieCFRecs(request.getMid());
    }


}
