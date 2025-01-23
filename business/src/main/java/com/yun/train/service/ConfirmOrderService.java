package com.yun.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yun.train.context.LoginMemberContext;
import com.yun.train.domain.*;
import com.yun.train.enums.ConfirmOrderStatusEnum;
import com.yun.train.enums.RedisKeyPreEnum;
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
//import org.redisson.Redisson;
//import org.redisson.RedissonRedLock;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
//import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private AfterConfirmOrderService afterConfirmOrderService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private SkTokenService skTokenService;

//    @Autowired
//    private RedissonClient redissonClient;


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


    @SentinelResource(value = "BeforeDoConfirm",blockHandler = "doConfirmBlock")
    public void doConfirm(ConfirmOrderDoReq req) {

//        校验令牌余量
//        boolean validSkToken=skTokenService.validSkToken(req.getDate(),req.getTrainCode(),req.getMemberId());
//        if (validSkToken) {
//            LOGGER.info("令牌校验通过");
//        }else {
//            LOGGER.info("令牌校验不通过");
//            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
//        }
//
//        获取分布式锁
        String lockKey= RedisKeyPreEnum.CONFIRM_ORDER + "-" + DateUtil.formatDate(req.getDate())+"-"+req.getTrainCode();
//        setIfabsent就是对应redis的setnx
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
        if(Boolean.TRUE.equals(setIfAbsent)){
            LOGGER.info("恭喜抢到锁了!lockKey:{}",lockKey);
        }else {
//            只是没抢到锁，不知道票卖完没有
            LOGGER.info("很遗憾没抢到锁！lockKey:{}",lockKey);
            throw
                    new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
        }

//        RLock lock = null;

        /*

         */
        try {
//            // 使用redisson，自带看门狗
//            lock = redissonClient.getLock(lockKey);
//
////            RedissonRedLock redissonRedLock = new RedissonRedLock(lock,lock,lock);
////            boolean tryLock1 = redissonRedLock.tryLock(0,TimeUnit.SECONDS);
//            /**
//             waitTime – the maximum time to acquire the lock 等待获取锁时间(最大尝试获得锁的时间)，超时返回false
//             leaseTime – lease time 锁时长，即n秒后自动释放锁
//             time unit – time unit 时间单位
//             */
////             boolean tryLock = lock.tryLock(30, 10, TimeUnit.SECONDS); // 不带看门狗
//            boolean tryLock =lock.tryLock(0,TimeUnit.SECONDS);//带看门狗
//            if (tryLock) {
//                LOGGER.info("恭喜，抢到锁了！");
//                // 可以把下面这段放开，只用一个线程来测试，看看redisson的看门狗效果
////                 for (int i = 0; i < 30; i++) {
////                     Long expire = redisTemplate.opsForValue().getOperations().getExpire(lockKey);
////                     LOGGER.info("锁过期时间还有：{}", expire);
////                     Thread.sleep(1000);
////                 }
//            }else{
//////            只是没抢到锁，不知道票卖完没有
//            LOGGER.info("很遗憾没抢到锁");
//            throw
//                    new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
//        }
////                省略业务数据校验


            Date date = req.getDate();
            String trainCode = req.getTrainCode();
            String start = req.getStart();
            String end = req.getEnd();
            List<ConfirmOrderTicketReq> tickets = req.getTickets();
//
////        保存确认订单表，状态初始
//            DateTime now = DateTime.now();
//            ConfirmOrder confirmOrder = new ConfirmOrder();
//            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
//            confirmOrder.setCreateTime(now);
//            confirmOrder.setUpdateTime(now);
//            confirmOrder.setMemberId(req.getMemberId());
//            confirmOrder.setDate(date);
//            confirmOrder.setTrainCode(trainCode);
//            confirmOrder.setStart(start);
//            confirmOrder.setEnd(end);
//            confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
//            confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
//            confirmOrder.setTickets(JSON.toJSONString(tickets));
//            confirmOrderMapper.insert(confirmOrder);

//            从数据库里面查出订单
            ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
            confirmOrderExample.setOrderByClause("id asc");
            ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();
            criteria.andDateEqualTo(req.getDate())
                    .andTrainCodeEqualTo(req.getTrainCode())
                    .andMemberIdEqualTo(req.getMemberId())
                    .andStatusEqualTo(ConfirmOrderStatusEnum.INIT.getCode());
            List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExampleWithBLOBs(confirmOrderExample);
            ConfirmOrder confirmOrder;
            if (CollUtil.isEmpty(confirmOrderList)) {
                LOGGER.info("找不到原始订单，结束");
                return;
            }else {
                LOGGER.info("本次处理{}条确认订单",confirmOrderList.size());
                confirmOrder = confirmOrderList.get(0);
            }

//        查出余票记录，需要得到真实库存
            DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date,trainCode,start,end);
            LOGGER.info("查出余票记录:{}",dailyTrainTicket);

//        预扣减库存，并判断余票是否足够
            reduceTickets(req, dailyTrainTicket);
//        最终选票结果
            List<DailyTrainSeat> finalSeatList=new ArrayList<>();

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
                        finalSeatList,
                        date,
                        trainCode
                        ,ticketReq0.getSeatTypeCode()
                        ,ticketReq0.getSeat().split("")[0]//从A1得到A
                        ,offsetList
                        ,dailyTrainTicket.getStartIndex()
                        ,dailyTrainTicket.getEndIndex()
                );

            }
            else {

                LOGGER.info("本次购票没有选座");
                for (ConfirmOrderTicketReq ticketReq :tickets) {
                    getSeat(
                            finalSeatList,
                            date,
                            trainCode
                            ,ticketReq0.getSeatTypeCode()
                            ,null
                            ,null
                            ,dailyTrainTicket.getStartIndex()
                            ,dailyTrainTicket.getEndIndex()
                    );
                }
            }
            LOGGER.info("最终购票:{}",finalSeatList);

