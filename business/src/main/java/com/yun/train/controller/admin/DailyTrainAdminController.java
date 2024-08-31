package com.yun.train.controller.admin;

import com.yun.train.context.LoginMemberContext;
import com.yun.train.req.DailyTrainQueryReq;
import com.yun.train.req.DailyTrainSaveReq;
import com.yun.train.resp.CommonResp;
import com.yun.train.resp.PageResp;
import com.yun.train.resp.DailyTrainQueryResp;
import com.yun.train.service.DailyTrainService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/admin/daily-train")
public class DailyTrainAdminController {
    @Resource
    private DailyTrainService dailyTrainService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainSaveReq req) {
        dailyTrainService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainQueryResp>> queryList(@Valid DailyTrainQueryReq req) {
        PageResp<DailyTrainQueryResp> list = dailyTrainService.queryList(req);
        return new CommonResp<>(list);
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable long id) {
        dailyTrainService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/gen-daily/{date}")
    public CommonResp<Object> genDaily(
            @PathVariable
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            Date date) {
        dailyTrainService.genDaily(date);
        return new CommonResp<>();
    }

}
