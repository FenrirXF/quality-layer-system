package com.quality.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SysNotice {
    private Long id;
    private String title;
    private String content;
    private String noticeType;
    private LocalDateTime createTime;
    private String createUser;
    private Long targetUserId;
    private Integer isRead; // 0 未读  1 已读
}
