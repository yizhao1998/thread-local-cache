package com.yi.thread.local.cache.web.test.service;

import com.yi.thread.local.cache.core.ThreadLocalCacheable;
import com.yi.thread.local.cache.util.GsonUtils;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestService implements ThreadLocalCacheable<Integer, TestService.Supplier<Integer>> {

    @Override
    public Integer resolveKey(Supplier<Integer> sup) {
        System.out.println("resolve key" + GsonUtils.toJSONString(sup));
        return sup.getSupId();
    }

    @Override
    public List<Supplier<Integer>> cacheableGet(List<Integer> strings) {
        System.out.println("find key for " + GsonUtils.toJSONString(strings));
        if (strings.contains(4)) {
            throw new IllegalArgumentException("test illegal access");
        }
        return strings.stream().map(integer -> new Supplier<>(integer, "NORMAL")).collect(Collectors.toList());
    }

    @Data
    public static class Supplier<T> {

        private T supId;

        private String status;

        Supplier(T supId, String status) {
            this.supId = supId;
            this.status = status;
        }
    }
}

