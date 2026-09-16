package com.quality.controller;

import com.quality.common.result.Result;
import com.quality.entity.SysNotice;
import com.quality.entity.SysUser;
import com.quality.service.SysNoticeService;
import com.quality.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/system/notice")
public class SysNoticeController {
    @Autowired
    private SysNoticeService sysNoticeService;

    // 获取未读通知数量（右上角红点）
    @GetMapping("/unreadCount")
    public Result<Integer> getUnreadCount(){
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        int count = sysNoticeService.countUnread(loginUser.getId());
        return Result.success(count);
    }

    // 获取通知列表（弹窗展示）
    @GetMapping("/list")
    public Result<List<SysNotice>> noticeList(){
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        List<SysNotice> list = sysNoticeService.listUserNotice(loginUser.getId());
        return Result.success(list);
    }

    // 标记通知已读
    @PutMapping("/read/{id}")
    public Result<Integer> read(@PathVariable Long id){
        SysUser loginUser = ThreadLocalUtil.getLoginUser();
        int rows = sysNoticeService.updateRead(id,loginUser.getId());
        return Result.success(rows);
    }
}
