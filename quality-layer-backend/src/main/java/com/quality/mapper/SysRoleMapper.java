package com.quality.mapper;

import com.quality.entity.SysRole;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysRoleMapper {
    int insert(SysRole role);

    SysRole selectById(Long id);

    List<SysRole> selectList(@Param("name") String name);

    int update(SysRole role);

    int deleteById(Long id);

    SysRole selectByCode(String code);

    List<Long> selectPermIdsByRoleId(@Param("roleId") Long roleId);

    Long countUserByRoleCode(@Param("roleCode") String roleCode);
}