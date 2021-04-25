package com.yi.thread.local.cache.interceptor;

import com.yi.thread.local.cache.manager.ThreadLocalCacheManager;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ThreadLocalCacheInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // for robustness
        ThreadLocalCacheManager.refresh();

        ThreadLocalCacheManager.init();
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalCacheManager.refresh();
    }
}
