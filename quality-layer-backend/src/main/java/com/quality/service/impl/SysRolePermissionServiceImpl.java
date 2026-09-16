package com.quality.service.impl;

import com.quality.entity.SysRolePermission;
import com.quality.mapper.SysRolePermissionMapper;
import com.quality.service.SysRolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysRolePermissionServiceImpl implements SysRolePermissionService {

    @Autowired
    private SysRolePermissionMapper mapper;

    @Override
    public int batchInsert(List<SysRolePermission> list) {
        return mapper.batchInsert(list);
    }

    @Override
    public List<Long> getPermIdByRoleId(Long roleId) {
        return mapper.selectPermIdByRoleId(roleId);
    }

    @Override
    public int deleteByRoleId(Long roleId) {
        return mapper.deleteByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRolePermission(Long roleId, List<Long> permissionIdList) {
        deleteByRoleId(roleId);
        if (permissionIdList == null || permissionIdList.isEmpty()) {
            return;
        }
        List<SysRolePermission> insertList = new ArrayList<>();
        for (Long pid : permissionIdList) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(pid);
            insertList.add(rp);
        }
        batchInsert(insertList);
    }
}