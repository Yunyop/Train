package com.yun.train.controller.web;

import com.yun.train.req.TrainQueryReq;
import com.yun.train.req.TrainSaveReq;
import com.yun.train.resp.CommonResp;
import com.yun.train.resp.PageResp;
import com.yun.train.resp.TrainQueryResp;
import com.yun.train.service.TrainSeatService;
import com.yun.train.service.TrainService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/web/train")
public class TrainWebController {

    @Resource
    private TrainService trainService;

//    查询所有车站
    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> queryList() {
        List<TrainQueryResp> list = trainService.queryAll();
        return new CommonResp<>(list);
    }
}
