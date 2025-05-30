//package com.yun.train.mq;
//
// import com.alibaba.fastjson.JSON;
// import com.yun.train.domain.ConfirmOrder;
// import com.yun.train.dto.ConfirmOrderMQDto;
// import com.yun.train.req.ConfirmOrderDoReq;
// import com.yun.train.service.ConfirmOrderService;
// import jakarta.annotation.Resource;
// import org.apache.rocketmq.common.message.MessageExt;
// import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
// import org.apache.rocketmq.spring.core.RocketMQListener;
// import org.checkerframework.checker.units.qual.A;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.slf4j.MDC;
// import org.springframework.stereotype.Service;
//
// @Service
// @RocketMQMessageListener(consumerGroup = "default", topic = "CONFIRM_ORDER")
// public class ConfirmOrderConsumer implements RocketMQListener<MessageExt> {
//
//     private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmOrderConsumer.class);
//
//     @Resource
//     private ConfirmOrderService confirmOrderService;
//
//     @Override
//     public void onMessage(MessageExt messageExt) {
//         byte[] body = messageExt.getBody();
//         ConfirmOrderMQDto confirmOrderMQDto = JSON.parseObject(new String(body), ConfirmOrderMQDto.class);
//         MDC.put("LOG_ID", confirmOrderMQDto.getLogId());
//         LOGGER.info("ROCKETMQ收到消息：{}", new String(body));
//         confirmOrderService.doConfirm(confirmOrderMQDto);
//     }
// }
