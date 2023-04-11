package com.linrushao.businessserver.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author LRS
 * @Date 2022/8/8 12:10
 * Desc  分页
 */

@Configuration
@ConditionalOnClass(value = {PaginationInterceptor.class})
public class MybatisPluConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
