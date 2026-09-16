package com.quality.mapper;
import com.quality.entity.SysPermission;
import java.util.List;

public interface SysPermissionMapper {
    List<SysPermission> selectAll();
    List<SysPermission> selectByParentId(Long parentId);
    SysPermission selectById(Long id);
}