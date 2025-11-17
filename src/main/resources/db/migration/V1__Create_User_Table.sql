-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `student_id` VARCHAR(50) NOT NULL COMMENT '学号',
    `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `avatar` VARCHAR(500) COMMENT '头像URL',
    `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '用户角色：user-普通用户, club_admin-社团管理员, system_admin-系统管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '账户状态：0-禁用，1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_student_id` (`student_id`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试数据（密码均为：123456，加盐后的MD5值）
INSERT INTO `user` (`username`, `password`, `real_name`, `student_id`, `email`, `phone`, `role`, `status`) VALUES
('admin', '7c929ed40efba1687825423bd1c19b43', '系统管理员', 'ADMIN001', 'admin@campus.com', '13800000001', 'system_admin', 1),
('test_user', '7c929ed40efba1687825423bd1c19b43', '测试用户', '20230001', 'test@campus.com', '13800000002', 'user', 1),
('club_admin', '7c929ed40efba1687825423bd1c19b43', '社团管理员', '20230002', 'club@campus.com', '13800000003', 'club_admin', 1);
