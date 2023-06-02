package com.linrushao.businessserver.service.Impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linrushao.businessserver.entity.Tag;
import com.linrushao.businessserver.service.TagService;
import com.linrushao.businessserver.utils.Constant;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author linrushao
 * @Date 2023-06-02
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestHighLevelClient esClient;

    private MongoCollection<Document> tagCollection;

    /**
     * 获取标签集合
     * @return
     */
    @Override
    public MongoCollection<Document> getTagCollection() {
        if (null == tagCollection)
            tagCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_TAG_COLLECTION);
        return tagCollection;
    }

    /**
     * 将文档转为Tag对象
     * @param document
     * @return
     */
    @Override
    public Tag documentToTag(Document document) {
        try {
            return objectMapper.readValue(JSON.serialize(document), Tag.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
            return null;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 添加新文档
     * @param tag
     */
    @Override
    public void newTag(Tag tag) {
        try {
            getTagCollection().insertOne(Document.parse(objectMapper.writeValueAsString(tag)));
            updateElasticSearchIndex(tag);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新ElasticSearch索引
     * @param tag
     */
    @Override
    public void updateElasticSearchIndex(Tag tag) {

        Map<String, Object> map = null;
        try {
            map = searchDataById(Constant.ELEASTICSEARCH_INDEX, Constant.ELEASTICSEARCH_MOVIE_TYPE, String.valueOf(tag.getMid()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Object value = map.get("tags");
        UpdateRequest updateRequest = new UpdateRequest(Constant.ELEASTICSEARCH_INDEX, Constant.ELEASTICSEARCH_MOVIE_TYPE, String.valueOf(tag.getMid()));

        try {
            if (value == null) {
                updateRequest.doc(XContentFactory.jsonBuilder().startObject().field("tags", tag.getTag()).endObject());
            } else {
                updateRequest.doc(XContentFactory.jsonBuilder().startObject().field("tags", value + "|" + tag.getTag()).endObject());
            }

            esClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过ID获取数据
     *
     * @param index  索引，类似数据库
     * @param id     数据ID
     * @param fields 需要显示的字段，逗号分隔（缺省为全部字段）
     * @return
     */
    @Override
    public Map<String, Object> searchDataById(String index, String id, String fields){
        GetRequest request = new GetRequest(index, id);
        GetResponse response = null;
        try {
            response = esClient.get(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Object> map = response.getSource();
        //为返回的数据添加id
        map.put("id", response.getId());
        return map;
    }

    /**
     * 查询电影标签
     * @param mid
     * @return
     */
    @Override
    public List<Tag> findMovieTags(int mid) {
        List<Tag> tags = new ArrayList<>();
        FindIterable<Document> documents = getTagCollection().find(new Document("mid", mid));
        for (Document document : documents) {
            tags.add(documentToTag(document));
        }
        return tags;
    }

    /**
     * 查询我对电影打的标签
     * @param uid
     * @param mid
     * @return
     */
    @Override
    public List<Tag> findMyMovieTags(int uid, int mid) {
        List<Tag> tags = new ArrayList<>();
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("uid", uid);
        basicDBObject.append("mid", mid);
        FindIterable<Document> documents = getTagCollection().find(basicDBObject);
        for (Document document : documents) {
            tags.add(documentToTag(document));
        }
        return tags;
    }

    /**
     * 移除电影标签
     * @param eid
     */
    @Override
    public void removeTag(int eid) {
        getTagCollection().deleteOne(new Document("eid", eid));
    }


}
