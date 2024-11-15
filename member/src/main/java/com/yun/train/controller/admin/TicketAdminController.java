package com.yun.train.controller.admin;

import com.yun.train.context.LoginMemberContext;
import com.yun.train.req.TicketQueryReq;
import com.yun.train.req.TicketSaveReq;
import com.yun.train.resp.CommonResp;
import com.yun.train.resp.PageResp;
import com.yun.train.resp.TicketQueryResp;
import com.yun.train.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/ticket")
public class TicketAdminController {
    @Resource
    private TicketService ticketService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryResp>> queryList(@Valid TicketQueryReq req) {
        PageResp<TicketQueryResp> list = ticketService.queryList(req);
        return new CommonResp<>(list);
    }

}
