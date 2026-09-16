package com.quality.controller;

import com.github.pagehelper.PageInfo;
import com.quality.common.result.Result;
import com.quality.entity.SysRole;
import com.quality.entity.SysUser;
import com.quality.service.SysOperLogService;
import com.quality.service.SysRoleService;
import com.quality.util.OperLogUtil;
import com.quality.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/role")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysOperLogService sysOperLogService;

    // 分页角色列表
    @GetMapping("/page")
    public Result<PageInfo<SysRole>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String name
    ) {
        PageInfo<SysRole> page = sysRoleService.pageList(pageNum, pageSize, name);
        return Result.success(page);
    }

    // 根据id查询角色
    @GetMapping("/{id}")
    public Result<SysRole> getById(@PathVariable Long id) {
        return Result.success(sysRoleService.getById(id));
    }

    // 新增角色
    @PostMapping("/add")
    public Result<Integer> add(@RequestBody SysRole sysRole, HttpServletRequest request) {
        int rows = sysRoleService.add(sysRole);
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "角色管理", "新增", "新增角色：" + sysRole.getName());
        return Result.success(rows);
    }

    // 修改角色
    @PutMapping("/update")
    public Result<Integer> update(@RequestBody SysRole sysRole, HttpServletRequest request) {
        int rows = sysRoleService.update(sysRole);
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "角色管理", "编辑", "编辑角色：" + sysRole.getName());
        return Result.success(rows);
    }

    // 删除角色（连带删除权限关联）
    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable Long id, HttpServletRequest request) {
        int rows = sysRoleService.deleteById(id);
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "角色管理", "删除", "删除角色，ID：" + id);
        return Result.success(rows);
    }

    // 获取角色已分配的权限id集合
    @GetMapping("/permIds/{roleId}")
    public Result<List<Long>> getPermIds(@PathVariable Long roleId) {
        return Result.success(sysRoleService.getPermIdsByRoleId(roleId));
    }

    // 保存角色权限（批量分配）
    @PostMapping("/savePerm")
    public Result<Void> savePerm(@RequestParam Long roleId, @RequestBody List<Long> permIdList, HttpServletRequest request) {
        sysRoleService.saveRolePerm(roleId, permIdList);
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "角色管理", "编辑", "为角色ID[" + roleId + "]分配权限");
        return Result.success();
    }
}