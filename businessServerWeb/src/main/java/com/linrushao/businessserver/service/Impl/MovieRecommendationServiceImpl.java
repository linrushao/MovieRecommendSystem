package com.linrushao.businessserver.service.Impl;

import com.linrushao.businessserver.entity.form.*;
import com.linrushao.businessserver.service.ElasticsearchSearchService;
import com.linrushao.businessserver.service.MovieRecommendationService;
import com.linrushao.businessserver.service.MovieService;
import com.linrushao.businessserver.utils.Constant;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.linrushao.businessserver.utils.Constant.*;

/**
 * @Author linrushao
 * @Date 2023-06-02
 */
@Service
public class MovieRecommendationServiceImpl implements MovieRecommendationService {

    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private RestHighLevelClient esClient;
    @Autowired
    private ElasticsearchSearchService elasticsearchSearchService;
    @Autowired
    private MovieService movieService;

    /**
     * 获取热门电影（热门推荐）
     * @return
     */
    @Override
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

    /**
     * 获取评分数最多的电影
     * @return
     */
    @Override
    public List<Recommendation> getRateMoreRecommendations() {
        // 获取评分最多电影的条目
        MongoCollection<Document> rateMoreMoviesCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_RATE_MORE_MOVIES_COLLECTION);
        FindIterable<Document> documents = rateMoreMoviesCollection.find().sort(Sorts.descending("count")).limit(Constant.HOME_MOVIES_ITEM_SIZE);
        List<Recommendation> recommendations = new ArrayList<>();
        for (Document document : documents) {
            recommendations.add(new Recommendation(document.getInteger("mid"), 0D));
        }
        return recommendations;
    }

    /**
     * 基于电影类别推荐
     * @param request
     * @return
     */
    @Override
    public List<Recommendation> getContentBasedGenresRecommendations(MovieSearchForm request) {
        // 创建搜索请求对象
        SearchRequest request1 = new SearchRequest();
        request1.indices(ELEASTICSEARCH_INDEX);
        // 构建查询的请求体
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(QueryBuilders.matchQuery("genres", request.getText()).fuzziness(Fuzziness.AUTO)).size(ES_MOVIES_ITEM_SIZE);
        request1.source(searchSourceBuilder);
        // 查询匹配
        SearchResponse response = null;
        try {
            response = esClient.search(request1, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  elasticsearchSearchService.parseESResponse(response);
    }

    /**
     * 冷启动问题，用户刚注册时获取填写的类别，选择一种类别进行电影的推荐
     * @param request
     * @return
     */
    @Override
    public List<Recommendation> getTopGenresRecommendations(MovieGenresForm request) {
        Document genresTopMovies = mongoClient.getDatabase(MONGODB_DATABASE).getCollection(MONGODB_GENRES_TOP_MOVIES_COLLECTION)
                .find(Filters.eq("genres",request.getGenres())).first();
        return movieService.parseRecs(genresTopMovies);
    }

    /**
     * 混合推荐
     * @param request
     * @return
     */
    @Override
    public List<Recommendation> getHybridRecommendations(UserRecommendationForm request) {
        return findHybridRecommendations(request.getUid());
    }

    /**
     * 混合推荐算法【获取ALS离线电影推荐结果+获取当前用户的实时推荐】
     * @param Uid
     * @return
     */
    @Override
    public List<Recommendation> findHybridRecommendations(int Uid) {
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

    /**
     * 协同过滤推荐【用户电影矩阵】【UserRecs表】【用户获取ALS算法中用户推荐矩阵】
     * @param uid
     * @return
     */
    @Override
    public List<Recommendation> findUserCFRecs(int uid) {
        MongoCollection<Document> movieRecsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_USER_RECS_COLLECTION);
        Document userRecs = movieRecsCollection.find(new Document("uid", uid)).first();
        return movieService.parseRecs(userRecs);
    }

    /**
     * 实时推荐结果【StreamRecs表】
     * @param uid
     * @return
     */
    @Override
    public List<Recommendation> findStreamRecs(int uid) {
        MongoCollection<Document> streamRecsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_STREAM_RECS_COLLECTION);
        Document streamRecs = streamRecsCollection.find(new Document("uid", uid)).first();
        return movieService.parseRecs(streamRecs);
    }

    /**
     * 协同过滤推荐【用户电影矩阵】
     * @param request
     * @return
     */
    @Override
    public List<Recommendation> getUserCFilteringRecommendations(UserRecommendationForm request) {
        return findUserCFRecs(request.getUid());
    }

    /**
     * 电影相似度【电影详细页面中的相似电影】【电影相似度矩阵+ES相似内容推荐】
     * @param request
     * @return
     */
    @Override
    public List<Recommendation> getSimilarMovieRecommendations(MovieMidRecommendation request) {
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

    /**
     * 基于内容的推荐算法[详细页面中的相似推荐]
     * @param request
     * @return
     */
    @Override
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
    @Override
    public List<Recommendation> findContentBasedMoreLikeThisRecommendations(int mid) {
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
        return  elasticsearchSearchService.parseESResponse(response);
    }

    /**
     * 协同过滤推荐【电影相似性】
     * @param mid
     * @return
     */
    @Override
    public List<Recommendation> findMovieCFRecs(int mid) {
        MongoCollection<Document> movieRecsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_MOVIE_RECS_COLLECTION);
        Document movieRecs = movieRecsCollection.find(new Document("mid", mid)).first();
        return movieService.parseRecs(movieRecs);
    }

    /**
     * 协同过滤推荐【电影相似性】
     * @param request
     * @return
     */
    @Override
    public List<Recommendation> getCollaborativeFilteringRecommendations(MovieMidRecommendation request) {
        return findMovieCFRecs(request.getMid());
    }


}
