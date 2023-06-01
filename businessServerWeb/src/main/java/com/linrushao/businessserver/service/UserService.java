package com.linrushao.businessserver.service;

import com.linrushao.businessserver.entity.mainEntity.User;
import com.linrushao.businessserver.entity.userEntity.UserLogin;
import com.linrushao.businessserver.entity.userEntity.UserRegister;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * @Author linrushao
 * @Date 2023-06-01
 */
public interface UserService {

    // 获取数据库和数据库中User表集合
    MongoCollection<Document> getUserCollection();

    //用户注册
    boolean registerUser(UserRegister request);

    //用户登录
    User loginUser(UserLogin request);

    //根据用户名查找用户
    User findByUsername(String username);

    //将文档转为用户对象
    User documentToUser(Document document);

    //查看用户是否存在
    boolean checkUserExist(String username);

    //根据用户信息
    boolean updateUser(User user);

    //根据用户ID查找用户
    User findByUID(int uid);

    //移除用户
    void removeUser(String username);
}
