package com.quality.controller;

import com.quality.common.result.Result;
import com.quality.common.vo.LoginVO;
import com.quality.common.vo.UserVO;
import com.quality.entity.SysUser;
import com.quality.service.SysUserService;
import com.quality.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试环境免密登录接口
 */
@RestController
@RequestMapping("/api/test")
public class FreeLoginController {

    @Autowired
    private SysUserService sysUserService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/freeLogin")
    public Result<LoginVO> freeLogin() {
        // 演示账号 管理员 admin / admin123
        String username = "admin";
        String rawPassword = "admin123";

        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            return Result.fail("用户名不存在");
        }
        // 密码校验，和项目实现保持一致
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            return Result.fail("密码错误");
        }

        // 匹配JwtUtil参数顺序：username, role, region
        String token = JwtUtil.generateToken(
                user.getUsername(),
                user.getRole(),
                user.getRegion()
        );

        LoginVO vo = new LoginVO();
        vo.setToken(token);

        UserVO userVO = new UserVO();
        userVO.setUsername(user.getUsername());
        userVO.setName(user.getName());
        userVO.setRole(user.getRole());
        userVO.setRegion(user.getRegion());

        vo.setUser(userVO);
        return Result.success(vo);
    }
}