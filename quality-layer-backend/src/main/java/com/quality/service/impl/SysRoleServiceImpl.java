package com.quality.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.quality.entity.SysRole;
import com.quality.entity.SysRolePermission;
import com.quality.mapper.SysRoleMapper;
import com.quality.mapper.SysRolePermissionMapper;
import com.quality.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRolePermissionMapper rolePermMapper;

    @Override
    public int add(SysRole sysRole) {
        sysRole.setCreateTime(java.time.LocalDateTime.now());
        return sysRoleMapper.insert(sysRole);
    }

    @Override
    public SysRole getById(Long id) {
        return sysRoleMapper.selectById(id);
    }

    @Override
    public PageInfo<SysRole> pageList(Integer pageNum, Integer pageSize, String name) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysRole> list = sysRoleMapper.selectList(name);
        PageInfo<SysRole> pageInfo = new PageInfo<>(list);
        // 填充每个角色下关联用户数量
        pageInfo.getList().forEach(role -> {
            Long userCount = sysRoleMapper.countUserByRoleCode(role.getCode());
            role.setUserCount(userCount);
        });
        return pageInfo;
    }

    @Override
    public int update(SysRole sysRole) {
        return sysRoleMapper.update(sysRole);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteById(Long id) {
        rolePermMapper.deleteByRoleId(id);
        return sysRoleMapper.deleteById(id);
    }

    @Override
    public SysRole getByCode(String code) {
        return sysRoleMapper.selectByCode(code);
    }

    @Override
    public List<Long> getPermIdsByRoleId(Long roleId) {
        // 修正：使用 sysRoleMapper
        return sysRoleMapper.selectPermIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRolePerm(Long roleId, List<Long> permIdList) {
        rolePermMapper.deleteByRoleId(roleId);
        if (permIdList != null && !permIdList.isEmpty()) {
            List<SysRolePermission> batchList = new ArrayList<>();
            for (Long pid : permIdList) {
                SysRolePermission item = new SysRolePermission();
                item.setRoleId(roleId);
                item.setPermissionId(pid);
                batchList.add(item);
            }
            rolePermMapper.batchInsert(batchList);
        }
    }
}