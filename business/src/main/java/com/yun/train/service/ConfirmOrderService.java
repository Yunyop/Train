package com.yun.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
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
import com.yun.train.req.ConfirmOrderQueryReq;
import com.yun.train.req.ConfirmOrderDoReq;
import com.yun.train.req.ConfirmOrderTicketReq;
import com.yun.train.resp.PageResp;
import com.yun.train.resp.ConfirmOrderQueryResp;
import com.yun.train.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ConfirmOrderService {

    private static final Logger LOGGER= LoggerFactory.getLogger(ConfirmOrderService.class);


    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    public void save(ConfirmOrderDoReq req) {
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }
    }
    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req){
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.setOrderByClause("id desc");
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();

        LOGGER.info("查询页码：{}",req.getPage());
        LOGGER.info("每页条数：{}",req.getSize());
        PageHelper.startPage(req.getPage(),req.getSize());
        List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExample(confirmOrderExample);
        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrderList);

        LOGGER.info("总行数：{}",pageInfo.getTotal());
        LOGGER.info("总页数：{}",pageInfo.getPages());

        List<ConfirmOrderQueryResp> list = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryResp.class);

        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();

        pageResp.setList(list);

        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }
    public void delete(long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }


    public void doConfirm(ConfirmOrderDoReq req) {
//        省略业务数据校验

        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        String start = req.getStart();
        String end = req.getEnd();
        List<ConfirmOrderTicketReq> tickets = req.getTickets();

//        保存确认订单表，状态初始
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrder.setMemberId(LoginMemberContext.getId());
        confirmOrder.setDate(date);
        confirmOrder.setTrainCode(trainCode);
        confirmOrder.setStart(start);
        confirmOrder.setEnd(end);
        confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setTickets(JSON.toJSONString(tickets));
        confirmOrderMapper.insert(confirmOrder);

//        查出余票记录，需要得到真实库存
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date,trainCode,start,end);
        LOGGER.info("查出余票记录:{}",dailyTrainTicket);

//        预扣减库存，并判断余票是否足够
        reduceTickets(req, dailyTrainTicket);

//        计算相对座位的第一个偏移值
//        比如选择得是C1,D2,偏移值是[0,5]
//        比如选择得是A1,B1,C1则偏移值是:[0,1,2]

//        选座
        ConfirmOrderTicketReq ticketReq0 = tickets.get(0);
        if (StrUtil.isNotBlank(ticketReq0.getSeat())){
            LOGGER.info("本次购票有选座");
//            查出本次选座类型有哪些列，用于计算所选座位与第一个座位的偏移值
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
            LOGGER.info("本次选座类型包含的列:{}",colEnumList);

//            组成和前端两排选座一样的列表，用于参照的座位列表
            List<String > referSeatList = new ArrayList<>();
            for (int i = 1; i <= 2; i++) {
                for (SeatColEnum seatColEnum : colEnumList) {
                    referSeatList.add(seatColEnum.getCode()+i);
                }
            }
            LOGGER.info("用于做参照的两排座位:{}",referSeatList);

            List<Integer> offsetList = new ArrayList<>();
//            绝对偏移值，在参照座位列表中的位置
            List<Integer> absoluteOffsetList = new ArrayList<>();
            for (ConfirmOrderTicketReq ticketReq :tickets) {
                int index = referSeatList.indexOf(ticketReq.getSeat());
                absoluteOffsetList.add(index);
            }
            LOGGER.info("计算得到所有座位的绝对偏移值:{}",absoluteOffsetList);
            for (Integer index : absoluteOffsetList) {
                int offset = index - absoluteOffsetList.get(0);
                offsetList.add(offset);
            }
            LOGGER.info("计算得到所有座位的相对偏移值:{}",offsetList);
            getSeat(
                    date,
                    trainCode
                    ,ticketReq0.getSeatTypeCode()
                    ,ticketReq0.getSeat().split("")[0]//从A1得到A
                    ,offsetList);

        }
        else {

            LOGGER.info("本次购票没有选座");
            for (ConfirmOrderTicketReq ticketReq :tickets) {
                getSeat(date,
                        trainCode
                        ,ticketReq0.getSeatTypeCode()
                        ,null
                        ,null);
            }

        }

    }

    private void getSeat(Date date,String trainCode,String seatType,String column,List<Integer> offsetList){
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOGGER.info("共查出{}个符合条件的车厢",carriageList.size());

//        一个车厢一个车厢的获取座位数据
        for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
            LOGGER.info("开始从车厢{}选座",dailyTrainCarriage.getIndex());
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
            LOGGER.info("车厢{}的座位数{}：",dailyTrainCarriage.getIndex(),seatList.size());
        }
    }

    private static void reduceTickets(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq : req.getTickets()){
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            switch (seatTypeEnum){
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft<0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft<0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);
                }case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft<0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);
                }case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft<0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);
                }
            }
        }
    }

}
