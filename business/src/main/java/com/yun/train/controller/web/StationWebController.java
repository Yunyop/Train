package com.yun.train.controller.web;

import com.yun.train.req.StationQueryReq;
import com.yun.train.req.StationSaveReq;
import com.yun.train.resp.CommonResp;
import com.yun.train.resp.PageResp;
import com.yun.train.resp.StationQueryResp;
import com.yun.train.service.StationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/web/station")
public class StationWebController {

    @Resource
    private StationService stationService;

    //    查询所有车站
    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> queryList() {
        List<StationQueryResp> list = stationService.queryAll();
        return new CommonResp<>(list);
    }

}
