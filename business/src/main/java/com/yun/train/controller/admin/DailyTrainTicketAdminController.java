package com.yun.train.controller.admin;

import com.yun.train.context.LoginMemberContext;
import com.yun.train.req.DailyTrainTicketQueryReq;
import com.yun.train.req.DailyTrainTicketSaveReq;
import com.yun.train.resp.CommonResp;
import com.yun.train.resp.PageResp;
import com.yun.train.resp.DailyTrainTicketQueryResp;
import com.yun.train.service.DailyTrainTicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-ticket")
public class DailyTrainTicketAdminController {
    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainTicketSaveReq req) {
        dailyTrainTicketService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq req) {
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList(req);
        return new CommonResp<>(list);
    }
    @GetMapping("/query-list2")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList2(@Valid DailyTrainTicketQueryReq req) {
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList2(req);
        return new CommonResp<>(list);
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable long id) {
        dailyTrainTicketService.delete(id);
        return new CommonResp<>();
    }

}
