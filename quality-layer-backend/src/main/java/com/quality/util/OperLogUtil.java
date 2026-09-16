package com.quality.util;

import com.quality.entity.SysOperLog;
import com.quality.entity.SysUser;
import com.quality.service.SysOperLogService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

//操作记录日志
public class OperLogUtil {

    public static void record(SysOperLogService service,
                              HttpServletRequest request,
                              SysUser loginUser,
                              String module,
                              String action,
                              String content) {
        if (service == null || request == null || loginUser == null) {
            return;
        }
        SysOperLog log = new SysOperLog();
        log.setOperator(loginUser.getName());
        log.setOperateTime(LocalDateTime.now());
        log.setIp(IpUtil.getClientIp(request));

        String region = loginUser.getRegion();
        if (region == null || region.isBlank()) {
            region = "总部";
        }
        log.setOrg(region);

        log.setModule(module);
        log.setAction(action);
        log.setContent(content);
        service.add(log);
    }
}