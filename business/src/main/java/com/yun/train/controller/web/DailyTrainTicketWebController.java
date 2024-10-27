package com.yun.train.controller.web;

import com.yun.train.req.DailyTrainTicketQueryReq;
import com.yun.train.req.DailyTrainTicketSaveReq;
import com.yun.train.resp.CommonResp;
import com.yun.train.resp.DailyTrainTicketQueryResp;
import com.yun.train.resp.PageResp;
import com.yun.train.service.DailyTrainTicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/web/daily-train-ticket")
public class DailyTrainTicketWebController {

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq req) {
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList(req);
        return new CommonResp<>(list);
    }

}
