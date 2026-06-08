-- 宠物领养服务数据库初始化脚本
-- 字符集：utf8mb4，支持中文和表情符号

-- 创建数据库
CREATE DATABASE IF NOT EXISTS pet_adoption DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pet_adoption;

-- 1. 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` varchar(32) NOT NULL COMMENT '用户ID（UUID）',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 宠物种类字典表
DROP TABLE IF EXISTS `pet_category_dict`;
CREATE TABLE `pet_category_dict` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_id` int(11) NOT NULL COMMENT '种类ID',
  `category_name` varchar(50) NOT NULL COMMENT '种类名称',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序序号',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物种类字典表';

-- 初始化宠物种类数据
INSERT INTO `pet_category_dict` (`category_id`, `category_name`, `sort_order`) VALUES
(1, '犬类', 1),
(2, '猫类', 2),
(3, '鸟类', 3),
(4, '兔类', 4),
(5, '仓鼠', 5),
(6, '其他', 6);

-- 3. 宠物状态字典表
DROP TABLE IF EXISTS `pet_status_dict`;
CREATE TABLE `pet_status_dict` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `status_id` int(11) NOT NULL COMMENT '状态ID',
  `status_name` varchar(50) NOT NULL COMMENT '状态名称',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序序号',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_status_id` (`status_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='宠物状态字典表';

-- 初始化宠物状态数据
INSERT INTO `pet_status_dict` (`status_id`, `status_name`, `sort_order`) VALUES
(1, '健康', 1),
(2, '已绝育', 2),
(3, '已免疫', 3),
(4, '有特殊护理需求', 4),
(5, '其他', 5);

-- 4. 领养信息表
DROP TABLE IF EXISTS `pet_adoption`;
CREATE TABLE `pet_adoption` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pet_id` varchar(32) NOT NULL COMMENT '领养信息ID（UUID）',
  `publisher_id` varchar(32) NOT NULL COMMENT '发布者ID',
  `pet_name` varchar(100) NOT NULL COMMENT '宠物名称',
  `category_id` int(11) NOT NULL COMMENT '宠物种类ID',
  `pet_status` int(11) NOT NULL COMMENT '宠物状态ID',
  `age` int(11) DEFAULT NULL COMMENT '年龄（月）',
  `gender` tinyint(1) DEFAULT NULL COMMENT '性别：1-公，2-母，0-未知',
  `color` varchar(50) DEFAULT NULL COMMENT '毛色',
  `weight` decimal(5,2) DEFAULT NULL COMMENT '体重（kg）',
  `description` text COMMENT '详细描述',
  `photo_urls` varchar(1000) DEFAULT NULL COMMENT '照片URL列表（JSON数组）',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区县',
  `address` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `contact_wechat` varchar(50) DEFAULT NULL COMMENT '微信号',
  `adoption_status` tinyint(1) DEFAULT 1 COMMENT '领养状态：1-待领养，2-已领养，3-已下架',
  `view_count` int(11) DEFAULT 0 COMMENT '浏览次数',
  `like_count` int(11) DEFAULT 0 COMMENT '收藏次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pet_id` (`pet_id`),
  KEY `idx_publisher_id` (`publisher_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_adoption_status` (`adoption_status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_status_category_time` (`adoption_status`, `category_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='领养信息表';

-- 5. 联系记录表
DROP TABLE IF EXISTS `contact_record`;
CREATE TABLE `contact_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `contact_id` varchar(32) NOT NULL COMMENT '联系记录ID（UUID）',
  `pet_id` varchar(32) NOT NULL COMMENT '领养信息ID',
  `adopter_id` varchar(32) NOT NULL COMMENT '领养者ID（发起联系人）',
  `publisher_id` varchar(32) NOT NULL COMMENT '发布者ID',
  `message` text COMMENT '联系留言',
  `contact_type` tinyint(1) DEFAULT 1 COMMENT '联系方式：1-站内联系，2-电话，3-微信',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：1-已发送，2-已读，3-已回复，4-已拒绝',
  `is_read` tinyint(1) DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_contact_id` (`contact_id`),
  KEY `idx_pet_id` (`pet_id`),
  KEY `idx_adopter_id` (`adopter_id`),
  KEY `idx_publisher_id` (`publisher_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='联系记录表';

-- 6. 操作日志表
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(100) NOT NULL COMMENT '操作类型',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` text COMMENT '请求参数',
  `result` text COMMENT '操作结果',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：1-成功，0-失败',
  `error_msg` text COMMENT '错误信息',
  `execute_time` bigint(20) DEFAULT NULL COMMENT '执行时长（毫秒）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_operation` (`operation`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 数据库配置优化
SET GLOBAL max_connections = 1000;
SET GLOBAL innodb_buffer_pool_size = 1073741824;
