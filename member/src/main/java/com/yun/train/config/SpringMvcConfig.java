package com.yun.train.config;


import com.yun.train.interceptor.MemberInterceptor;
import com.yun.train.interceptor.LoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    LoginInterceptor loginInterceptor;

   @Resource
   MemberInterceptor memberInterceptor;

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(loginInterceptor);

       // 路径不要包含context-path
       registry.addInterceptor(memberInterceptor)
               .addPathPatterns("/**")
               .excludePathPatterns(
                       "/hello",
                       "/member/send-code",
                       "/member/login"
               );
   }
}
