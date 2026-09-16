package com.quality.controller;

import com.github.pagehelper.PageInfo;
import com.quality.common.result.Result;
import com.quality.entity.SysRegion;
import com.quality.entity.SysUser;
import com.quality.service.SysOperLogService;
import com.quality.service.SysRegionService;
import com.quality.util.OperLogUtil;
import com.quality.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.swing.plaf.synth.Region;
import java.util.List;

@RestController
@RequestMapping("/api/system/region")
public class SysRegionController {
    @Autowired
    private SysRegionService sysRegionService;

    @Autowired
    private SysOperLogService sysOperLogService;

    @GetMapping("/page")
    public Result<PageInfo<SysRegion>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String name, Integer status
    ) {
        PageInfo<SysRegion> page = sysRegionService.pageList(pageNum, pageSize, name, status);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<SysRegion> getById(@PathVariable Long id) {
        return Result.success(sysRegionService.getById(id));
    }

    @GetMapping("/listAll")
    public Result<List<SysRegion>> listAll(){
        List<SysRegion> list = sysRegionService.list();
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<Integer> add(@RequestBody SysRegion sysRegion, HttpServletRequest request) {
        int rows = sysRegionService.add(sysRegion);
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "区域管理", "新增", "新增区域：" + sysRegion.getName());
        return Result.success(rows);
    }

    @PutMapping("/update")
    public Result<Integer> update(@RequestBody SysRegion sysRegion, HttpServletRequest request) {
        int rows = sysRegionService.update(sysRegion);
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "区域管理", "编辑", "编辑区域：" + sysRegion.getName());
        return Result.success(rows);
    }

    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable Long id, HttpServletRequest request) {
        int rows = sysRegionService.deleteById(id);
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "区域管理", "删除", "删除区域，ID：" + id);
        return Result.success(rows);
    }

    @GetMapping("/enableList")
    public Result<List<SysRegion>> enableList() {
        List<SysRegion> list = sysRegionService.listEnable();
        return Result.success(list);
    }
}