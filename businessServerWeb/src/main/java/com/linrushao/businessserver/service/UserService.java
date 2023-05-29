package com.linrushao.businessserver.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linrushao.businessserver.entity.mainEntity.User;
import com.linrushao.businessserver.entity.userEntity.UserLogin;
import com.linrushao.businessserver.entity.userEntity.UserRegister;
import com.linrushao.javamodel.Constant;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private ObjectMapper objectMapper;

    private MongoCollection<Document> userCollection;

    /**
     * 获取数据库和数据库中User表集合
     * @return
     */
    private MongoCollection<Document> getUserCollection() {
        if (null == userCollection)
            userCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_USER_COLLECTION);
        return userCollection;
    }

    /**
     * 注册用户
     * @param request
     * @return
     */
    public boolean registerUser(UserRegister request) {
        User user = new User();
        // 根据参数user对象获取注册的用户名
        String username = request.getUsername();
        // 补全数据：加密后的密码
        String salt = UUID.randomUUID().toString().toUpperCase();
        String md5Password = getMd5Password(request.getPassword(), salt);

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
     * 执行密码加密
     * @param password 原始密码
     * @param salt 盐值
     * @return 加密后的密文
     */
    private String getMd5Password(String password, String salt) {
        /*
         * 加密规则：
         * 1、无视原始密码的强度
         * 2、使用UUID作为盐值，在原始密码的左右两侧拼接
         * 3、循环加密3次
         */
        for (int i = 0; i < 3; i++) {
            password = DigestUtils.md5DigestAsHex((salt + password + salt).getBytes()).toUpperCase();
        }
        return password;
    }

    /**
     * 登录用户
     * @param request
     * @return
     */
    public User loginUser(UserLogin request) {
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
     * 将文档格式转为用户对象
     * @param document
     * @return
     */
    private User documentToUser(Document document) {
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
    public boolean checkUserExist(String username) {
        return null != findByUsername(username);
    }

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    public User findByUsername(String username) {
        Document user = getUserCollection().find(new Document("username", username)).first();
        if (null == user || user.isEmpty())
            return null;
        return documentToUser(user);
    }

    /**
     * 更新用户
     * @param user
     * @return
     */
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
    public void removeUser(String username) {
        getUserCollection().deleteOne(new Document("username", username));
    }

}
