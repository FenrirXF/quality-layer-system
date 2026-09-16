package com.quality.mapper;
import com.quality.entity.SysRolePermission;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysRolePermissionMapper {
    int batchInsert(@Param("list") List<SysRolePermission> list);

    List<Long> selectPermIdByRoleId(@Param("roleId") Long roleId);

    int deleteByRoleId(@Param("roleId") Long roleId);
}