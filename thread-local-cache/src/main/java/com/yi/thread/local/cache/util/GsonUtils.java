package com.yi.thread.local.cache.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class GsonUtils {

    private static final Gson gson = new Gson();

    public static <T> String toJSONString(T t) {
        return gson.toJson(t);
    }

    public static <T> T toObj(String json, Type type) {
        return gson.fromJson(json, type);
    }
}
