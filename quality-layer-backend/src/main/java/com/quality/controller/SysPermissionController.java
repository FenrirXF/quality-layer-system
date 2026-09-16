package com.quality.controller;

import com.quality.common.result.Result;
import com.quality.entity.SysPermission;
import com.quality.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/permission")
public class SysPermissionController {
    @Autowired
    private SysPermissionService sysPermissionService;

    // 查询全部菜单（构建树形）
    @GetMapping("/tree")
    public Result<List<SysPermission>> getAll() {
        return Result.success(sysPermissionService.getAll());
    }

    // 根据父id查子菜单
    @GetMapping("/listByParent")
    public Result<List<SysPermission>> listByParent(Long parentId) {
        return Result.success(sysPermissionService.getByParentId(parentId));
    }

    // 单条详情
    @GetMapping("/{id}")
    public Result<SysPermission> getById(Long id) {
        return Result.success(sysPermissionService.getById(id));
    }
}