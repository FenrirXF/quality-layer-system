package com.quality.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

//需求主表
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqRequirement {
    private Long id;
    private String region;
    private String techManager;
    private String devManager;
    private String devLeader;
    private String testLeader;
    private String taskName;
    private String triggerItem;
    private String conclusion;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;
    private String status;
    private String remark;
    private String createUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String updateUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}