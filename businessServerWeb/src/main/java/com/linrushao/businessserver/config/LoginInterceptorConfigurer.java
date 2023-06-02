package com.linrushao.businessserver.config;

import com.linrushao.businessserver.utils.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/** 注册处理器拦截器 */
@Configuration
public class LoginInterceptorConfigurer implements WebMvcConfigurer {
    /** 拦截器配置 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 创建拦截器对象
        HandlerInterceptor interceptor = new LoginInterceptor();

        // 白名单
        List<String> patterns = new ArrayList<String>();
        patterns.add("/css/**");
        patterns.add("/fonts/**");
        patterns.add("/images/**");
        patterns.add("/js/**");
        patterns.add("/loginbootstrap/**");
        patterns.add("/register");
        patterns.add("/user/register");
        patterns.add("/login");
        patterns.add("/user/login");
        patterns.add("/register.html");
        patterns.add("/login.html");
        patterns.add("/");
        patterns.add("/index.html");
        patterns.add("/common/common.html");

        // 通过注册工具添加拦截器
        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns(patterns);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        上传的图片在D盘下的img目录下，访问路径如：http://localhost:8081/image/1.jpg
//        其中image表示访问的前缀。"file:D:/img/"是文件真实的存储路径
        registry.addResourceHandler("/imagesData/**").addResourceLocations("file:E:/movieSystemImages/imagesData/");

    }
}
