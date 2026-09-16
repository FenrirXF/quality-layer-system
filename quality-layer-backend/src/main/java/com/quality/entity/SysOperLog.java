package com.quality.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//操作日志
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysOperLog {
    private Long id;
    private String operator;
    private LocalDateTime operateTime;
    private String ip;
    private String org;
    private String module;
    private String action;
    private String content;
}