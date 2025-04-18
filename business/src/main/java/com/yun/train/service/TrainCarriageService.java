package com.yun.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yun.train.domain.Station;
import com.yun.train.domain.StationExample;
import com.yun.train.domain.TrainCarriage;
import com.yun.train.domain.TrainCarriageExample;
import com.yun.train.enums.SeatColEnum;
import com.yun.train.exception.BusinessException;
import com.yun.train.exception.BusinessExceptionEnum;
import com.yun.train.mapper.TrainCarriageMapper;
import com.yun.train.req.TrainCarriageQueryReq;
import com.yun.train.req.TrainCarriageSaveReq;
import com.yun.train.resp.PageResp;
import com.yun.train.resp.TrainCarriageQueryResp;
import com.yun.train.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainCarriageService {

    private static final Logger LOGGER= LoggerFactory.getLogger(TrainCarriageService.class);


    @Resource
    private TrainCarriageMapper trainCarriageMapper;
    public void save(TrainCarriageSaveReq req) {
        DateTime now = DateTime.now();

//        自动计算出列数和总座位数
        List<SeatColEnum> seatColEnums=SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(seatColEnums.size());
        req.setSeatCount(req.getColCount()*req.getRowCount());


        TrainCarriage trainCarriage = BeanUtil.copyProperties(req, TrainCarriage.class);
        if (ObjectUtil.isNull(trainCarriage.getId())) {
            //        保存之前，先校验唯一键是否存在
            TrainCarriage trainCarriageDB = selectByUnique(req.getTrainCode(), req.getIndex());
            if (ObjectUtil.isNotEmpty(trainCarriageDB)){
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CARRIAGE_INDEX_UNIQUE_ERROR);
            }
            trainCarriage.setId(SnowUtil.getSnowflakeNextId());
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.insert(trainCarriage);
        } else {
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.updateByPrimaryKey(trainCarriage);
        }
    }

    //    唯一键查询方法
    private TrainCarriage selectByUnique(String trainCode,Integer index) {
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        trainCarriageExample.createCriteria()
                .andTrainCodeEqualTo(trainCode)
                .andIndexEqualTo(index);
        List<TrainCarriage> trainCarriage = trainCarriageMapper.selectByExample(trainCarriageExample);
        if (CollUtil.isNotEmpty(trainCarriage)){
            return trainCarriage.get(0);
        }else {
            return null;
        }
    }
    public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req){
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        trainCarriageExample.setOrderByClause("id desc");
        TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
        if (ObjectUtil.isNotNull(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }

        LOGGER.info("查询页码：{}",req.getPage());
        LOGGER.info("每页条数：{}",req.getSize());
        PageHelper.startPage(req.getPage(),req.getSize());
        List<TrainCarriage> trainCarriageList = trainCarriageMapper.selectByExample(trainCarriageExample);
        PageInfo<TrainCarriage> pageInfo = new PageInfo<>(trainCarriageList);

        LOGGER.info("总行数：{}",pageInfo.getTotal());
        LOGGER.info("总页数：{}",pageInfo.getPages());

        List<TrainCarriageQueryResp> list = BeanUtil.copyToList(trainCarriageList, TrainCarriageQueryResp.class);

        PageResp<TrainCarriageQueryResp> pageResp = new PageResp<>();

        pageResp.setList(list);

        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }
    public void delete(long id) {
        trainCarriageMapper.deleteByPrimaryKey(id);
    }

//    查询所有车厢数据
    public List<TrainCarriage> selectByTrainCode(String trainCode){
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        trainCarriageExample.setOrderByClause("`index` desc");
        trainCarriageExample.createCriteria().andTrainCodeEqualTo(trainCode);
        return trainCarriageMapper.selectByExample(trainCarriageExample);
    }


}
