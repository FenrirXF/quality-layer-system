package com.quality.util;

import com.quality.entity.SysUser;

//全局拿所有登录用户，防止多线程冲突
public class ThreadLocalUtil {
    private static final ThreadLocal<SysUser> USER_HOLDER = new ThreadLocal<>();

    // 设置当前登录用户
    public static void setLoginUser(SysUser user) {
        USER_HOLDER.set(user);
    }

    // 获取当前登录用户
    public static SysUser getLoginUser() {
        return USER_HOLDER.get();
    }

    // 请求结束清除，内存泄漏
    public static void clear() {
        USER_HOLDER.remove();
    }

    public static void remove() {
        USER_HOLDER.remove();
    }
}