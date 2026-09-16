package com.quality.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//区域
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRegion {
    private Long id;
    private String name;
    private String techManager;
    private Integer status;
    private LocalDateTime createTime;
    private Integer userCount;
    private Integer reqCount;
}