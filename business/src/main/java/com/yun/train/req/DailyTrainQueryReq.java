package com.yun.train.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DailyTrainQueryReq extends PageReq {


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DailyTrainQueryReq{");
        sb.append("date=").append(date);
        sb.append(", code='").append(code).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
