package com.yi.thread.local.cache.web.test.controller;

import com.yi.thread.local.cache.util.GsonUtils;
import com.yi.thread.local.cache.web.test.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {

    @Resource
    private TestService testService;

    @GetMapping("/hello")
    public String hello() {
        List<Integer> keys = new ArrayList<>();
        keys.add(1);
        keys.add(2);
        List<TestService.Supplier<Integer>> strings = testService.cacheableGet(keys);
        System.out.println("result: " + GsonUtils.toJSONString(strings));
        keys = new ArrayList<>();
        keys.add(1);
        keys.add(3);
        strings = testService.cacheableGet(keys);
        System.out.println("result: " + GsonUtils.toJSONString(strings));
        keys = new ArrayList<>();
        keys.add(1);
        keys.add(2);
        keys.add(3);
        keys.add(4);
        strings = testService.cacheableGet(keys);
        System.out.println("result: " + GsonUtils.toJSONString(strings));
        return "hello";
    }
}
