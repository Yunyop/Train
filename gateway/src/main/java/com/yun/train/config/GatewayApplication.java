package com.yun.train.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan("com.yun")
public class GatewayApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(GatewayApplication.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GatewayApplication.class);
		Environment env = app.run(args).getEnvironment();
		LOGGER.info("启动成功");
		LOGGER.info("网关地址：\thttp://127.0.0.1:{}/hello", env.getProperty("server.port"));
	}
}
