package com.quality.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//用户信息
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUser {
    private Long id;
    private String username;
    private String name;
    private String password;
    private String role;
    private String region;
    private Integer status;//1:账号正常启用，可以登录系统 0:账号禁用，禁止登录
    private LocalDateTime createTime;
}