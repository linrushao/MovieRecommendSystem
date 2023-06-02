package com.linrushao.businessserver.service;

import com.linrushao.businessserver.entity.Tag;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.List;
import java.util.Map;

/**
 * @Author linrushao
 * @Date 2023-06-02
 */
public interface TagService {

    //获取标签表集合
    MongoCollection<Document> getTagCollection();

    //将集合转换为文档
    Tag documentToTag(Document document);

    //添加新标签
    void newTag(Tag tag);

    //更新ElasticSearch索引
    void updateElasticSearchIndex(Tag tag);

    //通过ID获取数据
    Map<String, Object> searchDataById(String index, String id, String fields);

    //查找电影标签
    List<Tag> findMovieTags(int mid);

    //查找我对电影打的标签
    List<Tag> findMyMovieTags(int uid, int mid);

    //移除标签
    void removeTag(int eid);

}
