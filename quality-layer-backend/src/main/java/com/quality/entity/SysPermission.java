package com.quality.entity;

import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//菜单权限
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysPermission {
    private Long id;
    private String label;
    private Long parentId;
    private String type;
}