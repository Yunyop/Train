package com.yun.train.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class MemberTicketReq {

    /**
     * 乘客id
     */
    @NotNull(message = "【会员id】不能为空")
    private Long memberId;

    /**
     * 乘客id
     */
    @NotNull(message = "【乘客id】不能为空")
    private Long passengerId;

    /**
     * 乘客id
     */
    @NotNull(message = "【乘客名字】不能为空")
    private String passengerName;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "【日期】不能为空")
    private Date date;

    /**
     * 车次编号
     */
    @NotBlank(message = "【车次编号】不能为空")
    private String trainCode;

    /**
     * 箱序
     */
    @NotNull(message = "【箱序】不能为空")
    private Integer carriageIndex;

    /**
     * 排号|01, 02
     */
    @NotBlank(message = "【排号】不能为空")
    private String row;

    /**
     * 列号|枚举[SeatColumnEnum]
     */
    @NotBlank(message = "【列号】不能为空")
    private String col;

    /**
     * 出发站
     */
    @NotBlank(message = "【出发站】不能为空")
    private String start;

    /**
     * 出发时间
     */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "【出发时间】不能为空")
    private Date startTime;

    /**
     * 到达站
     */
    @NotBlank(message = "【到达站】不能为空")
    private String end;

    /**
     * 到站时间
     */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "【到站时间】不能为空")
    private Date endTime;

    /**
     * 座位类型|枚举[SeatTypeEnum]
     */
    @NotBlank(message = "【座位类型】不能为空")
    private String seatType;

    public @NotNull(message = "【会员id】不能为空") Long getMemberId() {
        return memberId;
    }

    public void setMemberId(@NotNull(message = "【会员id】不能为空") Long memberId) {
        this.memberId = memberId;
    }

    public @NotNull(message = "【乘客id】不能为空") Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(@NotNull(message = "【乘客id】不能为空") Long passengerId) {
        this.passengerId = passengerId;
    }

    public @NotNull(message = "【乘客名字】不能为空") String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(@NotNull(message = "【乘客名字】不能为空") String passengerName) {
        this.passengerName = passengerName;
    }

    public @NotNull(message = "【日期】不能为空") Date getDate() {
        return date;
    }

    public void setDate(@NotNull(message = "【日期】不能为空") Date date) {
        this.date = date;
    }

    public @NotBlank(message = "【车次编号】不能为空") String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(@NotBlank(message = "【车次编号】不能为空") String trainCode) {
        this.trainCode = trainCode;
    }

    public @NotNull(message = "【箱序】不能为空") Integer getCarriageIndex() {
        return carriageIndex;
    }

    public void setCarriageIndex(@NotNull(message = "【箱序】不能为空") Integer carriageIndex) {
        this.carriageIndex = carriageIndex;
    }

    public @NotBlank(message = "【排号】不能为空") String getRow() {
        return row;
    }

    public void setRow(@NotBlank(message = "【排号】不能为空") String row) {
        this.row = row;
    }

    public @NotBlank(message = "【列号】不能为空") String getCol() {
        return col;
    }

    public void setCol(@NotBlank(message = "【列号】不能为空") String col) {
        this.col = col;
    }

    public @NotBlank(message = "【出发站】不能为空") String getStart() {
        return start;
    }

    public void setStart(@NotBlank(message = "【出发站】不能为空") String start) {
        this.start = start;
    }

    public @NotNull(message = "【出发时间】不能为空") Date getStartTime() {
        return startTime;
    }

    public void setStartTime(@NotNull(message = "【出发时间】不能为空") Date startTime) {
        this.startTime = startTime;
    }

    public @NotBlank(message = "【到达站】不能为空") String getEnd() {
        return end;
    }

    public void setEnd(@NotBlank(message = "【到达站】不能为空") String end) {
        this.end = end;
    }

    public @NotNull(message = "【到站时间】不能为空") Date getEndTime() {
        return endTime;
    }

    public void setEndTime(@NotNull(message = "【到站时间】不能为空") Date endTime) {
        this.endTime = endTime;
    }

    public @NotBlank(message = "【座位类型】不能为空") String getSeatType() {
        return seatType;
    }

    public void setSeatType(@NotBlank(message = "【座位类型】不能为空") String seatType) {
        this.seatType = seatType;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MemberTicketReq{");
        sb.append("memberId=").append(memberId);
        sb.append(", passengerId=").append(passengerId);
        sb.append(", passengerName='").append(passengerName).append('\'');
        sb.append(", date=").append(date);
        sb.append(", trainCode='").append(trainCode).append('\'');
        sb.append(", carriageIndex=").append(carriageIndex);
        sb.append(", row='").append(row).append('\'');
        sb.append(", col='").append(col).append('\'');
        sb.append(", start='").append(start).append('\'');
        sb.append(", startTime=").append(startTime);
        sb.append(", end='").append(end).append('\'');
        sb.append(", endTime=").append(endTime);
        sb.append(", seatType='").append(seatType).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

