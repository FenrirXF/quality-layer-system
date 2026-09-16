package com.quality.service.impl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quality.entity.SysOperLog;
import com.quality.mapper.SysOperLogMapper;
import com.quality.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SysOperLogServiceImpl implements SysOperLogService {

    @Autowired
    private SysOperLogMapper sysOperLogMapper;

    @Override
    public int add(SysOperLog operLog) {
        return sysOperLogMapper.insert(operLog);
    }

    @Override
    public PageInfo<SysOperLog> pageList(Integer pageNum, Integer pageSize, String operator, String module, String startTime, String endTime) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysOperLog> list = sysOperLogMapper.selectList(operator, module, startTime, endTime);
        return new PageInfo<>(list);
    }

    @Override
    public SysOperLog getById(Long id) {
        return sysOperLogMapper.selectById(id);
    }
}