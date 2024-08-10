package com.yun.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yun.train.domain.DailyTrain;
import com.yun.train.domain.DailyTrainExample;
import com.yun.train.mapper.DailyTrainMapper;
import com.yun.train.req.DailyTrainQueryReq;
import com.yun.train.req.DailyTrainSaveReq;
import com.yun.train.resp.PageResp;
import com.yun.train.resp.DailyTrainQueryResp;
import com.yun.train.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyTrainService {

    private static final Logger LOGGER= LoggerFactory.getLogger(DailyTrainService.class);


    @Resource
    private DailyTrainMapper dailyTrainMapper;
    public void save(DailyTrainSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(req, DailyTrain.class);
        if (ObjectUtil.isNull(dailyTrain.getId())) {
            dailyTrain.setId(SnowUtil.getSnowflakeNextId());
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.insert(dailyTrain);
        } else {
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.updateByPrimaryKey(dailyTrain);
        }
    }
    public PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req){
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        dailyTrainExample.setOrderByClause("id desc");
        DailyTrainExample.Criteria criteria = dailyTrainExample.createCriteria();

        LOGGER.info("查询页码：{}",req.getPage());
        LOGGER.info("每页条数：{}",req.getSize());
        PageHelper.startPage(req.getPage(),req.getSize());
        List<DailyTrain> dailyTrainList = dailyTrainMapper.selectByExample(dailyTrainExample);
        PageInfo<DailyTrain> pageInfo = new PageInfo<>(dailyTrainList);

        LOGGER.info("总行数：{}",pageInfo.getTotal());
        LOGGER.info("总页数：{}",pageInfo.getPages());

        List<DailyTrainQueryResp> list = BeanUtil.copyToList(dailyTrainList, DailyTrainQueryResp.class);

        PageResp<DailyTrainQueryResp> pageResp = new PageResp<>();

        pageResp.setList(list);

        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }
    public void delete(long id) {
        dailyTrainMapper.deleteByPrimaryKey(id);
    }
}
