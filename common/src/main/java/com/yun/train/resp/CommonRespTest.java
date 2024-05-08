package com.yun.train.resp;

public class CommonRespTest <T>{
//    业务上的成功或失败
    private boolean success=true;
//    反回信息
    private String msg;
//    返回泛型数据，自定义类型
    private T data;
    public  CommonRespTest(){

    }
    public CommonRespTest(T data){
        this.data=data;
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    @Override

    public String toString() {
        final StringBuffer sb = new StringBuffer("CommonResp{");
        sb.append("success=").append(success);
        sb.append(", message='").append(msg).append('\'');
        sb.append(", content=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
