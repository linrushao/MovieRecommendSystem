package com.linrushao.businessserver.service.Impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linrushao.businessserver.entity.User;
import com.linrushao.businessserver.entity.form.UserLoginForm;
import com.linrushao.businessserver.entity.form.UserRegisterForm;
import com.linrushao.businessserver.mapper.UserMapper;
import com.linrushao.businessserver.service.UserService;
import com.linrushao.businessserver.utils.Constant;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

import static com.linrushao.businessserver.utils.MD5Util.getMd5Password;

/**
 * @Author linrushao
 * @Date 2023-06-01
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private ObjectMapper objectMapper;

    private MongoCollection<Document> userCollection;

    /**
     * 获取数据库和数据库中User表集合
     * @return
     */
    @Override
    public MongoCollection<Document> getUserCollection() {
        if (null == userCollection)
            userCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_USER_COLLECTION);
        return userCollection;
    }

    /**
     * 用户注册
     * @param request 用户请求参数
     * @return
     */
    @Override
    public boolean registerUser(UserRegisterForm request) {
        User user = new User();
        // 根据参数user对象获取注册的用户名
        String username = request.getUsername();
        // 补全数据：加密后的密码
        String salt = UUID.randomUUID().toString().toUpperCase();
        String md5Password = getMd5Password(request.getPassword(),salt);

        user.setUsername(username);
        user.setPassword(request.getPassword());
        user.setMd5Password(md5Password);
        user.setFirst(true);
        user.setSalt(salt);

        user.setTimestamp(System.currentTimeMillis());
        try {
            getUserCollection().insertOne(Document.parse(objectMapper.writeValueAsString(user)));
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 登录用户
     * @param request
     * @return
     */
    @Override
    public User loginUser(UserLoginForm request) {
        // 调用userMapper的findByUsername()方法，根据参数username查询用户数据
        User result = findByUsername(request.getUsername());
        if (null == result || result.equals("")) {
            return null;
        }
        // 从查询结果中获取盐值
        String salt = result.getSalt();
        // 调用getMd5Password()方法，将参数password和salt结合起来进行加密
        String md5Password = getMd5Password(request.getPassword(), salt);
        // 判断查询结果中的密码，与以上加密得到的密码是否不一致
        if (!result.getMd5Password().equals(md5Password)) {
//            throw new PasswordNotMatchException("密码验证失败的错误");
            return null;
        }
        // 创建新的User对象
        User user = new User();
        // 将查询结果中的uid、username、avatar封装到新的user对象中
        user.setUid(result.getUid());
        user.setUsername(result.getUsername());
        // 返回新的user对象
        return user;
    }

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    @Override
    public User findByUsername(String username) {
        Document user = getUserCollection().find(new Document("username", username)).first();
        if (null == user || user.isEmpty())
            return null;
        return documentToUser(user);
    }

    /**
     * 将文档格式转为用户对象
     * @param document mongodb文档数据
     * @return
     */
    @Override
    public User documentToUser(Document document) {
        try {
            return objectMapper.readValue(JSON.serialize(document), User.class);
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
     * 检查用户是否存在
     * @param username
     * @return
     */
    @Override
    public boolean checkUserExist(String username) {
        return null != findByUsername(username);
    }

    /**
     * 更新用户
     * @param user
     * @return
     */
    @Override
    public boolean updateUser(User user) {
        getUserCollection().updateOne(Filters.eq("uid", user.getUid()), new Document().append("$set", new Document("first", user.isFirst())));
        getUserCollection().updateOne(Filters.eq("uid", user.getUid()), new Document().append("$set", new Document("prefGenres", user.getPrefGenres())));
        return true;
    }

    /**
     * 根据UID查找用户
     * @param uid
     * @return
     */
    @Override
    public User findByUID(int uid) {
        Document user = getUserCollection().find(new Document("uid", uid)).first();
        if (null == user || user.isEmpty())
            return null;
        return documentToUser(user);
    }

    /**
     * 移除用户
     * @param username
     */
    @Override
    public void removeUser(String username) {
        getUserCollection().deleteOne(new Document("username", username));
    }


}
