package com.yun.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yun.train.domain.*;
import com.yun.train.enums.SeatTypeEnum;
import com.yun.train.enums.TrainTypeEnum;
import com.yun.train.mapper.DailyTrainTicketMapper;
import com.yun.train.req.DailyTrainTicketQueryReq;
import com.yun.train.req.DailyTrainTicketSaveReq;
import com.yun.train.resp.PageResp;
import com.yun.train.resp.DailyTrainTicketQueryResp;
import com.yun.train.util.SnowUtil;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
public class DailyTrainTicketService {

    private static final Logger LOGGER= LoggerFactory.getLogger(DailyTrainTicketService.class);


    @Resource
    private DailyTrainTicketMapper dailyTrainTicketMapper;

    @Cacheable(value = "DailyTrainTicketService.queryList")
    public PageResp<DailyTrainTicketQueryResp>queryList3(DailyTrainTicketQueryReq req) {
        LOGGER.info("测试缓存击穿");
        return null;
    }

    @CachePut(value = "DailyTrainTicketService.queryList")
    public PageResp<DailyTrainTicketQueryResp>queryList2(DailyTrainTicketQueryReq req) {
        return queryList(req);
    }

    @Cacheable(value = "DailyTrainTicketService.queryList")
    public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req){
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.setOrderByClause("id desc");
        DailyTrainTicketExample.Criteria criteria = dailyTrainTicketExample.createCriteria();
        if(ObjectUtil.isNotNull(req.getDate())){
            criteria.andDateEqualTo(req.getDate());
        }
        if(ObjectUtil.isNotEmpty(req.getTrainCode())){
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }
        if (ObjectUtil.isNotEmpty(req.getStart())){
            criteria.andStartEqualTo(req.getStart());
        }
        if (ObjectUtil.isNotEmpty(req.getEnd())){
            criteria.andEndEqualTo(req.getEnd());
        }
        LOGGER.info("查询页码：{}",req.getPage());
        LOGGER.info("每页条数：{}",req.getSize());
        PageHelper.startPage(req.getPage(),req.getSize());
        List<DailyTrainTicket> dailyTrainTicketList = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);
        PageInfo<DailyTrainTicket> pageInfo = new PageInfo<>(dailyTrainTicketList);

        LOGGER.info("总行数：{}",pageInfo.getTotal());
        LOGGER.info("总页数：{}",pageInfo.getPages());

        List<DailyTrainTicketQueryResp> list = BeanUtil.copyToList(dailyTrainTicketList, DailyTrainTicketQueryResp.class);

        PageResp<DailyTrainTicketQueryResp> pageResp = new PageResp<>();

        pageResp.setList(list);

        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }

    @Resource
    private TrainStationService trainStationService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    public void save(DailyTrainTicketSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(req, DailyTrainTicket.class);
        if (ObjectUtil.isNull(dailyTrainTicket.getId())) {
            dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.insert(dailyTrainTicket);
        } else {
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.updateByPrimaryKey(dailyTrainTicket);
        }
    }
    public void delete(long id) {
        dailyTrainTicketMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public void genDaily(DailyTrain dailyTrain,Date date,String trainCode){
        LOGGER.info("生成日期【{}】车次【{}】的余票信息开始", DateUtil.formatDate(date), trainCode);

//        删除某日某车次的余票信息
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        dailyTrainTicketMapper.deleteByExample(dailyTrainTicketExample);

//        查出某车次的所有信息
        List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);

        if(CollUtil.isEmpty(stationList)){
            LOGGER.info("该车次没有车站基础数据，生成该车次的余票信息结束");
            return;
        }

        DateTime now = DateTime.now();

        for (int i = 0; i < stationList.size(); i++) {
//            得到出发站
            TrainStation trainStationStart = stationList.get(i);
            BigDecimal sumKM=BigDecimal.ZERO;
            for (int j = i+1; j < stationList.size(); j++) {
                TrainStation trainStationEnd = stationList.get(j);

                sumKM=sumKM.add(trainStationEnd.getKm());
                DailyTrainTicket dailyTrainTicket = new DailyTrainTicket();
                dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
                dailyTrainTicket.setDate(date);
                dailyTrainTicket.setTrainCode(trainCode);
                dailyTrainTicket.setStart(trainStationStart.getName());
                dailyTrainTicket.setStartPinyin(trainStationStart.getNamePinyin());
                dailyTrainTicket.setStartTime(trainStationStart.getOutTime());
//                可以用这个这个工具转类型
//                dailyTrainTicket.setStartIndex(ByteUtil.intToByte(trainStationStart.getIndex()));
                dailyTrainTicket.setStartIndex(trainStationStart.getIndex());
                dailyTrainTicket.setEnd(trainStationEnd.getName());
                dailyTrainTicket.setEndPinyin(trainStationEnd.getNamePinyin());
                dailyTrainTicket.setEndTime(trainStationEnd.getInTime());
                dailyTrainTicket.setEndIndex(trainStationEnd.getIndex());
                trainTicket ticketCount = gettrainTicket(dailyTrain, date, trainCode, sumKM);
                dailyTrainTicket.setYdz(ticketCount.ydz());
                dailyTrainTicket.setYdzPrice(ticketCount.ydzPrice());
                dailyTrainTicket.setEdz(ticketCount.edz());
                dailyTrainTicket.setEdzPrice(ticketCount.edzPrice());
                dailyTrainTicket.setRw(ticketCount.rw());
                dailyTrainTicket.setRwPrice(ticketCount.rwPrice());
                dailyTrainTicket.setYw(ticketCount.yw());
                dailyTrainTicket.setYwPrice(ticketCount.ywPrice());
                dailyTrainTicket.setUpdateTime(now);
                dailyTrainTicketMapper.insert(dailyTrainTicket);
            }
        }
        LOGGER.info("生成日期【{}】车次【{}】的余票信息结束",DateUtil.formatDate(date), trainCode);

    }

    public DailyTrainTicket selectByUnique(Date date, String trainCode, String start, String end) {
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode)
                .andStartEqualTo(start)
                .andEndEqualTo(end);
        List<DailyTrainTicket> list = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    private  trainTicket gettrainTicket(DailyTrain dailyTrain, Date date, String trainCode, BigDecimal sumKM) {
        int ydz = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YDZ.getCode());
        int edz = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.EDZ.getCode());
        int rw = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.RW.getCode());
        int yw = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YW.getCode());
        // 票价 = 里程之和 * 座位单价 * 车次类型系数
        String trainType = dailyTrain.getType();
        // 计算票价系数：TrainTypeEnum.priceRate
        BigDecimal priceRate = EnumUtil.getFieldBy(TrainTypeEnum::getPriceRate, TrainTypeEnum::getCode, trainType);
        BigDecimal ydzPrice = sumKM.multiply(SeatTypeEnum.YDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal edzPrice = sumKM.multiply(SeatTypeEnum.EDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal rwPrice = sumKM.multiply(SeatTypeEnum.RW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal ywPrice = sumKM.multiply(SeatTypeEnum.YW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
        return new trainTicket(ydz, edz, rw, yw, ydzPrice, edzPrice, rwPrice, ywPrice);
    }

    private record trainTicket(int ydz, int edz, int rw, int yw, BigDecimal ydzPrice, BigDecimal edzPrice, BigDecimal rwPrice, BigDecimal ywPrice) {
    }
}
