package com.quality.service.impl;

import com.quality.entity.SysNotice;
import com.quality.mapper.SysNoticeMapper;
import com.quality.service.SysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SysNoticeServiceImpl implements SysNoticeService {
    @Autowired
    private SysNoticeMapper sysNoticeMapper;

    @Override
    public int countUnread(Long userId) {
        return sysNoticeMapper.countUnread(userId);
    }

    @Override
    public List<SysNotice> listUserNotice(Long userId) {
        return sysNoticeMapper.listUserNotice(userId);
    }

    @Override
    public int updateRead(Long id, Long userId) {
        return sysNoticeMapper.updateRead(id,userId);
    }

    @Override
    public int addNotice(SysNotice notice) {
        return sysNoticeMapper.addNotice(notice);
    }
}
