package com.linrushao.businessserver;

import com.linrushao.businessserver.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;


/**
 * @Author linrushao
 * @Date 2023-06-01
 */

public class MongoTemplateTest extends BusinessServerApplicationTests{
    @Autowired
    MongoTemplate mongoTemplate;


    @Test
    public  void TestFind() {
        List<User> all = mongoTemplate.findAll(User.class);
        all.forEach(System.out::println);
    }

}
