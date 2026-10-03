package com.quality.interceptor;

import cn.hutool.json.JSONUtil;
import com.quality.entity.SysUser;
import com.quality.mapper.SysUserMapper;
import com.quality.util.JwtUtil;
import com.quality.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行登录接口
        String uri = request.getRequestURI();
        if (uri.contains("/api/login")) {
            return true;
        }
        // 获取token
        String token = request.getHeader("token");

        // 判断token为空
        if(token == null || token.isBlank()){
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"登录失效，请重新登录\",\"data\":null}");
            return false;
        }
        String username = JwtUtil.getUsername(token);
        if (username == null) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"登录失效，请重新登录\",\"data\":null}");
            return false;
        }

        // Redis 校验会话是否存在（登出/被踢后这里 key 已删 → 拦截）
        Boolean sessionExist = stringRedisTemplate.hasKey("login:token:" + username);
        if (sessionExist == null || !sessionExist) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"登录已失效，请重新登录\",\"data\":null}");
            return false;
        }

        // 查询用户存入线程
        SysUser loginUser = null;
        String userJson = stringRedisTemplate.opsForValue().get("login:user:" + username);
        if (userJson != null) {
            loginUser = JSONUtil.toBean(userJson, SysUser.class);   // JSON字符串 → SysUser对象
        }
        // 缓存 miss（过期/被删）：查库兜底，并回填缓存
        if (loginUser == null) {
            loginUser = sysUserMapper.selectByUsername(username);
            if (loginUser != null) {
                stringRedisTemplate.opsForValue().set(
                        "login:user:" + username, JSONUtil.toJsonStr(loginUser), 2, TimeUnit.HOURS);
            }
        }

        ThreadLocalUtil.setLoginUser(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ThreadLocalUtil.clear();
    }
}