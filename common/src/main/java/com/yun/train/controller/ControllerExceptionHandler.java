package com.yun.train.controller;

import com.yun.train.exception.BusinessException;
import com.yun.train.resp.CommonResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public CommonResp exception(Exception e) {
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
    public CommonResp exception(BusinessException e) {
        CommonResp commonResp = new CommonResp();
        LOG.error("业务异常：",e);
        commonResp.setSuccess(false);
        commonResp.setMessage(e.getBusinessExceptionEnum().getDesc());
        return commonResp;
    }
}
