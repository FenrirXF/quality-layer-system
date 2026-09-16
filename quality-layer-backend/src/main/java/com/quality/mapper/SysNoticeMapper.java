package com.quality.mapper;

import com.quality.entity.SysNotice;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysNoticeMapper {
    // 查询当前用户未读通知数量
    int countUnread(@Param("userId") Long userId);
    // 查询当前用户通知列表
    List<SysNotice> listUserNotice(@Param("userId") Long userId);
    // 标记单条通知已读
    int updateRead(@Param("id") Long id, @Param("userId") Long userId);
    // 新增通知（推送消息使用）
    int addNotice(SysNotice notice);
}
