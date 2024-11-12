package com.yun.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yun.train.context.LoginMemberContext;
import com.yun.train.domain.*;
import com.yun.train.enums.ConfirmOrderStatusEnum;
import com.yun.train.enums.SeatColEnum;
import com.yun.train.enums.SeatTypeEnum;
import com.yun.train.exception.BusinessException;
import com.yun.train.exception.BusinessExceptionEnum;
import com.yun.train.mapper.ConfirmOrderMapper;
import com.yun.train.mapper.DailyTrainSeatMapper;
import com.yun.train.req.ConfirmOrderDoReq;
import com.yun.train.req.ConfirmOrderQueryReq;
import com.yun.train.req.ConfirmOrderTicketReq;
import com.yun.train.resp.ConfirmOrderQueryResp;
import com.yun.train.resp.PageResp;
import com.yun.train.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AfterConfirmOrderService {

    private static final Logger LOGGER= LoggerFactory.getLogger(AfterConfirmOrderService.class);


    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;


    /**
     * 选中座位后事务处理
     *  座位表修改余票sell
     *  为会员增加购票记录
     *  更新确认订单为成功
     */
    @Transactional
    public void afterDoConfirm(List<DailyTrainSeat> finalSeatList) {
        for (DailyTrainSeat dailyTrainSeat : finalSeatList) {
            DailyTrainSeat seatForUpdate = new DailyTrainSeat();
            seatForUpdate.setId(dailyTrainSeat.getId());
            seatForUpdate.setSell(dailyTrainSeat.getSell());
            seatForUpdate.setUpdateTime(new Date());
            dailyTrainSeatMapper.updateByPrimaryKeySelective(seatForUpdate);
        }
    }
}
