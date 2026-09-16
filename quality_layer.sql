-- =====================================================================
-- 质量分层系统 数据库初始化脚本
-- 数据库：quality_layer（UTF-8 编码）
-- 适用后端：quality-layer-backend（Spring Boot + MyBatis）
-- 说明：所有表、字段均依据项目实体类与 MyBatis mapper XML 生成
-- =====================================================================

-- 1. 创建数据库（强制 UTF-8，不使用 utf8mb4）
CREATE DATABASE IF NOT EXISTS `quality_layer`
  DEFAULT CHARACTER SET utf8
  COLLATE utf8_general_ci;

USE `quality_layer`;

-- =====================================================================
-- 2. 需求主表（实体：ReqRequirement）
--    业务：按区域分层管理测试需求台账
-- =====================================================================
DROP TABLE IF EXISTS `req_requirement`;
CREATE TABLE `req_requirement` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `region`       VARCHAR(64)  DEFAULT NULL COMMENT '区域',
  `tech_manager` VARCHAR(64)  DEFAULT NULL COMMENT '技术经理',
  `dev_manager`  VARCHAR(64)  DEFAULT NULL COMMENT '开发经理',
  `dev_leader`   VARCHAR(64)  DEFAULT NULL COMMENT '开发组长',
  `test_leader`  VARCHAR(64)  DEFAULT NULL COMMENT '测试组长',
  `task_name`    VARCHAR(200) DEFAULT NULL COMMENT '任务名称',
  `trigger_item` VARCHAR(255) DEFAULT NULL COMMENT '触发项',
  `conclusion`   VARCHAR(64)  DEFAULT NULL COMMENT '结论（测试团队介入保障/研发自测负责）',
  `deadline`     DATE         DEFAULT NULL COMMENT '截止日期',
  `status`       VARCHAR(32)  DEFAULT '待评估' COMMENT '状态（待评估/进行中/已完成）',
  `remark`       VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_user`  VARCHAR(64)  DEFAULT NULL COMMENT '创建人',
  `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user`  VARCHAR(64)  DEFAULT NULL COMMENT '更新人',
  `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_req_region` (`region`),
  KEY `idx_req_status` (`status`),
  KEY `idx_req_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='需求主表';