//        选中座位后事务处理
//            座位表修改余票sell
//            为会员增加购票记录
//            更新确认订单为成功
            try {
                afterConfirmOrderService.afterDoConfirm(dailyTrainTicket,finalSeatList,tickets,confirmOrder);
            } catch (Exception e) {
                LOGGER.info("保存购票信息失败",e);
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);
            }
//            LOGGER.info("购票流程结束，释放锁!lockKey:{}",lockKey);
//            redisTemplate.delete(lockKey);
//        } catch (InterruptedException e) {
//
//            LOGGER.info("购票异常",e);
        } finally {
//            LOGGER.info("购票流程结束，释放锁!lockKey:{}",lockKey);
//            redisTemplate.delete(lockKey);
//            if (lock != null&&lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
        }
    }

    /**
     * 挑座位，如果有选座位，则一次性挑完，如果无选座，则一个一个挑
     * @param date
     * @param trainCode
     * @param seatType
     * @param column
     * @param offsetList
     */

    private void getSeat(List<DailyTrainSeat> finalSeatList, Date date, String trainCode, String seatType, String column, List<Integer> offsetList, Integer startIndex, Integer endIndex) {
        List<DailyTrainSeat> getSeatList = new ArrayList<>();
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOGGER.info("共查出{}个符合条件的车厢", carriageList.size());

        // 一个车箱一个车箱的获取座位数据
        for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
            LOGGER.info("开始从车厢{}选座", dailyTrainCarriage.getIndex());
            getSeatList = new ArrayList<>();
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
            LOGGER.info("车厢{}的座位数：{}", dailyTrainCarriage.getIndex(), seatList.size());
            for (int i = 0; i < seatList.size(); i++) {
                DailyTrainSeat dailyTrainSeat = seatList.get(i);
                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();
                String col = dailyTrainSeat.getCol();

                // 判断当前座位不能被选中过
                boolean alreadyChooseFlag = false;
                for (DailyTrainSeat finalSeat : finalSeatList){
                    if (finalSeat.getId().equals(dailyTrainSeat.getId())) {
                        alreadyChooseFlag = true;
                        break;
                    }
                }
                if (alreadyChooseFlag) {
                    LOGGER.info("座位{}被选中过，不能重复选中，继续判断下一个座位", seatIndex);
                    continue;
                }

                // 判断column，有值的话要比对列号
                if (StrUtil.isBlank(column)) {
                    LOGGER.info("无选座");
                } else {
                    if (!column.equals(col)) {
                        LOGGER.info("座位{}列值不对，继续判断下一个座位，当前列值：{}，目标列值：{}", seatIndex, col, column);
                        continue;
                    }
                }

                boolean isChoose = calSell(dailyTrainSeat, startIndex, endIndex);
                if (isChoose) {
                    LOGGER.info("选中座位");
                    getSeatList.add(dailyTrainSeat);
                } else {
                    continue;
                }

                // 根据offset选剩下的座位
                boolean isGetAllOffsetSeat = true;
                if (CollUtil.isNotEmpty(offsetList)) {
                    LOGGER.info("有偏移值：{}，校验偏移的座位是否可选", offsetList);
                    // 从索引1开始，索引0就是当前已选中的票
                    for (int j = 1; j < offsetList.size(); j++) {
                        Integer offset = offsetList.get(j);
                        // 座位在库的索引是从1开始
                        // int nextIndex = seatIndex + offset - 1;
                        int nextIndex = i + offset;

                        // 有选座时，一定是在同一个车箱
                        if (nextIndex >= seatList.size()) {
                            LOGGER.info("座位{}不可选，偏移后的索引超出了这个车箱的座位数", nextIndex);
                            isGetAllOffsetSeat = false;
                            break;
                        }

                        DailyTrainSeat nextDailyTrainSeat = seatList.get(nextIndex);
                        boolean isChooseNext = calSell(nextDailyTrainSeat, startIndex, endIndex);
                        if (isChooseNext) {
                            LOGGER.info("座位{}被选中", nextDailyTrainSeat.getCarriageSeatIndex());
                            getSeatList.add(nextDailyTrainSeat);
                        } else {
                            LOGGER.info("座位{}不可选", nextDailyTrainSeat.getCarriageSeatIndex());
                            isGetAllOffsetSeat = false;
                            break;
                        }
                    }
                }
                if (!isGetAllOffsetSeat) {
                    getSeatList = new ArrayList<>();
                    continue;
                }

                // 保存选好的座位
                finalSeatList.addAll(getSeatList);
                return;
            }
        }
    }

    /**
     * 计算某座位在区间内是否可卖
     * 例：sell=10001，本次购买区间站1~4，则区间已售000
     * 全部是0，表示这个区间可买；只要有1，就表示区间内已售过票
     *
     * 选中后，要计算购票后的sell，比如原来是10001，本次购买区间站1~4
     * 方案：构造本次购票造成的售卖信息01110，和原sell 10001按位与，最终得到11111
     */
    private boolean calSell(DailyTrainSeat dailyTrainSeat, Integer startIndex, Integer endIndex) {
        // 00001, 00000
        String sell = dailyTrainSeat.getSell();
        //  000, 000
        String sellPart = sell.substring(startIndex, endIndex);
        if (Integer.parseInt(sellPart) > 0) {
            LOGGER.info("座位{}在本次车站区间{}~{}已售过票，不可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            return false;
        } else {
            LOGGER.info("座位{}在本次车站区间{}~{}未售过票，可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            //  111,   111
            String curSell = sellPart.replace('0', '1');
            // 0111,  0111
            curSell = StrUtil.fillBefore(curSell, '0', endIndex);
            // 01110, 01110
            curSell = StrUtil.fillAfter(curSell, '0', sell.length());

            // 当前区间售票信息curSell 01110与库里的已售信息sell 00001按位与，即可得到该座位卖出此票后的售票详情
            // 15(01111), 14(01110 = 01110|00000)
            int newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
            //  1111,  1110
            String newSell = NumberUtil.getBinaryStr(newSellInt);
            // 01111, 01110
            newSell = StrUtil.fillBefore(newSell, '0', sell.length());
            LOGGER.info("座位{}被选中，原售票信息：{}，车站区间：{}~{}，即：{}，最终售票信息：{}"
                    , dailyTrainSeat.getCarriageSeatIndex(), sell, startIndex, endIndex, curSell, newSell);
            dailyTrainSeat.setSell(newSell);
            return true;

        }
    }

    private static void reduceTickets(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq : req.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            switch (seatTypeEnum) {
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);
                }
            }
        }
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
