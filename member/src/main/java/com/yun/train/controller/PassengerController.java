package com.yun.train.controller;

import com.yun.train.context.LoginMemberContext;
import com.yun.train.req.PassengerQueryReq;
import com.yun.train.req.PassengerSaveReq;
import com.yun.train.resp.CommonResp;
import com.yun.train.resp.PageResp;
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
    public CommonResp<PageResp<PassengerQueryResp>> queryList(@Valid PassengerQueryReq req) {
        req.setMemberId(LoginMemberContext.getId());
        PageResp<PassengerQueryResp> list = passengerService.queryList(req);
        return new CommonResp<>(list);
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable long id) {
        passengerService.delete(id);
        return new CommonResp<>();
    }
    @GetMapping("/query-mine")
    public CommonResp<List<PassengerQueryResp>> queryMine() {
        List<PassengerQueryResp> list = passengerService.queryMinePassengerList();
        return new CommonResp<>(list);
    }

}
