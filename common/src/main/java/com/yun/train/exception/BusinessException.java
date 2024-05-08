package com.yun.train.exception;

public class BusinessException extends RuntimeException{
    private BusinessExceptionEnum businessExceptionEnum;

    public BusinessExceptionEnum getBusinessExceptionEnum() {
        return businessExceptionEnum;
    }

    public void setBusinessExceptionEnum(BusinessExceptionEnum businessExceptionEnum) {
        this.businessExceptionEnum = businessExceptionEnum;
    }

    public BusinessException(BusinessExceptionEnum businessExceptionEnum) {
        this.businessExceptionEnum = businessExceptionEnum;
    }
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
