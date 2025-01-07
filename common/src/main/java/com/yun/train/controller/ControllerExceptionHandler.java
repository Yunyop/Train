package com.yun.train.controller;

import cn.hutool.core.util.StrUtil;
import com.yun.train.exception.BusinessException;
import com.yun.train.resp.CommonResp;
import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


//统一异常处理
@ControllerAdvice
public class ControllerExceptionHandler {
    private static final Logger LOG= LoggerFactory.getLogger(ControllerExceptionHandler.class);
//    所有异常统一处理
//    @param e
//    @return
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResp exception(Exception e) throws Exception{
        LOG.info("seata全局事务ID save:{}", RootContext.getXID());
         // 如果是在一次全局事务里出异常了，就不要包装返回值，将异常抛给调用方，让调用方回滚事务
         if (StrUtil.isNotBlank(RootContext.getXID())) {
             throw e;
         }
        CommonResp commonResp = new CommonResp();
        LOG.error("系统异常：",e);
        commonResp.setSuccess(false);
        commonResp.setMessage("系统出现异常，请联系管理员");
        return commonResp;
    }
    //    业务异常统一处理
//    @param e
//    @return
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public CommonResp exception(BusinessException e) throws Exception {
        LOG.info("seata全局事务ID save:{}", RootContext.getXID());
        // // 如果是在一次全局事务里出异常了，就不要包装返回值，将异常抛给调用方，让调用方回滚事务
         if (StrUtil.isNotBlank(RootContext.getXID())) {
             throw e;
         }
        CommonResp commonResp = new CommonResp();
        LOG.error("业务异常：{}",e.getBusinessExceptionEnum().getDesc());
        commonResp.setSuccess(false);
        commonResp.setMessage(e.getBusinessExceptionEnum().getDesc());
        return commonResp;
    }
    //    校验异常统一处理
    //    @param e
    //    @return
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public CommonResp exception(MethodArgumentNotValidException e) {
        CommonResp commonResp = new CommonResp();
        LOG.error("校验异常：{}",e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        commonResp.setSuccess(false);
        commonResp.setMessage(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return commonResp;
    }
}
