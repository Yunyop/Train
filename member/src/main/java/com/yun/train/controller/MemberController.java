package com.yun.train.controller;

import com.yun.train.req.MemberLoginReq;
import com.yun.train.req.MemberRegisterReq;
import com.yun.train.req.MemberSendCodeReq;
import com.yun.train.resp.CommonResp;
import com.yun.train.resp.MemberLoginResp;
import com.yun.train.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Resource
    private MemberService memberService;
    @GetMapping("/count")
    public CommonResp<Integer> count() {
        int count = memberService.count();
//        CommonResp<Integer> commonResp = new CommonResp();
//        commonResp.setContent(count);
//        return commonResp;
        return new CommonResp<>(count);
    }
    @PostMapping("/register")
    public CommonResp<Long> register(@Valid MemberRegisterReq req) {
        long register = memberService.register(req);
        return new CommonResp<>(register);
    }
    @PostMapping("/send-code")
    public CommonResp<Long> sendCode(@Valid @RequestBody MemberSendCodeReq req) {
        memberService.sendCode(req);
        return new CommonResp<>();
    }
    @PostMapping("/login")
    public CommonResp<MemberLoginResp> login(@Valid MemberLoginReq req) {
        MemberLoginResp resp = memberService.login(req);
        return new CommonResp<>(resp);
    }
}
