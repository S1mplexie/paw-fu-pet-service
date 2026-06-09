-- =====================================================
-- 管理员后台管理系统 - 数据库迁移脚本（简化版）
-- 请在MySQL Workbench、Navicat或其他数据库工具中执行
-- =====================================================

USE pet_adoption;

-- 1. 添加role字段到user表
ALTER TABLE user ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '用户角色：USER-普通用户，ADMIN-管理员' AFTER email;

-- 2. 修改status字段的默认值（如果已存在则忽略错误）
-- ALTER TABLE user MODIFY COLUMN status TINYINT NOT NULL DEFAULT 1 COMMENT '账户状态：0-禁用，1-正常';

-- 3. 创建角色索引
CREATE INDEX idx_role ON user(role);

-- 4. 创建状态索引
CREATE INDEX idx_status ON user(status);

-- 5. 创建审计日志表
CREATE TABLE IF NOT EXISTS audit_log (
    id VARCHAR(32) PRIMARY KEY COMMENT '日志ID',
    operator_id VARCHAR(32) NOT NULL COMMENT '操作人ID',
    operator_name VARCHAR(50) NOT NULL COMMENT '操作人姓名',
    operator_role VARCHAR(20) NOT NULL COMMENT '操作人角色',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    operation_target VARCHAR(100) COMMENT '操作对象',
    operation_detail TEXT COMMENT '操作详情',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    result VARCHAR(20) NOT NULL DEFAULT 'SUCCESS' COMMENT '操作结果：SUCCESS-成功，FAILURE-失败',
    error_message TEXT COMMENT '错误信息',
    INDEX idx_operator_id (operator_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_operation_time (operation_time),
    INDEX idx_result (result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';

-- 6. 初始化管理员账户
-- 密码: admin123 (BCrypt加密)
INSERT INTO user (id, user_id, username, password, nickname, phone, email, role, status, create_time, update_time)
SELECT NULL, 'admin001', 'admin', '$2a$10$N.zmdr9k7uOCQv37YK0Ke.Lqp3MGJfF2dX8aP9jHn6FqGQxZvBmKa', '系统管理员', '13800138000', 'admin@pawfu.com', 'ADMIN', 1, NOW(), NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user WHERE username = 'admin');

-- 7. 验证迁移结果
SELECT '=== 迁移完成 ===' AS message;
SELECT '用户表结构:' AS info;
DESC user;
SELECT '管理员账户:' AS info;
SELECT username, nickname, role, status FROM user WHERE username = 'admin';
SELECT '审计日志表:' AS info;
SHOW TABLES LIKE 'audit_log';
