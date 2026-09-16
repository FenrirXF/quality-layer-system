package com.quality.controller;

import com.github.pagehelper.PageInfo;
import com.quality.common.result.Result;
import com.quality.common.vo.ReqRegisterVO;
import com.quality.entity.SysUser;
import com.quality.service.SysOperLogService;
import com.quality.service.SysUserService;
import com.quality.util.BCryptUtil;
import com.quality.util.OperLogUtil;
import com.quality.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//用户管理
@RestController
@RequestMapping("/api/user")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysOperLogService sysOperLogService;

    // 分页列表
    @GetMapping("/page")
    public Result<PageInfo<SysUser>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String username, String role, String region
    ) {
        PageInfo<SysUser> page = sysUserService.pageList(pageNum, pageSize, username, role, region);
        return Result.success(page);
    }

    // 根据id查询单条
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        return Result.success(user);
    }

    // 新增用户
    @PostMapping("/add")
    public Result<Integer> add(@RequestBody SysUser sysUser, HttpServletRequest request) {
        // 密码加密
        sysUser.setPassword(BCryptUtil.encrypt(sysUser.getPassword()));
        int rows = sysUserService.add(sysUser);

        // 记录操作日志
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "用户管理", "新增", "新增用户：" + sysUser.getUsername());

        return Result.success(rows);
    }

    // 修改用户
    @PutMapping("/update")
    public Result<Integer> update(@RequestBody SysUser sysUser, HttpServletRequest request) {
        // 如果前端传了新密码才加密
        if (sysUser.getPassword() != null && !sysUser.getPassword().isEmpty()) {
            sysUser.setPassword(BCryptUtil.encrypt(sysUser.getPassword()));
        }
        int rows = sysUserService.update(sysUser);

        // 记录操作日志
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "用户管理", "编辑", "编辑用户：" + sysUser.getUsername());

        return Result.success(rows);
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable Long id, HttpServletRequest request) {
        int rows = sysUserService.deleteById(id);

        // 记录操作日志
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "用户管理", "删除", "删除用户，ID：" + id);

        return Result.success(rows);
    }

    // 获取当前登录用户
    @GetMapping("/current")
    public Result<SysUser> getCurrentUser() {
        // 从ThreadLocal获取登录用户
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        // 清空密码，防止前端拿到明文密码
        loginUser.setPassword(null);
        return Result.success(loginUser);
    }

    // 修改密码
    @PutMapping("/pwd")
    public Result<String> updatePassword(@Valid @RequestBody UpdatePwdDTO dto, HttpServletRequest request) {
        // 获取当前登录用户
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        // 查询数据库完整用户信息（含加密密码）
        SysUser dbUser = sysUserService.getById(loginUser.getId());

        // 1. 校验旧密码
        if (!BCryptUtil.match(dto.getOldPwd(), dbUser.getPassword())) {
            return Result.fail("原密码不正确");
        }
        // 2. 新旧密码不能相同
        if (dto.getOldPwd().equals(dto.getNewPwd())) {
            return Result.fail("新密码不能与原密码一致");
        }
        // 3. 两次新密码校验
        if (!dto.getNewPwd().equals(dto.getConfirmPwd())) {
            return Result.fail("两次输入的新密码不一致");
        }

        // 4. 加密新密码更新
        dbUser.setPassword(BCryptUtil.encrypt(dto.getNewPwd()));
        sysUserService.update(dbUser);

        // 记录操作日志
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "用户管理", "编辑", "用户修改登录密码");

        return Result.success("密码修改成功，请重新登录");
    }

    //内部类
    @Getter
    @Setter
    public static class UpdatePwdDTO {
        @NotBlank(message = "请输入原密码")
        private String oldPwd;

        @NotBlank(message = "请输入新密码")
        private String newPwd;

        @NotBlank(message = "请确认新密码")
        private String confirmPwd;

    }

    @GetMapping("/listByRole")
    public Result<List<SysUser>> listByRole(@RequestParam String role) {
        List<SysUser> userList = sysUserService.listByRoleCode(role);
        return Result.success(userList);
    }

    @PostMapping("/register")
    //不加权限注解，匿名游客可以访问
    public Result<?> register(@RequestBody @Valid ReqRegisterVO vo){
        sysUserService.register(vo);
        return Result.success("注册成功，请前往登录");
    }

    @PutMapping("/status/{id}/{status}")
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status, HttpServletRequest request) {
        sysUserService.updateStatus(id, status);
        // 操作日志记录
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        String desc = status == 1 ? "启用用户ID：" + id : "禁用用户ID：" + id;
        OperLogUtil.record(sysOperLogService, request, loginUser,
                "用户管理", "状态修改", desc);
        return Result.success("状态更新成功");
    }
}