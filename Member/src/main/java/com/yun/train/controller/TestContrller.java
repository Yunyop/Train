package com.yun.train;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestContrller {
    @GetMapping("/hello")
    public String toString() {
        return "hello world1345";
    }
}
