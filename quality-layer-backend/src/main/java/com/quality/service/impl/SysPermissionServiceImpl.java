package com.quality.service.impl;
import com.quality.entity.SysPermission;
import com.quality.mapper.SysPermissionMapper;
import com.quality.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SysPermissionServiceImpl implements SysPermissionService {

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public List<SysPermission> getAll() {
        return sysPermissionMapper.selectAll();
    }

    @Override
    public List<SysPermission> getByParentId(Long parentId) {
        return sysPermissionMapper.selectByParentId(parentId);
    }

    @Override
    public SysPermission getById(Long id) {
        return sysPermissionMapper.selectById(id);
    }
}