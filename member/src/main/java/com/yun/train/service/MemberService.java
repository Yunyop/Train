package com.yun.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.yun.train.domain.Member;
import com.yun.train.domain.MemberExample;
import com.yun.train.exception.BusinessException;
import com.yun.train.exception.BusinessExceptionEnum;
import com.yun.train.mapper.MemberMapper;
import com.yun.train.req.MemberLoginReq;
import com.yun.train.req.MemberRegisterReq;
import com.yun.train.req.MemberSendCodeReq;
import com.yun.train.resp.MemberLoginResp;
import com.yun.train.util.JwtUtil;
import com.yun.train.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberService.class);
    @Resource
     private MemberMapper memberMapper;
    public int count() {
        return Math.toIntExact(memberMapper.countByExample(null));
    }
    public long register(MemberRegisterReq memberRegisterReq){
        String mobile = memberRegisterReq.getMobile();
        Member memberDB = selectBymobile(mobile);
        if(ObjectUtil.isNotNull(memberDB)){
//            return members.get(0).getId();
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }
        Member member=new Member();
        member.setId(SnowUtil.getSnowflakeNextId());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }

    public void sendCode(MemberSendCodeReq memberSendCodeReq){
        String mobile = memberSendCodeReq.getMobile();
        Member memberDB = selectBymobile(mobile);
//        如果手机号不存在，则插入一条记录
        if(ObjectUtil.isNull(memberDB)){
            LOGGER.info("如果手机号不存在，则插入一条记录");
            Member member=new Member();
            member.setId(SnowUtil.getSnowflakeNextId());
            member.setMobile(mobile);
            memberMapper.insert(member);
        }else {
            LOGGER.info("手机号存在，不插入记录");
        }
//        生成验证码
//        String code= RandomUtil.randomString(4);
        String code="8888";
        LOGGER.info("生成短信验证码：{}",code);
//        保存短信记录表：手机号，短信验证码，有效期，是否已使用，业务类型，发送时间，使用时间
        LOGGER.info("保存短信记录表");
//            MemberSendCodeReq memberSendCodeReq=new MemberSendCodeReq();

//        对接短信通道，发送短信
        LOGGER.info("对接短信通道");

    }

    public MemberLoginResp login(MemberLoginReq memberLoginReq){
        String mobile = memberLoginReq.getMobile();
        String code = memberLoginReq.getCode();
        Member memberDB = selectBymobile(mobile);
//        如果手机号不存在，则插入一条记录
        if(ObjectUtil.isNull(memberDB)){
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }
//        校验短信验证码
        if (!"8888".equals(code)){
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR);
        }
        MemberLoginResp memberLoginResp = BeanUtil.copyProperties(memberDB, MemberLoginResp.class);
        String token = JwtUtil.createToken(memberLoginResp.getId(), memberLoginResp.getMobile());
        memberLoginResp.setToken(token);
        return memberLoginResp;
    }

    private Member selectBymobile(String mobile) {
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(memberExample);
        if(CollUtil.isEmpty(members)){
            return null;
        }else {
            return members.get(0);
        }
    }

}
