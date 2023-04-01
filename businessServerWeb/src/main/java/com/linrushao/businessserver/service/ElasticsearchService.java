package com.linrushao.businessserver.service;

import com.linrushao.businessserver.entity.movieEntity.Recommendation;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author LinRuShao
 * @Date 2022/11/10 16:37
 */
@Service
public class ElasticsearchService {

    //查询elasticsearch响应的数据，解析ES的查询响应
    public List<Recommendation> parseESResponse(SearchResponse response) {
        List<Recommendation> recommendations = new ArrayList<>();
        //getHits表示获取到了ES中的所有的数据，包括多个电影的信息，是一个大的集合{}，集合中又有小的[]。
        for (SearchHit hit : response.getHits()) {
            recommendations.add(new Recommendation((int) hit.getSourceAsMap().get("mid"), (double) hit.getScore()));
        }
        return recommendations;
    }
}
