package com.yun.train.controller;

import com.yun.train.req.MemberRegisterReq;
import com.yun.train.req.MemberSendCodeReq;
import com.yun.train.resp.CommonResp;
import com.yun.train.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public CommonResp<Long> register(@Valid MemberRegisterReq memberRegisterReq) {
        long register = memberService.register(memberRegisterReq);
        return new CommonResp<>(register);
    }
    @PostMapping("/send-code")
    public CommonResp<Long> sendCode(@Valid MemberSendCodeReq memberRegisterReq) {
        memberService.sendCode(memberRegisterReq);
        return new CommonResp<>();
    }
}
