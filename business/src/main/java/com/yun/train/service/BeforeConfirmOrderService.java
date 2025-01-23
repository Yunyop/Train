package com.yun.train.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.yun.train.enums.RedisKeyPreEnum;
import com.yun.train.exception.BusinessException;
import com.yun.train.exception.BusinessExceptionEnum;
import com.yun.train.feign.MemberFeign;
import com.yun.train.mapper.ConfirmOrderMapper;
import com.yun.train.mapper.DailyTrainSeatMapper;
import com.yun.train.mapper.cust.DailyTrainTicketMapperCust;
import com.yun.train.req.ConfirmOrderDoReq;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BeforeConfirmOrderService {

    private static final Logger LOGGER= LoggerFactory.getLogger(BeforeConfirmOrderService.class);


    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    @Resource
    private DailyTrainTicketMapperCust dailyTrainTicketMapperCust;

    @Resource
    private MemberFeign memberFeign;

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private SkTokenService skTokenService;


    @SentinelResource(value = "beforeDoConfirm",blockHandler = "beforeDoConfirmBlock")
    public void beforeDoConfirm(ConfirmOrderDoReq req) {

//        校验令牌余量
        boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), req.getMemberId());
        if (validSkToken) {
            LOGGER.info("令牌校验通过");
        } else {
            LOGGER.info("令牌校验不通过");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
        }

//        获取车次锁
        String lockKey = RedisKeyPreEnum.CONFIRM_ORDER + "-" + DateUtil.formatDate(req.getDate()) + "-" + req.getTrainCode();
//        setIfabsent就是对应redis的setnx
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(setIfAbsent)) {
            LOGGER.info("恭喜抢到锁了!lockKey:{}", lockKey);
        } else {
//            只是没抢到锁，不知道票卖完没有
            LOGGER.info("很遗憾没抢到锁！lockKey:{}", lockKey);
            throw
                    new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
        }
//        可以购票：TODO：发送MQ，等待出票
        LOGGER.info("准备发出MQ，等待出票");
    }

    /**
     * 降级方法，需包含限流方法的所有参数和BlockException参数
     * @param req
     * @param e
     */
    public void doConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        LOGGER.info("购票请求被限流：{}", req);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }

}
