-- =========================================
-- 学校社团管理平台 - 核心数据库表结构
-- 版本: V2
-- 创建日期: 2025-11-17
-- 描述: 创建社团、成员、活动、报名、公告、通知等核心表
-- =========================================

-- =========================================
-- 1. 社团表 (club)
-- =========================================
CREATE TABLE IF NOT EXISTS `club` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '社团ID',
    `name` VARCHAR(100) NOT NULL COMMENT '社团名称',
    `description` TEXT COMMENT '社团简介',
    `logo` VARCHAR(500) COMMENT '社团图标URL',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '社团状态：pending-审核中, active-正常, disabled-已禁用',
    `create_user_id` BIGINT NOT NULL COMMENT '创建者用户ID',
    `member_count` INT NOT NULL DEFAULT 0 COMMENT '成员数量（冗余字段，用于快速查询）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`, `is_deleted`),
    KEY `idx_status` (`status`),
    KEY `idx_create_user_id` (`create_user_id`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_club_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社团表';

-- =========================================
-- 2. 社团成员表 (club_member)
-- =========================================
CREATE TABLE IF NOT EXISTS `club_member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '成员关系ID',
    `club_id` BIGINT NOT NULL COMMENT '社团ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role` VARCHAR(20) NOT NULL DEFAULT 'member' COMMENT '成员角色：member-普通成员, leader-社长/负责人, vice_leader-副社长',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '申请状态：pending-待审核, approved-已通过, rejected-已拒绝, quit-已退出',
    `join_time` DATETIME COMMENT '加入时间（审核通过时间）',
    `memo` VARCHAR(500) COMMENT '备注信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_club_user` (`club_id`, `user_id`, `is_deleted`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_role` (`role`),
    KEY `idx_join_time` (`join_time`),
    CONSTRAINT `fk_club_member_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_club_member_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社团成员表';

-- =========================================
-- 3. 活动表 (activity)
-- =========================================
CREATE TABLE IF NOT EXISTS `activity` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '活动ID',
    `club_id` BIGINT NOT NULL COMMENT '所属社团ID',
    `title` VARCHAR(200) NOT NULL COMMENT '活动标题',
    `content` TEXT NOT NULL COMMENT '活动内容详情',
    `cover` VARCHAR(500) COMMENT '活动封面图URL',
    `location` VARCHAR(200) COMMENT '活动地点',
    `start_time` DATETIME NOT NULL COMMENT '活动开始时间',
    `end_time` DATETIME NOT NULL COMMENT '活动结束时间',
    `signup_start_time` DATETIME COMMENT '报名开始时间',
    `signup_end_time` DATETIME COMMENT '报名截止时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '活动状态：draft-草稿, pending-审核中, published-已发布, cancelled-已取消, completed-已完成',
    `max_members` INT COMMENT '最大报名人数（NULL表示不限制）',
    `current_members` INT NOT NULL DEFAULT 0 COMMENT '当前报名人数（冗余字段）',
    `create_user_id` BIGINT NOT NULL COMMENT '创建者用户ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_club_id` (`club_id`),
    KEY `idx_status` (`status`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_end_time` (`end_time`),
    KEY `idx_create_user_id` (`create_user_id`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_activity_club` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_activity_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `chk_activity_time` CHECK (`end_time` >= `start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';

-- =========================================
-- 4. 活动报名表 (activity_signup)
-- =========================================
CREATE TABLE IF NOT EXISTS `activity_signup` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报名记录ID',
    `activity_id` BIGINT NOT NULL COMMENT '活动ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'registered' COMMENT '报名状态：registered-已报名, cancelled-已取消, checked_in-已签到, absent-缺席',
    `signup_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    `checkin_time` DATETIME COMMENT '签到时间',
    `remark` VARCHAR(500) COMMENT '备注信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_activity_user` (`activity_id`, `user_id`, `is_deleted`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_signup_time` (`signup_time`),
    CONSTRAINT `fk_activity_signup_activity` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_activity_signup_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动报名表';

-- =========================================
-- 5. 公告表 (announcement)
-- =========================================
CREATE TABLE IF NOT EXISTS `announcement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
    `content` TEXT NOT NULL COMMENT '公告内容',
    `type` VARCHAR(20) NOT NULL DEFAULT 'system' COMMENT '公告类型：system-系统公告, club-社团公告, activity-活动公告',
    `related_id` BIGINT COMMENT '关联ID（社团ID或活动ID，根据type类型关联）',
    `priority` TINYINT NOT NULL DEFAULT 0 COMMENT '优先级：0-普通, 1-重要, 2-紧急',
    `status` VARCHAR(20) NOT NULL DEFAULT 'published' COMMENT '状态：draft-草稿, published-已发布, archived-已归档',
    `create_user_id` BIGINT NOT NULL COMMENT '创建者用户ID',
    `publish_time` DATETIME COMMENT '发布时间',
    `expire_time` DATETIME COMMENT '过期时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_related_id` (`related_id`),
    KEY `idx_priority` (`priority`),
    KEY `idx_status` (`status`),
    KEY `idx_create_user_id` (`create_user_id`),
    KEY `idx_publish_time` (`publish_time`),
    CONSTRAINT `fk_announcement_create_user` FOREIGN KEY (`create_user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- =========================================
-- 6. 通知表 (notification)
-- =========================================
CREATE TABLE IF NOT EXISTS `notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT NOT NULL COMMENT '通知内容',
    `type` VARCHAR(20) NOT NULL COMMENT '通知类型：system-系统通知, audit-审核消息, activity-活动提醒, club-社团通知',
    `related_type` VARCHAR(20) COMMENT '关联类型：club, activity, announcement等',
    `related_id` BIGINT COMMENT '关联ID',
    `read_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '阅读标记：0-未读, 1-已读',
    `read_time` DATETIME COMMENT '阅读时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`),
    KEY `idx_read_flag` (`read_flag`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_related` (`related_type`, `related_id`),
    CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- =========================================
-- 索引优化说明
-- =========================================
-- 1. 所有外键字段都添加了索引，提升JOIN查询性能
-- 2. 状态字段(status)添加索引，支持按状态筛选
-- 3. 时间字段(create_time, start_time等)添加索引，支持按时间排序和范围查询
-- 4. 用户相关查询字段(user_id, create_user_id)添加索引
-- 5. 联合唯一索引防止重复数据(如同一用户重复加入同一社团)

-- =========================================
-- 约束说明
-- =========================================
-- 1. 外键约束确保数据完整性
-- 2. ON DELETE CASCADE: 级联删除(社团删除时删除相关成员、活动等)
-- 3. ON DELETE RESTRICT: 限制删除(有关联数据时不允许删除用户)
-- 4. CHECK约束: 保证活动结束时间>=开始时间
-- 5. UNIQUE约束: 防止重复数据(如社团名称、用户重复报名等)
