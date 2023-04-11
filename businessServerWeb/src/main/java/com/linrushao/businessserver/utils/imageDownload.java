package com.linrushao.businessserver.utils;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linrushao.businessserver.BusinessServerApplication;
import com.linrushao.businessserver.entity.mainEntity.Movie;
import com.linrushao.businessserver.entity.movieEntity.Recommendation;
import com.linrushao.businessserver.service.HomeHotRecommendationsService;
import com.linrushao.businessserver.service.MovieService;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import javafx.application.Application;
import org.apache.ibatis.annotations.Mapper;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.linrushao.businessserver.utils.Constant.MONGODB_DATABASE;
import static com.linrushao.businessserver.utils.Constant.MONGODB_MOVIE_COLLECTION;

/**
 * @Author LinRuShao
 * @Date 2023/4/6 10:18
 */

public class imageDownload{

    public static void main(String[] args) {
        /**
         * 在 main 方法中无法直接使用 Spring Boot 应用程序中的自动注入的 mongoClient，因为 main 方法是一个静态方法，
         * 而 Spring Boot 的自动注入是在实例化应用程序时完成的。因此，在 main 方法中，你需要手动获取 mongoClient 对象
         */
//        ApplicationContext ctx = SpringApplication.run(BusinessServerApplication.class, args);
//        MongoClient mongoClient = ctx.getBean(MongoClient.class);
//        MongoCollection<Document> movieCollection = mongoClient.getDatabase(MONGODB_DATABASE).getCollection(MONGODB_MOVIE_COLLECTION);
//        BasicDBObject query = new BasicDBObject();
//        query.put("image", new BasicDBObject("$exists", true));
//        FindIterable<Document> documents = movieCollection.find();
//
//
//        for (Document document : documents) {
//            String imageUrl = document.getString("image");
//            String destinationFile = document.getInteger("mid")+".jpg"; // 下载到本地的文件名
//            try {
//                URL url = new URL(imageUrl);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//                InputStream inputStream = connection.getInputStream();
//                FileOutputStream outputStream = new FileOutputStream("E:\\临时资料\\movieSystemImages\\imagesData\\"+destinationFile);
//
//                byte[] buffer = new byte[2048];
//                int length;
//
//                while ((length = inputStream.read(buffer)) != -1) {
//                    outputStream.write(buffer, 0, length);
//                }
//
//                inputStream.close();
//                outputStream.close();
//
//                System.out.println("图片已下载到本地：" + destinationFile);
//            } catch (Exception e) {
//                System.out.println("下载图片时出错：" + e.getMessage());
//            }
//        }
//    }
        String intputfile = "E:\\比赛\\数据集\\筛选后的数据\\output.txt";
        try {
            // 读取文件
            File file = new File(intputfile);
            Scanner scanner = new Scanner(file);
            // 遍历每一行
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\^\\^");
                String urlName = parts[0].trim(); // 获取mid作为图片名称
                String imageURL = parts[3].trim();
                downloadImage(imageURL,urlName);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    // 下载图片
    private static void downloadImage(String imageUrl,String urlName) {
        try {
            URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream("E:\\临时资料\\movieSystemImages\\imagesData\\"+urlName+".jpg");

                byte[] buffer = new byte[2048];
                int length;

                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
