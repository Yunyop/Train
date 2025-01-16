package com.yun.train.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SentinelResource("hello")
@RestController
public class TestController {
    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        Thread.sleep(500);
        return "hello world! Business";
    }
}
