package com.linrushao.businessserver.service.Impl;

import com.linrushao.businessserver.entity.form.Recommendation;
import com.linrushao.businessserver.entity.form.MovieSearchForm;
import com.linrushao.businessserver.service.ElasticsearchSearchService;
import com.linrushao.businessserver.utils.Constant;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author linrushao
 * @Date 2023-06-02
 */
@Service
public class ElasticsearchSearchServiceImpl implements ElasticsearchSearchService {
    @Autowired
    private ElasticsearchSearchService elasticsearchSearchService;
    @Autowired
    private RestHighLevelClient esClient;

    /**
     * 查询elasticsearch响应的数据，解析ES的查询响应
     * @param response 请求的参数
     * @return
     */
    @Override
    public List<Recommendation> parseESResponse(SearchResponse response) {
        List<Recommendation> recommendations = new ArrayList<>();
        //getHits表示获取到了ES中的所有的数据，包括多个电影的信息，是一个大的集合{}，集合中又有小的[]。
        for (SearchHit hit : response.getHits()) {
            recommendations.add(new Recommendation((int) hit.getSourceAsMap().get("mid"), (double) hit.getScore()));
        }
        return recommendations;
    }

    /**
     * 基于内容查询电影
     * @param request
     * @return
     */
    @Override
    public List<Recommendation> getContentBasedSearchRecommendations(MovieSearchForm request) {
        return findContentBasedSearchRecommendations(request.getText());
    }

    /**
     * 全文检索
     * @param text 需要检索的内容
     * @return
     */
    @Override
    public List<Recommendation> findContentBasedSearchRecommendations(String text) {
        // 创建搜索请求对象
        SearchRequest request = new SearchRequest();
        request.indices(Constant.ELEASTICSEARCH_INDEX);
        //先进行精确匹配，如果没有就模糊匹配，使用了ik分词器，所以在模糊匹配时会进行分词操作
        MatchPhraseQueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("name",text);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(queryBuilder)
                .size(Constant.HOME_MOVIES_ITEM_SIZE);
        request.source(searchSourceBuilder);

        // 构建查询的请求体,multiMatchQuery模糊匹配
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
//                .query(QueryBuilders.multiMatchQuery(text, "name","actors").analyzer("ik_smart")).size(Constant.ES_MOVIES_ITEM_SIZE);
//        request.source(searchSourceBuilder);
        // 查询匹配
        SearchResponse response = null;
        try {
            response = esClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return elasticsearchSearchService.parseESResponse(response);
    }
}
