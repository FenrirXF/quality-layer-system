package com.quality.mapper;

import com.quality.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysUserMapper {
    int insert(SysUser user);

    SysUser selectById(Long id);

    List<SysUser> selectList(
            @Param("username") String username,
            @Param("role") String role,
            @Param("region") String region
    );

    int update(SysUser user);

    int deleteById(Long id);

    SysUser selectByUsername(String username);

    List<SysUser> selectByRoleCode(@Param("role") String role);

    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
}