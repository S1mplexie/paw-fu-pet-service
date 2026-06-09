-- 浏览记录表 - 用于防止重复刷浏览量
CREATE TABLE IF NOT EXISTS `view_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_identifier` varchar(100) NOT NULL COMMENT '用户标识（userId或游客标识）',
  `pet_id` varchar(32) NOT NULL COMMENT '宠物ID',
  `view_time` datetime NOT NULL COMMENT '浏览时间',
  `user_type` varchar(20) NOT NULL COMMENT '用户类型：user-登录用户，guest-游客',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_pet` (`user_identifier`, `pet_id`),
  KEY `idx_pet_id` (`pet_id`),
  KEY `idx_view_time` (`view_time`),
  KEY `idx_user_type` (`user_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浏览记录表';