-- =====================================================================
-- 3. 用户表（实体：SysUser）
--    密码使用 BCrypt 加密存储，禁止明文
-- =====================================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username`    VARCHAR(64) NOT NULL COMMENT '登录账号',
  `name`        VARCHAR(64) DEFAULT NULL COMMENT '姓名',
  `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt密文）',
  `role`        VARCHAR(32) DEFAULT NULL COMMENT '角色编码（如 admin）',
  `region`      VARCHAR(64) DEFAULT NULL COMMENT '所属区域',
  `status`      INT         DEFAULT 1 COMMENT '账号状态：1启用 0禁用',
  `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`),
  KEY `idx_user_role` (`role`),
  KEY `idx_user_region` (`region`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='用户表';

-- =====================================================================
-- 4. 角色表（实体：SysRole）
-- =====================================================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name`        VARCHAR(64)  NOT NULL COMMENT '角色名称',
  `code`        VARCHAR(32)  NOT NULL COMMENT '角色编码（唯一）',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
  `status`      INT          DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='角色表';

-- =====================================================================
-- 5. 菜单权限表（实体：SysPermission）
-- =====================================================================
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id`        BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `label`     VARCHAR(64) NOT NULL COMMENT '权限名称',
  `parent_id` BIGINT      DEFAULT 0 COMMENT '父权限ID，0为顶级',
  `type`      VARCHAR(32) DEFAULT 'menu' COMMENT '类型：menu菜单 button按钮',
  PRIMARY KEY (`id`),
  KEY `idx_perm_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='菜单权限表';

-- =====================================================================
-- 6. 角色-权限关联表（实体：SysRolePermission）
-- =====================================================================
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id`            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id`       BIGINT NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_id`,`permission_id`),
  KEY `idx_perm_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='角色权限关联表';

-- =====================================================================
-- 7. 区域表（实体：SysRegion）
-- =====================================================================
DROP TABLE IF EXISTS `sys_region`;
CREATE TABLE `sys_region` (
  `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name`         VARCHAR(64) NOT NULL COMMENT '区域名称',
  `tech_manager` VARCHAR(64) DEFAULT NULL COMMENT '技术经理',
  `status`       INT         DEFAULT 1 COMMENT '状态：1启用 0停用',
  `create_time`  DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_region_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='区域表';

-- =====================================================================
-- 8. 操作日志表（实体：SysOperLog）
-- =====================================================================
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `operator`     VARCHAR(64)  DEFAULT NULL COMMENT '操作人',
  `operate_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `ip`           VARCHAR(64)  DEFAULT NULL COMMENT '操作IP',
  `org`          VARCHAR(128) DEFAULT NULL COMMENT '所属组织',
  `module`       VARCHAR(64)  DEFAULT NULL COMMENT '操作模块',
  `action`       VARCHAR(64)  DEFAULT NULL COMMENT '操作动作（新增/编辑/删除）',
  `content`      VARCHAR(500) DEFAULT NULL COMMENT '操作内容',
  PRIMARY KEY (`id`),
  KEY `idx_log_operate_time` (`operate_time`),
  KEY `idx_log_module` (`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='操作日志表';

-- =====================================================================
-- 9. 公告表（实体：SysNotice）
-- =====================================================================
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title`          VARCHAR(128) DEFAULT NULL COMMENT '公告标题',
  `content`        TEXT         COMMENT '公告内容',
  `notice_type`    VARCHAR(32)  DEFAULT NULL COMMENT '公告类型',
  `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user`    VARCHAR(64)  DEFAULT NULL COMMENT '创建人',
  `target_user_id` BIGINT       DEFAULT NULL COMMENT '目标用户ID',
  `is_read`        INT          DEFAULT 0 COMMENT '是否已读：0未读 1已读',
  PRIMARY KEY (`id`),
  KEY `idx_notice_target` (`target_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='公告表';

-- =====================================================================
-- 10. 初始化数据
-- =====================================================================

-- 10.1 管理员账号：admin / admin123（密码为 BCrypt 密文，已校验通过）
INSERT INTO `sys_user` (`username`, `name`, `password`, `role`, `region`, `status`)
VALUES ('admin', '超级管理员', '$2a$10$isyRbUdwwGasAbw83fnD2e3c9.MOWyhdGlg6XxADqrfegYYuheQ2G', 'admin', NULL, 1);

-- 10.2 默认角色（admin 超级管理员 / user 普通用户，可自行增改）
INSERT INTO `sys_role` (`name`, `code`, `description`, `status`) VALUES
('超级管理员', 'admin', '拥有系统全部权限', 1),
('普通用户',   'user',  '普通使用权限',     1);

-- 10.3 默认菜单权限（与前端路由模块对应，可自行增改）
INSERT INTO `sys_permission` (`id`, `label`, `parent_id`, `type`) VALUES
(1, '需求管理',   0, 'menu'),
(2, '统计分析',   0, 'menu'),
(3, '系统管理',   0, 'menu'),
(4, '用户管理',   3, 'menu'),
(5, '角色管理',   3, 'menu'),
(6, '区域管理',   3, 'menu'),
(7, '操作日志',   3, 'menu');

-- 10.4 超级管理员绑定全部权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7);

-- 10.5 示例区域（占位数据，请按实际区域名称修改后使用）
INSERT INTO `sys_region` (`name`, `tech_manager`, `status`) VALUES
('区域一', '张三', 1),
('区域二', '李四', 1);

-- =====================================================================
-- 11. 样例需求数据（可选，便于首次启动看到页面效果；不需要可删除）
-- =====================================================================
INSERT INTO `req_requirement`
(`region`, `tech_manager`, `dev_manager`, `dev_leader`, `test_leader`,
 `task_name`, `trigger_item`, `conclusion`, `deadline`, `status`, `remark`,
 `create_user`, `create_time`)
VALUES
('区域一', '张三', '王五', '赵六', '孙七',
 '示例任务A', '需求评审', '测试团队介入保障', '2026-09-30', '进行中', '示例数据，可删除',
 'admin', NOW()),
('区域二', '李四', '钱八', '周九', '吴十',
 '示例任务B', '接口联调', '研发自测负责', '2026-10-15', '待评估', '示例数据，可删除',
 'admin', NOW());
