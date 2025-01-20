package com.yun.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yun.train.domain.*;
import com.yun.train.exception.BusinessException;
import com.yun.train.exception.BusinessExceptionEnum;
import com.yun.train.mapper.SkTokenMapper;
import com.yun.train.mapper.cust.SkTokenMapperCust;
import com.yun.train.req.SkTokenQueryReq;
import com.yun.train.req.SkTokenSaveReq;
import com.yun.train.resp.PageResp;
import com.yun.train.resp.SkTokenQueryResp;
import com.yun.train.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SkTokenService {

    private static final Logger LOGGER= LoggerFactory.getLogger(SkTokenService.class);

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private DailyTrainStationService dailyTrainStationService;

    @Resource
    private SkTokenMapperCust skTokenMapperCust;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 初始化
     * @param date
     * @param trainCode
     */
    public void genDaily(Date date, String trainCode){
        LOGGER.info("删除日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);

//        删除某日某车次的令牌
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        skTokenMapper.deleteByExample(skTokenExample);
            DateTime now = DateTime.now();
            SkToken skToken = new SkToken();
            skToken.setDate(date);
            skToken.setTrainCode(trainCode);
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skToken.setId(SnowUtil.getSnowflakeNextId());
            int seatCount = dailyTrainSeatService.countSeat(date,trainCode);
            LOGGER.info("车次[{}]座位数：{}",trainCode,seatCount);

            long stationCount = dailyTrainStationService.countByTrainCode(trainCode);
            LOGGER.info("车次[{}]到站数：{}",trainCode,stationCount);

            int count = (int)(seatCount*stationCount*3/4);
            LOGGER.info("车次[{}]初次生成令牌数：{}",trainCode,count);
            skToken.setCount(count);
            skTokenMapper.insertSelective(skToken);
    }


    @Resource
    private SkTokenMapper skTokenMapper;
    public void save(SkTokenSaveReq req) {
        DateTime now = DateTime.now();
        SkToken skToken = BeanUtil.copyProperties(req, SkToken.class);
        if (ObjectUtil.isNull(skToken.getId())) {
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);
        } else {
            skToken.setUpdateTime(now);
            skTokenMapper.updateByPrimaryKey(skToken);
        }
    }
    public PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq req){
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.setOrderByClause("id desc");
        SkTokenExample.Criteria criteria = skTokenExample.createCriteria();

        LOGGER.info("查询页码：{}",req.getPage());
        LOGGER.info("每页条数：{}",req.getSize());
        PageHelper.startPage(req.getPage(),req.getSize());
        List<SkToken> skTokenList = skTokenMapper.selectByExample(skTokenExample);
        PageInfo<SkToken> pageInfo = new PageInfo<>(skTokenList);

        LOGGER.info("总行数：{}",pageInfo.getTotal());
        LOGGER.info("总页数：{}",pageInfo.getPages());

        List<SkTokenQueryResp> list = BeanUtil.copyToList(skTokenList, SkTokenQueryResp.class);

        PageResp<SkTokenQueryResp> pageResp = new PageResp<>();

        pageResp.setList(list);

        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }
    public void delete(long id) {
        skTokenMapper.deleteByPrimaryKey(id);
    }

    /**
     * 校验令牌
     */
    public boolean validSkToken(Date date,String trainCode,Long memberId) {
        LOGGER.info("会员[{}]获取日期[{}]车次[{}]的令牌开始",memberId,DateUtil.formatDate(date),trainCode);
        String lockKey= DateUtil.formatDate(date)+"-"+trainCode+"-"+memberId;
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
        if(Boolean.TRUE.equals(setIfAbsent)){
            LOGGER.info("恭喜抢到令牌锁了!lockKey:{}",lockKey);
        }else {
//            只是没抢到锁，不知道票卖完没有
            LOGGER.info("很遗憾没抢到令牌锁！lockKey:{}",lockKey);
            return false;
        }
//        令牌约等于库存，令牌没有了，就不再卖票，不需要再进入购票主流程去判断库存，判断令牌肯定比判断库存更有效率
        int updateCount = skTokenMapperCust.decrease(date,trainCode);
        if (updateCount > 0) {
            return true;
        }else {
            return false;
        }
    }
}
