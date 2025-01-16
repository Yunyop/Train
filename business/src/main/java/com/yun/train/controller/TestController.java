package com.yun.train.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.yun.train.service.TestService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Resource
    private TestService testService;
    @SentinelResource("hello")
    @GetMapping("/hello")
    public String hello() throws InterruptedException {

        testService.hello2();
        return "hello world! Business";
    }

    @SentinelResource("hello1")
    @GetMapping("/hello1")
    public String hello1() throws InterruptedException {

        testService.hello2();
        return "hello world! Business1";
    }
}
