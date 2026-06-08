-- 宠物领养服务数据库初始化脚本（H2版本）

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` VARCHAR(32) NOT NULL,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(20),
  `email` VARCHAR(100),
  `nickname` VARCHAR(50),
  `avatar` VARCHAR(255),
  `status` TINYINT DEFAULT 1,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_user_id ON `user`(`user_id`);
CREATE UNIQUE INDEX IF NOT EXISTS uk_username ON `user`(`username`);
CREATE INDEX IF NOT EXISTS idx_create_time ON `user`(`create_time`);

-- 2. 宠物种类字典表
CREATE TABLE IF NOT EXISTS `pet_category_dict` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `category_id` INT NOT NULL,
  `category_name` VARCHAR(50) NOT NULL,
  `sort_order` INT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_category_id ON `pet_category_dict`(`category_id`);

-- 初始化宠物种类数据
INSERT INTO `pet_category_dict` (`category_id`, `category_name`, `sort_order`) VALUES
(1, '犬类', 1),
(2, '猫类', 2),
(3, '鸟类', 3),
(4, '兔类', 4),
(5, '仓鼠', 5),
(6, '其他', 6);

-- 3. 宠物状态字典表
CREATE TABLE IF NOT EXISTS `pet_status_dict` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `status_id` INT NOT NULL,
  `status_name` VARCHAR(50) NOT NULL,
  `sort_order` INT DEFAULT 0,
  `status` TINYINT DEFAULT 1,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_status_id ON `pet_status_dict`(`status_id`);

-- 初始化宠物状态数据
INSERT INTO `pet_status_dict` (`status_id`, `status_name`, `sort_order`) VALUES
(1, '健康', 1),
(2, '已绝育', 2),
(3, '已免疫', 3),
(4, '有特殊护理需求', 4),
(5, '其他', 5);

-- 4. 领养信息表
CREATE TABLE IF NOT EXISTS `pet_adoption` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `pet_id` VARCHAR(32) NOT NULL,
  `publisher_id` VARCHAR(32) NOT NULL,
  `pet_name` VARCHAR(100) NOT NULL,
  `category_id` INT NOT NULL,
  `pet_status` INT NOT NULL,
  `age` INT,
  `gender` TINYINT,
  `color` VARCHAR(50),
  `weight` DECIMAL(5,2),
  `description` TEXT,
  `photo_urls` VARCHAR(1000),
  `province` VARCHAR(50),
  `city` VARCHAR(50),
  `district` VARCHAR(50),
  `address` VARCHAR(200),
  `contact_name` VARCHAR(50),
  `contact_phone` VARCHAR(20),
  `contact_wechat` VARCHAR(50),
  `adoption_status` TINYINT DEFAULT 1,
  `view_count` INT DEFAULT 0,
  `like_count` INT DEFAULT 0,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_pet_id ON `pet_adoption`(`pet_id`);
CREATE INDEX IF NOT EXISTS idx_publisher_id ON `pet_adoption`(`publisher_id`);
CREATE INDEX IF NOT EXISTS idx_category_id ON `pet_adoption`(`category_id`);
CREATE INDEX IF NOT EXISTS idx_adoption_status ON `pet_adoption`(`adoption_status`);
CREATE INDEX IF NOT EXISTS idx_create_time ON `pet_adoption`(`create_time`);

-- 5. 联系记录表
CREATE TABLE IF NOT EXISTS `contact_record` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `contact_id` VARCHAR(32) NOT NULL,
  `pet_id` VARCHAR(32) NOT NULL,
  `adopter_id` VARCHAR(32) NOT NULL,
  `publisher_id` VARCHAR(32) NOT NULL,
  `message` TEXT,
  `contact_type` TINYINT DEFAULT 1,
  `status` TINYINT DEFAULT 1,
  `is_read` TINYINT DEFAULT 0,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_contact_id ON `contact_record`(`contact_id`);
CREATE INDEX IF NOT EXISTS idx_pet_id ON `contact_record`(`pet_id`);
CREATE INDEX IF NOT EXISTS idx_adopter_id ON `contact_record`(`adopter_id`);

-- 6. 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` VARCHAR(32),
  `username` VARCHAR(50),
  `operation` VARCHAR(100) NOT NULL,
  `method` VARCHAR(200),
  `params` TEXT,
  `result` TEXT,
  `ip` VARCHAR(50),
  `user_agent` VARCHAR(500),
  `status` TINYINT DEFAULT 1,
  `error_msg` TEXT,
  `execute_time` BIGINT,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_id ON `operation_log`(`user_id`);
CREATE INDEX IF NOT EXISTS idx_create_time ON `operation_log`(`create_time`);
