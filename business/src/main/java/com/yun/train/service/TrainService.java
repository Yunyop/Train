package com.yun.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yun.train.domain.Train;
import com.yun.train.domain.TrainCarriage;
import com.yun.train.domain.TrainCarriageExample;
import com.yun.train.domain.TrainExample;
import com.yun.train.exception.BusinessException;
import com.yun.train.exception.BusinessExceptionEnum;
import com.yun.train.mapper.TrainMapper;
import com.yun.train.req.TrainQueryReq;
import com.yun.train.req.TrainSaveReq;
import com.yun.train.resp.PageResp;
import com.yun.train.resp.TrainQueryResp;
import com.yun.train.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainService {

    private static final Logger LOGGER= LoggerFactory.getLogger(TrainService.class);


    @Resource
    private TrainMapper trainMapper;
    public void save(TrainSaveReq req) {
        DateTime now = DateTime.now();
        Train train = BeanUtil.copyProperties(req, Train.class);
        if (ObjectUtil.isNull(train.getId())) {
            //        保存之前，先校验唯一键是否存在
            Train trainDB = selectByUnique(req.getCode());
            if (ObjectUtil.isNotEmpty(trainDB)){
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CODE_UNIQUE_ERROR);
            }
            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainMapper.insert(train);
        } else {
            train.setUpdateTime(now);
            trainMapper.updateByPrimaryKey(train);
        }
    }

    //    唯一键查询方法
    
    private Train selectByUnique(String code) {
        TrainExample trainExample = new TrainExample();
        trainExample.createCriteria().andCodeEqualTo(code);
        List<Train> train = trainMapper.selectByExample(trainExample);
        if (CollUtil.isNotEmpty(train)){
            return train.get(0);
        }else {
            return null;
        }
    }

//    分页列表查询
    
    public PageResp<TrainQueryResp> queryList(TrainQueryReq req){
        TrainExample trainExample = new TrainExample();
        trainExample.setOrderByClause("code asc");
        TrainExample.Criteria criteria = trainExample.createCriteria();

        LOGGER.info("查询页码：{}",req.getPage());
        LOGGER.info("每页条数：{}",req.getSize());
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Train> trainList = trainMapper.selectByExample(trainExample);
        PageInfo<Train> pageInfo = new PageInfo<>(trainList);

        LOGGER.info("总行数：{}",pageInfo.getTotal());
        LOGGER.info("总页数：{}",pageInfo.getPages());

        List<TrainQueryResp> list = BeanUtil.copyToList(trainList, TrainQueryResp.class);

        PageResp<TrainQueryResp> pageResp = new PageResp<>();

        pageResp.setList(list);

        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }
    public void delete(long id) {
        trainMapper.deleteByPrimaryKey(id);
    }

    //查询所有车站
    public List<TrainQueryResp> queryAll(){
        List<Train> trainList = selectAll();
        return BeanUtil.copyToList(trainList, TrainQueryResp.class);
    }

    public List<Train> selectAll() {
        TrainExample trainExample = new TrainExample();
        trainExample.setOrderByClause("code asc");
        return trainMapper.selectByExample(trainExample);
    }
}
