//package com.yun.train.job;
//
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
////适合单体应用，不适合集群
//// 没法实时更改定时任务状态和策略
//
//@EnableScheduling
//@Component
//public class SprinBootTestJob {
//    @Scheduled(cron = "0/5 * * * * ?")
//    protected void test(){
////        增加分布式锁
//        System.out.println("SprinBootTestJob Test");
//    }
//}
