package com.linrushao.businessserver.service;

import com.linrushao.businessserver.entity.form.Recommendation;
import com.linrushao.businessserver.entity.form.MovieSearchForm;
import org.elasticsearch.action.search.SearchResponse;

import java.util.List;

/**
 * @Author linrushao
 * @Date 2023-06-02
 */
public interface ElasticsearchSearchService {

    //查询elasticsearch响应的数据，解析ES的查询响应
    List<Recommendation> parseESResponse(SearchResponse response);

    //基于内容查询电影
    List<Recommendation> getContentBasedSearchRecommendations(MovieSearchForm request);

    // 全文检索
    List<Recommendation> findContentBasedSearchRecommendations(String text);

}
