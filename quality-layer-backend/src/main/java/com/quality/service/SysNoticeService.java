package com.quality.service;
import com.quality.entity.SysNotice;
import java.util.List;

public interface SysNoticeService {
    int countUnread(Long userId);
    List<SysNotice> listUserNotice(Long userId);
    int updateRead(Long id,Long userId);
    int addNotice(SysNotice notice);
}
