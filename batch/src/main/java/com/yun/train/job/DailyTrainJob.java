package com.yun.train.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.yun.train.feign.BusinessFeign;
import com.yun.train.resp.CommonResp;
import jakarta.annotation.Resource;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Date;

@DisallowConcurrentExecution
public class DailyTrainJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(DailyTrainJob.class);

    @Resource
    BusinessFeign  businessFeign;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        MDC.put("LOG_ID",System.currentTimeMillis()+ RandomUtil.randomString(3));

        DailyTrainJob.log.info("生成15天后的车次数据开始");
        Date date = new Date();
        DateTime dateTime = DateUtil.offsetDay(date, 15);
        Date offsetDate = dateTime.toJdkDate();
        CommonResp<Object> commonResp = businessFeign.genDaily(offsetDate);
        DailyTrainJob.log.info("生成15天后车次数据结束，结果；{}",commonResp);

    }
}
