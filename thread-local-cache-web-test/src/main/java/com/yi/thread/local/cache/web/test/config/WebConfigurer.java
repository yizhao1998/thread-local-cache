package com.yi.thread.local.cache.web.test.config;

import com.yi.thread.local.cache.interceptor.ThreadLocalCacheInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Bean
    public HandlerInterceptor threadLocalCacheInterceptor() {
        return new ThreadLocalCacheInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(threadLocalCacheInterceptor());
    }
}
