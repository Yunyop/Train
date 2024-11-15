package com.yun.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yun.train.domain.Ticket;
import com.yun.train.domain.TicketExample;
import com.yun.train.mapper.TicketMapper;
import com.yun.train.req.TicketQueryReq;
import com.yun.train.req.TicketSaveReq;
import com.yun.train.resp.PageResp;
import com.yun.train.resp.TicketQueryResp;
import com.yun.train.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private static final Logger LOGGER= LoggerFactory.getLogger(TicketService.class);


    @Resource
    private TicketMapper ticketMapper;
    public void save(TicketSaveReq req) {
        DateTime now = DateTime.now();
        Ticket ticket = BeanUtil.copyProperties(req, Ticket.class);
        if (ObjectUtil.isNull(ticket.getId())) {
            ticket.setId(SnowUtil.getSnowflakeNextId());
            ticket.setCreateTime(now);
            ticket.setUpdateTime(now);
            ticketMapper.insert(ticket);
        } else {
            ticket.setUpdateTime(now);
            ticketMapper.updateByPrimaryKey(ticket);
        }
    }
    public PageResp<TicketQueryResp> queryList(TicketQueryReq req){
        TicketExample ticketExample = new TicketExample();
        ticketExample.setOrderByClause("id desc");
        TicketExample.Criteria criteria = ticketExample.createCriteria();

        LOGGER.info("查询页码：{}",req.getPage());
        LOGGER.info("每页条数：{}",req.getSize());
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Ticket> ticketList = ticketMapper.selectByExample(ticketExample);
        PageInfo<Ticket> pageInfo = new PageInfo<>(ticketList);

        LOGGER.info("总行数：{}",pageInfo.getTotal());
        LOGGER.info("总页数：{}",pageInfo.getPages());

        List<TicketQueryResp> list = BeanUtil.copyToList(ticketList, TicketQueryResp.class);

        PageResp<TicketQueryResp> pageResp = new PageResp<>();

        pageResp.setList(list);

        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }
    public void delete(long id) {
        ticketMapper.deleteByPrimaryKey(id);
    }
}
