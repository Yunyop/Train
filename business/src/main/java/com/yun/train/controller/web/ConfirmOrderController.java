package com.yun.train.controller.web;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.yun.train.exception.BusinessException;
import com.yun.train.exception.BusinessExceptionEnum;
import com.yun.train.req.ConfirmOrderDoReq;
import com.yun.train.resp.CommonResp;
import com.yun.train.service.ConfirmOrderService;
import io.netty.util.internal.ObjectUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/web/confirm-order")
public class ConfirmOrderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmOrderController.class);
    @Resource
    private ConfirmOrderService confirmOrderService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @SentinelResource(value = "/web/confirmOrder/do",
            blockHandler = "doConfirmBlock")
    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req) {
//        图形验证码校验
        String imageCodeToken = req.getImageCodeToken();
        String imageCode = req.getImageCode();
        String imageCodeRedis = stringRedisTemplate.opsForValue().get(imageCodeToken);
        LOGGER.info("从redis中获取到的验证码：{}",imageCodeRedis);
        if (ObjectUtils.isEmpty(imageCodeRedis)) {
            return new CommonResp<>(false,"验证码已过期",null);
        }
        // 验证码校验，大小写忽略，提升体验，比如Oo Vv Ww容易混
        if (!imageCodeRedis.equalsIgnoreCase(imageCode)) {
            return new CommonResp<>(false,"验证码不正确",null);
        }else {
            // 验证通过后，移除验证码
            stringRedisTemplate.delete(imageCodeToken);
        }
        confirmOrderService.doConfirm(req);
        return new CommonResp<>();
    }
    /**
     * 降级方法，需包含限流方法的所有参数和BlockException参数
     * @param req
     * @param e
     */
    public CommonResp<Object> doConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
        LOGGER.info("购票请求被限流：{}",req);
//        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
        CommonResp<Object> commonResp = new CommonResp<>();
        commonResp.setSuccess(false);
        commonResp.setMessage(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION.getDesc());
        return commonResp;
    }

}
