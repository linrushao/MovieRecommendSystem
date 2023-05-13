package com.linrushao.businessserver.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @Author LinRuShao
 * @Date 2023/4/9 12:28
 */
public class imageURL {

    public static void main(String[] args) {
        int InvalidSum = 0;
        try {
            // 读取文件
            File file = new File("D:\\桌面\\比赛\\数据集\\筛选后的数据\\moviesURLCheck.txt");
            Scanner scanner = new Scanner(file);
            // 遍历每一行
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\^\\^");

                // 检查URL有效性
                boolean valid = checkURL(parts[3].trim());

                // 如果URL无效，删除整行数据
                if (!valid) {
                    InvalidSum += 1;
//                    System.out.println("mid:" + parts[0].trim());
                    System.out.println("Invalid URL: " + parts[3].trim());
                } else {
//                    System.out.println("Valid URL: " + parts[3].trim());
                    // 如果需要将有效的数据写入新的文件，可以使用下面的代码
                    FileWriter writer = new FileWriter("D:\\桌面\\比赛\\数据集\\筛选后的数据\\movies.txt", true);
                    writer.write(line + "\n");
                    writer.close();
                }
            }
            System.out.println("无效图片总数："+ InvalidSum);
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //验证URL
    public static boolean checkURL(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            return false;
        }
    }
}
