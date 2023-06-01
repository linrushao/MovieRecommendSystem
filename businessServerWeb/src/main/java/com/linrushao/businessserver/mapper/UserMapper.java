package com.linrushao.businessserver.mapper;

import com.linrushao.businessserver.entity.mainEntity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author linrushao
 * @Date 2023-06-01
 */
public interface UserMapper extends MongoRepository<User, String> {
}
