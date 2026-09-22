package com.quality.util;

import jakarta.servlet.http.HttpServletRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;

//获取真实客户端 IP，操作日志记录
public class IpUtil {
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");

        // 如果存在多个IP，取第一个（真实IP）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();

            // 处理IPv6本地地址
            if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
                try {
                    InetAddress localHost = InetAddress.getLocalHost();
                    ip = localHost.getHostAddress();
                } catch (UnknownHostException e) {
                    // 如果获取失败，保留原始值
                }
            }
        }

        return ip;
    }
}