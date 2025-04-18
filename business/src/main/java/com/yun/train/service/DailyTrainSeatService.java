package com.yun.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yun.train.domain.*;
import com.yun.train.mapper.DailyTrainSeatMapper;
import com.yun.train.req.DailyTrainSeatQueryReq;
import com.yun.train.req.DailyTrainSeatSaveReq;
import com.yun.train.req.SeatSellReq;
import com.yun.train.resp.PageResp;
import com.yun.train.resp.DailyTrainSeatQueryResp;
import com.yun.train.resp.SeatSellResp;
import com.yun.train.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainSeatService {

    private static final Logger LOGGER= LoggerFactory.getLogger(DailyTrainSeatService.class);


    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Resource
    private TrainSeatService trainSeatService;

    @Resource
    private TrainStationService trainStationService;

    public void save(DailyTrainSeatSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(req, DailyTrainSeat.class);
        if (ObjectUtil.isNull(dailyTrainSeat.getId())) {
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        } else {
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatMapper.updateByPrimaryKey(dailyTrainSeat);
        }
    }
    public PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req){
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.setOrderByClause("date desc, train_code asc, carriage_index asc, carriage_seat_index asc");
        DailyTrainSeatExample.Criteria criteria = dailyTrainSeatExample.createCriteria();
        if (ObjectUtil.isNotEmpty(req.getTrainCode())){
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        LOGGER.info("查询页码：{}",req.getPage());
        LOGGER.info("每页条数：{}",req.getSize());
        PageHelper.startPage(req.getPage(),req.getSize());
        List<DailyTrainSeat> dailyTrainSeatList = dailyTrainSeatMapper.selectByExample(dailyTrainSeatExample);
        PageInfo<DailyTrainSeat> pageInfo = new PageInfo<>(dailyTrainSeatList);

        LOGGER.info("总行数：{}",pageInfo.getTotal());
        LOGGER.info("总页数：{}",pageInfo.getPages());

        List<DailyTrainSeatQueryResp> list = BeanUtil.copyToList(dailyTrainSeatList, DailyTrainSeatQueryResp.class);

        PageResp<DailyTrainSeatQueryResp> pageResp = new PageResp<>();

        pageResp.setList(list);

        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }
    public void delete(long id) {
        dailyTrainSeatMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public void genDaily(Date date, String trainCode){
        LOGGER.info("生成日期【{}】车次【{}】的座位信息开始", DateUtil.formatDate(date), trainCode);

//        删除某日某车次的座位信息
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        dailyTrainSeatMapper.deleteByExample(dailyTrainSeatExample);

        List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
        String sell = StrUtil.fillBefore("",'0',stationList.size()-1);


//        查出某车次的所有信息
        List<TrainSeat> seatList = trainSeatService.selectByTrainCode(trainCode);
        if(CollUtil.isEmpty(seatList)){
            LOGGER.info("该车次没有座位基础数据，生成该车次的座位信息结束");
            return;
        }

        for (TrainSeat trainSeat:seatList){
            DateTime now = DateTime.now();
            DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(trainSeat, DailyTrainSeat.class);
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeat.setDate(date);
            dailyTrainSeat.setSell(sell);
            dailyTrainSeatMapper.insert(dailyTrainSeat);
        }
        LOGGER.info("生成日期【{}】车次【{}】的座位信息结束",DateUtil.formatDate(date), trainCode);
    }
    public int countSeat(Date date,String trainCode){
        return countSeat(date,trainCode,null);
    }

    public int countSeat(Date date, String trainCode,String seatType){

        DailyTrainSeatExample example = new DailyTrainSeatExample();
        DailyTrainSeatExample.Criteria criteria = example.createCriteria();
        criteria.andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        if (StrUtil.isNotBlank(seatType)){
            criteria.andSeatTypeEqualTo(seatType);
        }
        long l = dailyTrainSeatMapper.countByExample(example);
        if (l==0L){
            return -1;
        }
        return (int) l;
    }

    public List<DailyTrainSeat> selectByCarriage(Date date,String trainCode,Integer carriageIndex){
        DailyTrainSeatExample example = new DailyTrainSeatExample();
        example.setOrderByClause("carriage_seat_index asc");
        example.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode)
                .andCarriageIndexEqualTo(carriageIndex);
        return dailyTrainSeatMapper.selectByExample(example);
    }
    /**
     * 查询某日某车次的所有座位
     */
    public List<SeatSellResp> querySeatSell(SeatSellReq req) {
        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        LOGGER.info("查询日期【{}】车次【{}】的座位销售信息", DateUtil.formatDate(date), trainCode);
        DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
        dailyTrainSeatExample.setOrderByClause("`carriage_index` asc, carriage_seat_index asc");
        dailyTrainSeatExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        return BeanUtil.copyToList(dailyTrainSeatMapper.selectByExample(dailyTrainSeatExample), SeatSellResp.class);
    }

}
