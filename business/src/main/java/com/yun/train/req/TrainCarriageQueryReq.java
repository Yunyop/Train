package com.yun.train.req;

import com.yun.train.req.PageReq;

public class TrainCarriageQueryReq extends PageReq {
    private String trainCode;

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TrainCarriageQueryReq{");
        sb.append("trainCode='").append(trainCode).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
