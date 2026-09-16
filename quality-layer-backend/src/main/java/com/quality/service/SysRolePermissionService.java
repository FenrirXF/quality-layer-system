package com.quality.service;
import com.quality.entity.SysRolePermission;
import java.util.List;

public interface SysRolePermissionService {
    int batchInsert(List<SysRolePermission> list);
    List<Long> getPermIdByRoleId(Long roleId);
    int deleteByRoleId(Long roleId);
    void saveRolePermission(Long roleId, List<Long> permissionIdList);
}