package com.yi.thread.local.cache.core;

import com.google.common.base.Preconditions;
import com.yi.thread.local.cache.manager.ThreadLocalCacheManager;
import com.yi.thread.local.cache.util.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ThreadLocalCacheAspect {

    @Pointcut("execution(* com.yi.thread.local.cache.core.ThreadLocalCacheable.cacheableGet(..))")
    public void cacheableGetPointCut() {
    }

    @Around("cacheableGetPointCut()")
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object around(ProceedingJoinPoint pjp) {

        ThreadLocalCacheManager.MarkType markType = getThreadLocalCacheMarkType(pjp);

        Object id = pjp.getArgs()[0];
        Object proxy = pjp.getThis();

        Preconditions.checkArgument(proxy instanceof ThreadLocalCacheable, "the proxy object must be of ThreadLocalCacheable.class");
        Preconditions.checkArgument(id instanceof List, "the argument must be of List.class");

        // distinct
        id = ((List)id).stream().distinct().collect(Collectors.toList());
        List unCachedKeys = (List) ((List)id).stream()
                .filter(e -> !ThreadLocalCacheManager.containsCacheKey(markType, GsonUtils.toJSONString(e))).distinct()
                .collect(Collectors.toList());

        Object object = null;
        try {
            object = pjp.proceed(new Object[]{unCachedKeys});
        } catch (Throwable t) {
            log.warn("proceeding met exception, using cached values", t);
        }
        if (null != object) {
            Preconditions.checkArgument(object instanceof List, "the returned object must be of List.class");
        }

        List results = new ArrayList<>(((List)id).size());

        // append cached values to result list
        appendCachedValues(markType.getReturnType(), markType, (List) id, results);

        // append uncached values to result list, indexing uncached values into thread local cache
        appendAndIndexQueriedValues(markType, (ThreadLocalCacheable) proxy, object, results);

        return results;
    }

    private ThreadLocalCacheManager.MarkType getThreadLocalCacheMarkType(ProceedingJoinPoint pjp) {
        String metaKey = fetchMetaKey(pjp);
        Type idType = fetchIdClass(pjp);
        Type resultType = fetchResultClass(pjp);

        return ThreadLocalCacheManager.initMarkType(idType, resultType, metaKey);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void appendAndIndexQueriedValues(ThreadLocalCacheManager.MarkType markType, ThreadLocalCacheable proxy, Object object, List results) {
        if (null != object && ((List)object).size() != 0) {
            results.addAll(((List) object));
            ((List) object).forEach(r -> {
                Object key = proxy.resolveKey(r);
                ThreadLocalCacheManager.addCacheEntry(markType, GsonUtils.toJSONString(key), GsonUtils.toJSONString(r));
            });
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void appendCachedValues(Type resultType, ThreadLocalCacheManager.MarkType markType, List id, List results) {
        for (Object key : id) {
            String cacheKey = GsonUtils.toJSONString(key);
            if (ThreadLocalCacheManager.containsCacheKey(markType, cacheKey)) {
                results.add(GsonUtils.toObj(ThreadLocalCacheManager.getCacheValue(markType, cacheKey), resultType));
            }
        }
    }

    private Type fetchResultClass(ProceedingJoinPoint pjp) {
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        return ((ParameterizedType)method.getGenericReturnType()).getActualTypeArguments()[0];
    }

    private Type fetchIdClass(ProceedingJoinPoint pjp) {
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        if (!(method.getGenericParameterTypes()[0] instanceof ParameterizedType)) {
            return Object.class;
        }
        return ((ParameterizedType) method.getGenericParameterTypes()[0]).getActualTypeArguments()[0];
    }

    private String fetchMetaKey(ProceedingJoinPoint pjp) {
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        return method.getDeclaringClass().getSimpleName();
    }
}
