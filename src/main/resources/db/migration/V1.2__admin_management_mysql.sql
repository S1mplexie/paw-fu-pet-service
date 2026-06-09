-- =====================================================
-- 管理员后台管理系统 - 数据库迁移脚本（MySQL兼容版）
-- 版本: V1.2
-- 描述: 添加管理员权限体系和审计日志表
-- =====================================================

USE pet_adoption;

-- 1. 扩展用户表，添加角色和状态字段
-- 先检查字段是否存在，如果不存在则添加
SET @dbname = DATABASE();
SET @tablename = 'user';

-- 添加role字段
SET @columnname = 'role';
SET @preparedStatement = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = @dbname 
     AND TABLE_NAME = @tablename 
     AND COLUMN_NAME = @columnname) > 0,
    'SELECT 1',
    CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(20) NOT NULL DEFAULT ''USER'' COMMENT ''用户角色：USER-普通用户，ADMIN-管理员'' AFTER email')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 添加status字段（如果原来没有）
SET @columnname = 'status';
SET @preparedStatement = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
     WHERE TABLE_SCHEMA = @dbname 
     AND TABLE_NAME = @tablename 
     AND COLUMN_NAME = @columnname) > 0,
    'SELECT 1',
    CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' TINYINT NOT NULL DEFAULT 1 COMMENT ''账户状态：0-禁用，1-正常'' AFTER role')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 创建角色索引
SET @indexname = 'idx_role';
SET @preparedStatement = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_SCHEMA = @dbname 
     AND TABLE_NAME = @tablename 
     AND INDEX_NAME = @indexname) > 0,
    'SELECT 1',
    CONCAT('CREATE INDEX ', @indexname, ' ON ', @tablename, '(role)')
));
PREPARE createIndexIfNotExists FROM @preparedStatement;
EXECUTE createIndexIfNotExists;
DEALLOCATE PREPARE createIndexIfNotExists;

-- 创建状态索引
SET @indexname = 'idx_status';
SET @preparedStatement = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_SCHEMA = @dbname 
     AND TABLE_NAME = @tablename 
     AND INDEX_NAME = @indexname) > 0,
    'SELECT 1',
    CONCAT('CREATE INDEX ', @indexname, ' ON ', @tablename, '(status)')
));
PREPARE createIndexIfNotExists FROM @preparedStatement;
EXECUTE createIndexIfNotExists;
DEALLOCATE PREPARE createIndexIfNotExists;

-- 2. 创建审计日志表
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

-- 3. 初始化管理员账户
-- 密码为: admin123 (使用BCrypt加密)
-- BCrypt加密后的密码: $2a$10$N.zmdr9k7uOCQv37YK0Ke.Lqp3MGJfF2dX8aP9jHn6FqGQxZvBmKa
INSERT INTO user (id, user_id, username, password, nickname, phone, email, role, status, create_time, update_time)
SELECT NULL, 'admin001', 'admin', '$2a$10$N.zmdr9k7uOCQv37YK0Ke.Lqp3MGJfF2dX8aP9jHn6FqGQxZvBmKa', '系统管理员', '13800138000', 'admin@pawfu.com', 'ADMIN', 1, NOW(), NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user WHERE username = 'admin');

-- 4. 数据库迁移完成标记
SELECT '数据库迁移完成！' AS message;
SELECT COUNT(*) AS admin_count FROM user WHERE username = 'admin';
