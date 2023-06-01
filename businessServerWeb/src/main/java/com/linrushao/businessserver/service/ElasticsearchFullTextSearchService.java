package com.linrushao.businessserver.service;

import com.linrushao.businessserver.entity.movieEntity.Recommendation;
import com.linrushao.businessserver.entity.movieEntity.SearchRecommendation;
import com.linrushao.businessserver.utils.Constant;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Author LinRuShao
 * @Date 2022/11/10 16:50
 */
@Service
public class ElasticsearchFullTextSearchService {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private RestHighLevelClient esClient;

    /**
     * 基于内容查询电影
     * @param request
     * @return
     */
    public List<Recommendation> getContentBasedSearchRecommendations(SearchRecommendation request) {
        return findContentBasedSearchRecommendations(request.getText());
    }

    // 全文检索
    private List<Recommendation> findContentBasedSearchRecommendations(String text) {
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
        return elasticsearchService.parseESResponse(response);
    }

}
