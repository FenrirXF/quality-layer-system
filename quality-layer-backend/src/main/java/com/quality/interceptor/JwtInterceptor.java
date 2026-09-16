package com.quality.interceptor;

import com.quality.entity.SysUser;
import com.quality.mapper.SysUserMapper;
import com.quality.util.JwtUtil;
import com.quality.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行登录接口
        String uri = request.getRequestURI();
        if (uri.contains("/api/login")) {
            return true;
        }
        // 获取token
        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);
        if (username == null) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"登录失效，请重新登录\",\"data\":null}");
            return false;
        }
        // 查询用户存入线程
        SysUser loginUser = sysUserMapper.selectByUsername(username);
        ThreadLocalUtil.setLoginUser(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ThreadLocalUtil.clear();
    }
}