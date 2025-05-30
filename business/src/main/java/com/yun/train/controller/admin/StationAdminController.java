package com.yun.train.controller.admin;

import com.yun.train.context.LoginMemberContext;
import com.yun.train.req.StationQueryReq;
import com.yun.train.req.StationSaveReq;
import com.yun.train.resp.CommonResp;
import com.yun.train.resp.PageResp;
import com.yun.train.resp.StationQueryResp;
import com.yun.train.resp.TrainQueryResp;
import com.yun.train.service.StationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/station")
public class StationAdminController {
    @Resource
    private StationService stationService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody StationSaveReq req) {
        stationService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<StationQueryResp>> queryList(@Valid StationQueryReq req) {
        PageResp<StationQueryResp> list = stationService.queryList(req);
        return new CommonResp<>(list);
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable long id) {
        stationService.delete(id);
        return new CommonResp<>();
    }
    //    查询所有车站
    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> queryList() {
        List<StationQueryResp> list = stationService.queryAll();
        return new CommonResp<>(list);
    }

}
