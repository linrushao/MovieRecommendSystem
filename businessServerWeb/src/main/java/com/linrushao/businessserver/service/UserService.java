package com.linrushao.businessserver.service;

import com.linrushao.businessserver.entity.User;
import com.linrushao.businessserver.entity.form.UserLoginForm;
import com.linrushao.businessserver.entity.form.UserRegisterForm;
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
    boolean registerUser(UserRegisterForm request);

    //用户登录
    User loginUser(UserLoginForm request);

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
