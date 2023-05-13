package com.linrushao.businessserver.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * @Author LinRuShao
 * @Date 2023/5/12 22:25
 */
public class imageDownloadUtil {


    public static void main(String[] args) {
        int validsum = 0;
        int imagesum = 0;
        try {
            // 读取文件
            File file = new File("D:\\桌面\\比赛\\数据集\\筛选后的数据\\images.txt");
            Scanner scanner = new Scanner(file);
            // 遍历每一行
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\^\\^");

                // 检查URL有效性
                boolean valid = checkURL(parts[3].trim());

                // 如果URL无效，删除整行数据
                if (!valid) {
                    validsum += 1;
                    System.out.println("无效的图片链接："+parts[3].trim());
                } else {
                    //如果有效，下载图片
                    String urlName = parts[0].trim(); // 获取mid作为图片名称
                    String imageURL = parts[3].trim();
                    downloadImage(imageURL,urlName);
                    imagesum += 1;
                    // 如果需要将有效的数据写入新的文件，可以使用下面的代码
                    FileWriter writer = new FileWriter("D:\\桌面\\比赛\\数据集\\筛选后的数据\\moviesURL.txt", true);
                    writer.write(line + "\n");
                    writer.close();
                }
            }
            System.out.println("无效的图片链接总数："+validsum);
            System.out.println("图片下载完成："+imagesum);
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
            FileOutputStream outputStream = new FileOutputStream("E:\\movieSystemImages\\imagesData\\"+urlName+".jpg");

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
