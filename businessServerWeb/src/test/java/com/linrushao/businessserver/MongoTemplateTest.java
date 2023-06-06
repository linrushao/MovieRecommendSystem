package com.linrushao.businessserver;

import com.linrushao.businessserver.entity.Movie;
import com.linrushao.businessserver.entity.User;
import com.linrushao.businessserver.entity.form.Recommendation;
import com.linrushao.businessserver.mapper.MovieMapper;
import com.linrushao.businessserver.mapper.UserMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.linrushao.businessserver.utils.Constant.*;


/**
 * @Author linrushao
 * @Date 2023-06-01
 */

public class MongoTemplateTest extends BusinessServerApplicationTests{
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserMapper userMapper;

    @Autowired
    MovieMapper movieMapper;

    @Test
    public  void TestFind() {
        List<User> all = mongoTemplate.findAll(User.class);
//        all.forEach(System.out::println);
        System.out.println("==========================");

        List<User> uid = mongoTemplate.find(new Query(Criteria.where("uid").is(48690)), User.class);
        uid.forEach(System.out::println);
        System.out.println("==========================");

        MongoCollection<Document> collection = mongoTemplate.getDb().getCollection(MONGODB_RATE_MORE_MOVIES_RECENTLY_COLLECTION);
        FindIterable<Document> documents = collection
                .find().sort(Sorts.descending("yeahmonth"))
                .limit(REDIS_MOVIE_RATING_QUEUE_SIZE);
        List<Recommendation> recommendations = new ArrayList<>();
        for (Document document : documents) {
            recommendations.add(new Recommendation(document.getInteger("mid"), 0D));
        }

        recommendations.forEach(System.out::println);
    }

    @Test
    public void TestMapper(){
//        User user = new User();
//        user.setUid(3599307);

//        Example<User> usersExample = Example.of(user);
//        List<User> all = userMapper.findAll();

        System.out.println("---------------==============================");
        User user = new User();
        user.setUid(48690);
//        user.setAge(20);
        Example<User> userExample = Example.of(user);
        List<User> users = userMapper.findAll();
        System.out.println("---------------==============================");
//        System.out.println(users);

        Movie movie = new Movie();
        movie.setMid(1451391);
        Example<Movie> usersExample = Example.of(movie);
        long count = movieMapper.count(usersExample);

    }


}
