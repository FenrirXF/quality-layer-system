package com.quality.service;
import com.quality.entity.SysPermission;
import java.util.List;

public interface SysPermissionService {
    List<SysPermission> getAll();
    List<SysPermission> getByParentId(Long parentId);
    SysPermission getById(Long id);
}