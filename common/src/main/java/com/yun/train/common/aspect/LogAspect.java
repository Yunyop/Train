package com.yun.train.common.aspect;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
public class LogAspect {
    public LogAspect(){
        System.out.println("Common LogAspect");
    }
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
//    定义一个切点
    @Pointcut("execution(public * com.yun..*Controller.*(..))")
    public void controllerPointCut(){

    }
    @Before("controllerPointCut()")
    public void doBefore(JoinPoint joinPoint){
//        增加日志流水
        MDC.put("LOG_ID",System.currentTimeMillis()+RandomUtil.randomString(3));
//        开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Signature signature = joinPoint.getSignature();
        String name = signature.getName();
//        打印请求信息
        log.info("开始");
        log.info("请求地址:{} {}",request.getRequestURI(),request.getMethod());
        log.info("类名方法:{}.{}",signature.getDeclaringTypeName(),name);
        log.info("远程地址:{}",request.getRemoteAddr());
//        打印请求参数
        Object[] args = joinPoint.getArgs();
//        log.info("请求参数",JSONObject.toJSONString(args));
//        排除特殊类型的参数，如文件类型
        Object[] objects = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (    args[i] instanceof ServletRequest||
                    args[i] instanceof ServletResponse||
                    args[i] instanceof MultipartFile) {
                continue;
            }
            objects[i] = args[i];
        }
//        排除字段，敏感字段或太长字段不显示：身份证、手机号、邮箱、密码等
        String[] excludeProperties = {"mobile"};
        PropertyPreFilters filters = new PropertyPreFilters();
        PropertyPreFilters.MySimplePropertyPreFilter excludefilter = filters.addFilter();
        excludefilter.addExcludes(excludeProperties);
        log.info("请求参数:{}",JSONObject.toJSONString(objects,excludefilter));
    }
    @Around("controllerPointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
//        排除字段，敏感字符或太长字段不显示：身份证、手机号、邮箱、密码等
        String[] excludeProperties = {"mobile"};
        PropertyPreFilters filters = new PropertyPreFilters();
        PropertyPreFilters.MySimplePropertyPreFilter excludefilter = filters.addFilter();
        excludefilter.addExcludes(excludeProperties);
        log.info("返回结果:{}",JSONObject.toJSONString(result,excludefilter));
        log.info("结束耗时:{}ms",System.currentTimeMillis()-startTime);
        return result;
    }

}
