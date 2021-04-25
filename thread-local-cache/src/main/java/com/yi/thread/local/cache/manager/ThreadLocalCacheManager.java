package com.yi.thread.local.cache.manager;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ThreadLocalCacheManager {

    private static final ThreadLocal<Map<MarkType, Map<String, String>>> THREAD_LOCAL = new ThreadLocal<>();

    public static void refresh() {
        THREAD_LOCAL.remove();
    }

    public static void init() {
        THREAD_LOCAL.set(new HashMap<>());
    }

    public static MarkType initMarkType(Type argumentType, Type returnType, String mark) {
        MarkType markType = MarkType.newInstance(argumentType, returnType, mark);
        return initMarkType(markType);
    }

    private static MarkType initMarkType(MarkType markType) {
        Map<MarkType, Map<String, String>> markTypeMapMap = THREAD_LOCAL.get();
        markTypeMapMap.computeIfAbsent(markType, (mt) -> new HashMap<>());
        return markType;
    }

    public static boolean containsCacheKey(MarkType markType, String cacheKey) {
        return THREAD_LOCAL.get().containsKey(markType) &&
                THREAD_LOCAL.get().get(markType).containsKey(cacheKey);
    }

    public static String getCacheValue(MarkType markType, String cacheKey) {
        if (!THREAD_LOCAL.get().containsKey(markType)) {
            initMarkType(markType);
        }
        return THREAD_LOCAL.get().get(markType).get(cacheKey);
    }

    public static void addCacheEntry(MarkType markType, String cacheKey, String cacheValue) {
        if (!THREAD_LOCAL.get().containsKey(markType)) {
            initMarkType(markType);
        }
        THREAD_LOCAL.get().get(markType).putIfAbsent(cacheKey, cacheValue);
    }

    public static class MarkType {

        private final Type argumentType;

        private final Type returnType;

        private final String mark;

        private MarkType(Type argumentType, Type returnType, String mark) {
            this.argumentType = argumentType;
            this.returnType = returnType;
            this.mark = mark;
        }

        public static MarkType newInstance(Type argumentType, Type returnType, String mark) {
            return new MarkType(argumentType, returnType, mark);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MarkType markType = (MarkType) o;
            return Objects.equals(mark, markType.mark);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mark);
        }

        public Type getReturnType() {
            return returnType;
        }

        public Type getArgumentType() {
            return argumentType;
        }
    }
}
