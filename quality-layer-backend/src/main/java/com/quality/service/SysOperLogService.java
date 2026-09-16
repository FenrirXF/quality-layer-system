package com.quality.service;
import com.github.pagehelper.PageInfo;
import com.quality.entity.SysOperLog;

public interface SysOperLogService {
    int add(SysOperLog operLog);
    PageInfo<SysOperLog> pageList(Integer pageNum, Integer pageSize, String operator, String module, String startTime, String endTime);
    SysOperLog getById(Long id);
}