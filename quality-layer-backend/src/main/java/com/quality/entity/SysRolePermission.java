package com.quality.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//角色权限关联
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRolePermission {
    private Long roleId;
    private Long permissionId;
}