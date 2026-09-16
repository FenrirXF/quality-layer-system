package com.quality.service;
import com.github.pagehelper.PageInfo;
import com.quality.entity.SysRole;
import java.util.List;

public interface SysRoleService {
    int add(SysRole sysRole);
    SysRole getById(Long id);
    PageInfo<SysRole> pageList(Integer pageNum, Integer pageSize, String name);
    int update(SysRole sysRole);
    int deleteById(Long id);
    SysRole getByCode(String code);
    List<Long> getPermIdsByRoleId(Long roleId);
    void saveRolePerm(Long roleId, List<Long> permIdList);
}