package com.yun.train.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan("com.yun")
@MapperScan("com.yun.train.mapper")
@EnableFeignClients("com.yun.train.feign")
@EnableWebMvc
@EnableCaching
public class BusinessApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(BusinessApplication.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(BusinessApplication.class);
		try {
			Environment env = app.run(args).getEnvironment();
			LOGGER.info("启动成功！！");
			LOGGER.info("地址：\thttp://127.0.0.1:{}{}/hello", env.getProperty("server.port"), env.getProperty("server.servlet.context-path"));

////			限流规则
//			initFlowRules();
//			LOGGER.info("已定义限流规则");
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	private static void initFlowRules(){
		List<FlowRule> rules = new ArrayList<>();
		FlowRule rule = new FlowRule();
		rule.setResource("BeforeDoConfirm");
		rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
		// Set limit QPS to 20.
		rule.setCount(1);
		rules.add(rule);
		FlowRuleManager.loadRules(rules);
	}
}
