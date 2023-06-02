package com.linrushao.businessserver.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linrushao.businessserver.entity.Movie;
import com.linrushao.businessserver.entity.Rating;
import com.linrushao.businessserver.entity.form.Recommendation;
import com.linrushao.businessserver.service.MovieService;
import com.linrushao.businessserver.utils.Constant;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @Author linrushao
 * @Date 2023-06-02
 */
@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ObjectMapper objectMapper;

    private MongoCollection<Document> movieCollection;
    private MongoCollection<Document> averageMoviesScoreCollection;
    private MongoCollection<Document> rateCollection;

    /**
     * 获取电影的相关信息【Movie】集合
     * @return
     */
    @Override
    public MongoCollection<Document> getMovieCollection() {
        if(null == movieCollection)
            movieCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_MOVIE_COLLECTION);
        return movieCollection;
    }

    /**
     * 平均评分电影集合【AverageMovies】集合
     * @return
     */
    @Override
    public MongoCollection<Document> getAverageMoviesScoreCollection() {
        if(null == averageMoviesScoreCollection)
            averageMoviesScoreCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_AVERAGE_MOVIES_SCORE_COLLECTION);
        return averageMoviesScoreCollection;
    }

    /**
     * 评分数据集合【Rating】集合
     * @return
     */
    @Override
    public MongoCollection<Document> getRateCollection() {
        if(null == rateCollection)
            rateCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_RATING_COLLECTION);
        return rateCollection;
    }

    /**
     * 获取推荐电影
     * @param recommendations
     * @return
     */
    @Override
    public List<Movie> getRecommendeMovies(List<Recommendation> recommendations) {
        List<Integer> ids = new ArrayList<>();
        for (Recommendation rec: recommendations) {
            ids.add(rec.getMid());
        }
        return getMovies(ids);
    }

    /**
     * 获取混合推荐电影
     * @param recommendations
     * @return
     */
    @Override
    public List<Movie> getHybirdRecommendeMovies(List<Recommendation> recommendations) {
        List<Integer> ids = new ArrayList<>();
        for (Recommendation rec: recommendations) {
            ids.add(rec.getMid());
        }
        return getMovies(ids);
    }

    /**
     * 根据电影的mid查找电影
     * @param mids 电影ID列表
     * @return 电影的信息列表
     */
    @Override
    public List<Movie> getMovies(List<Integer> mids) {
        FindIterable<Document> documents = getMovieCollection().find(Filters.in("mid",mids));
        List<Movie> movies = new ArrayList<>();
        for (Document document: documents) {
            movies.add(documentToMovie(document));
        }
        return movies;
    }

    /**
     * 集合文档转换为Movie对象
     * @param document
     * @return
     */
    @Override
    public Movie documentToMovie(Document document) {
        Movie movie = null;
        try{
            movie = objectMapper.readValue(JSON.serialize(document),Movie.class);
            Document score = getAverageMoviesScoreCollection().find(Filters.eq("mid",movie.getMid())).first();
            if(null == score || score.isEmpty())
                movie.setScore(0D);
            else
                movie.setScore((Double) score.get("avg",0D));
        }catch (IOException e) {
            e.printStackTrace();
        }
        return movie;
    }

    /**
     * 集合文档转换为评分对象
     * @param document
     * @return
     */
    @Override
    public Rating documentToRating(Document document) {
        Rating rating = null;
        try{
            rating = objectMapper.readValue(JSON.serialize(document), Rating.class);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return rating;
    }

    /**
     * 查看电影是否存在
     * @param mid
     * @return
     */
    @Override
    public boolean movieExist(int mid) {
        return null != findByMID(mid);
    }

    /**
     * 根据电影mid查询电影内容
     * @param mid
     * @return
     */
    @Override
    public Movie findByMID(int mid) {
        Document document = getMovieCollection().find(new Document("mid",mid)).first();
        if(document == null || document.isEmpty())
            return null;
        return documentToMovie(document);
    }

    /**
     * 移除电影
     * @param mid
     */
    @Override
    public void removeMovie(int mid) {
        getMovieCollection().deleteOne(new Document("mid",mid));
    }

    /**
     * 获取用户评分的电影
     * @param uid
     * @return
     */
    @Override
    public List<Movie> getMyRateMovies(int uid) {
        FindIterable<Document> documents = getRateCollection().find(Filters.eq("uid",uid));
        List<Integer> ids = new ArrayList<>();
        Map<Integer,Double> scores = new HashMap<>();
        for (Document document: documents) {
            Rating rating = documentToRating(document);
            ids.add(rating.getMid());
            scores.put(rating.getMid(),rating.getScore());
        }
        List<Movie> movies = getMovies(ids);
        for (Movie movie: movies) {
            movie.setScore(scores.getOrDefault(movie.getMid(),movie.getScore()));
        }
        return movies;
    }

    /**
     * 获取新添加的电影
     * @return
     */
    @Override
    public List<Movie> getNewMovies() {
        FindIterable<Document> documents = getMovieCollection().find().sort(Sorts.descending("issue")).limit(Constant.HOME_MOVIES_ITEM_SIZE);
        List<Movie> movies = new ArrayList<>();
        for (Document document: documents) {
            movies.add(documentToMovie(document));
        }
        return movies;
    }

    /**
     * 获取mongdb表中内嵌的Recs中的数据，解析Document
     * 表名
     * _id @2374932
     * uid 1
     * recs  111|213|314|42
     * @param document
     * @return
     */
    @Override
    public List<Recommendation> parseRecs(Document document) {
        List<Recommendation> recommendations = new ArrayList<>();
        if (null == document || document.isEmpty())
            return recommendations;
        //在表中的内嵌中找到resc类型的数据
        ArrayList<Document> recs = document.get("recs", ArrayList.class);
        for (Document recDoc : recs) {
            recommendations.add(new Recommendation(recDoc.getInteger("mid"), recDoc.getDouble("score")));
        }
        //对推荐电影进行排序，根据分数的大小进行比较，分数大的在前面，分数少的在后面
        Collections.sort(recommendations, new Comparator<Recommendation>() {
            @Override
            public int compare(Recommendation o1, Recommendation o2) {
                return o1.getScore() > o2.getScore() ? -1 : 1;
            }
        });
        return recommendations.subList(0, recommendations.size());
    }


}
