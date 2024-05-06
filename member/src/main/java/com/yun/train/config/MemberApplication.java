package com.yun.train.config;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ComponentScan("com.yun")
@MapperScan("com.yun.train.mapper")
@EnableWebMvc
public class MemberApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(MemberApplication.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(MemberApplication.class);
		try {
			Environment env = app.run(args).getEnvironment();
			LOGGER.info("启动成功");
			LOGGER.info("地址：\thttp://127.0.0.1:{}{}/hello", env.getProperty("server.port"), env.getProperty("server.servlet.context-path"));

		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
