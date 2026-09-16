package com.quality.controller;

import com.github.pagehelper.PageInfo;
import com.quality.common.result.Result;
import com.quality.entity.SysOperLog;
import com.quality.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/operLog")
public class SysOperLogController {
    @Autowired
    private SysOperLogService sysOperLogService;

    //日志分页查询
    @GetMapping("/page")
    public Result<PageInfo<SysOperLog>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
           String operator, String module, String startTime, String endTime
    ) {
        PageInfo<SysOperLog> page = sysOperLogService.pageList(pageNum, pageSize, operator, module, startTime, endTime);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<SysOperLog> getDetail(@PathVariable Long id) {
        SysOperLog operLog = sysOperLogService.getById(id);
        return Result.success(operLog);
    }
}