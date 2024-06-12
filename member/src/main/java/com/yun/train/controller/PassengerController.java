package com.yun.train.controller;

import com.yun.train.context.LoginMemberContext;
import com.yun.train.req.PassengerQueryReq;
import com.yun.train.req.PassengerSaveReq;
import com.yun.train.resp.CommonResp;
import com.yun.train.resp.PassengerQueryResp;
import com.yun.train.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passenger")
public class PassengerController {
    @Resource
    private PassengerService passengerService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody PassengerSaveReq req) {
        passengerService.save(req);
        return new CommonResp<>();
    }
    @GetMapping("/query-list")
    public CommonResp<List<PassengerQueryResp>> queryList(@Valid PassengerQueryReq req) {
        req.setMemberId(LoginMemberContext.getId());
        List<PassengerQueryResp> list = passengerService.queryList(req);
        return new CommonResp<>(list);
    }

}
