package com.quality.controller;

import com.quality.common.dto.LoginDTO;
import com.quality.common.result.Result;
import com.quality.common.vo.LoginVO;
import com.quality.common.vo.UserVO;
import com.quality.entity.SysUser;
import com.quality.service.SysOperLogService;
import com.quality.service.SysUserService;
import com.quality.util.BCryptUtil;
import com.quality.util.JwtUtil;
import com.quality.util.OperLogUtil;
import com.quality.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import cn.hutool.json.JSONUtil;
import java.util.concurrent.TimeUnit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//登录登出
@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysOperLogService sysOperLogService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        SysUser user = sysUserService.getByUsername(loginDTO.getUsername());
        // 用户不存在
        if (user == null) {
            return Result.fail("用户名不存在");
        }

        // 密码校验
        if (!BCryptUtil.match(loginDTO.getPassword(), user.getPassword())) {
            return Result.fail("密码错误");
        }
        // 账号禁用校验
        if (user.getStatus() == 0) {
            return Result.fail("该账号已被禁用");
        }
        // 生成token返回
        String token = JwtUtil.generateToken(user.getUsername(), user.getRole(), user.getRegion());
        // 存 token（字符串），2小时过期，和 JWT 有效期一致
        stringRedisTemplate.opsForValue().set(
                "login:token:" + user.getUsername(), token, 2, TimeUnit.HOURS);
        // 存用户信息（JSON字符串），2小时过期，拦截器从这里取，不用每次查库
        stringRedisTemplate.opsForValue().set(
                "login:user:" + user.getUsername(), JSONUtil.toJsonStr(user), 2, TimeUnit.HOURS);

        // 组装VO返回token与用户信息
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);

        UserVO userVO = new UserVO();
        userVO.setUsername(user.getUsername());
        userVO.setName(user.getName());
        userVO.setRole(user.getRole());
        userVO.setRegion(user.getRegion());

        loginVO.setUser(userVO);

        // 记录操作日志
        OperLogUtil.record(sysOperLogService, request, user,
                "系统登录", "登录", user.getName() + "账号登录系统");

        return Result.success(loginVO);
    }

    //登出
    @PostMapping("/logout")
    public Result<String> logout() {
        // 从 ThreadLocal 拿当前登录用户
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        if (loginUser != null) {
            // 删除 Redis 会话 → token 立即失效
            stringRedisTemplate.delete("login:token:" + loginUser.getUsername());
            stringRedisTemplate.delete("login:user:" + loginUser.getUsername());
        }
        ThreadLocalUtil.remove();
        return Result.success("登出成功");
    }
}