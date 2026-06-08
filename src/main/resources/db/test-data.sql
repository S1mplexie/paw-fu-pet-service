-- 插入测试数据

-- 测试用户由DataInitializer自动初始化，密码为Test1234

-- 插入测试领养信息
INSERT INTO `pet_adoption` (`pet_id`, `publisher_id`, `pet_name`, `category_id`, `pet_status`, `age`, `gender`, `color`, `weight`, `description`, `photo_urls`, `province`, `city`, `district`, `address`, `contact_name`, `contact_phone`, `contact_wechat`, `adoption_status`, `view_count`, `like_count`, `deleted`) VALUES
('pet001', 'user001', '小黄', 1, 2, 24, 1, '金黄色', 15.5, '性格温顺，已绝育已免疫，非常乖巧听话，适合家庭饲养。定期洗澡，毛发柔顺。', 'https://images.unsplash.com/photo-1587300003381-16d9e0e4c6e8?w=400', '北京市', '北京市', '朝阳区', '朝阳公园附近', '张三', '13800138001', 'zhangsan_wx', 1, 156, 23, 0),
('pet002', 'user001', '小花', 2, 3, 18, 2, '橘白色', 4.2, '可爱的橘猫，已免疫，活泼好动，喜欢玩耍。很亲人，会使用猫砂。', 'https://images.unsplash.com/photo-1514837595336?w=400', '北京市', '北京市', '海淀区', '中关村大街', '张三', '13800138001', 'zhangsan_wx', 1, 89, 15, 0),
('pet003', 'user002', '豆豆', 1, 1, 12, 1, '黑白相间', 8.0, '边境牧羊犬，聪明伶俐，会接飞盘。定期驱虫，身体健康。', 'https://images.unsplash.com/photo-1552053830-52686f4bf6b2?w=400', '上海市', '上海市', '浦东新区', '陆家嘴', '李四', '13800138002', 'lisi_wx', 1, 234, 45, 0),
('pet004', 'user002', '咪咪', 2, 2, 36, 2, '灰白色', 3.5, '英短蓝猫，已绝育，性格安静，适合公寓饲养。', 'https://images.unsplash.com/photo-1573865568967-7b7c3a4d8c6e?w=400', '上海市', '上海市', '徐汇区', '徐家汇', '李四', '13800138002', 'lisi_wx', 1, 178, 32, 0),
('pet005', 'user003', '小白', 3, 1, 6, 0, '白色', 0.3, '虎皮鹦鹉，会说话，声音清脆悦耳。笼子一起送。', 'https://images.unsplash.com/photo-1552720306-5e4e3e3e3e3e?w=400', '广州市', '广州市', '天河区', '天河城', '王五', '13800138003', NULL, 1, 67, 8, 0),
('pet006', 'user003', '毛球', 4, 1, 8, 1, '白灰色', 1.2, '荷兰垂耳兔，很可爱，需要定期梳毛。性格温和。', 'https://images.unsplash.com/photo-1585110346179-3b8e3e3e3e3e?w=400', '广州市', '广州市', '越秀区', '北京路', '王五', '13800138003', NULL, 1, 112, 19, 0),
('pet007', 'user001', '旺财', 1, 3, 48, 1, '棕色', 20.0, '拉布拉多犬，已绝育已免疫，训练有素，会握手、坐下等指令。', 'https://images.unsplash.com/photo-1561038655-7b7c3a4d8c6e?w=400', '深圳市', '深圳市', '南山区', '科技园', '张三', '13800138001', 'zhangsan_wx', 1, 345, 67, 0),
('pet008', 'user002', '奶茶', 5, 1, 3, 2, '金黄色', 0.05, '小仓鼠，活泼可爱，容易饲养，适合新手。', 'https://images.unsplash.com/photo-1425082661110-2b8e3e3e3e3e?w=400', '深圳市', '深圳市', '福田区', '华强北', '李四', '13800138002', 'lisi_wx', 1, 45, 6, 0);

-- 插入测试联系记录
INSERT INTO `contact_record` (`contact_id`, `pet_id`, `adopter_id`, `publisher_id`, `message`, `contact_type`, `status`, `is_read`, `deleted`) VALUES
('contact001', 'pet001', 'user002', 'user001', '您好，我对小黄很感兴趣，请问可以领养吗？', 1, 1, 1, 0),
('contact002', 'pet003', 'user001', 'user002', '豆豆很可爱，想了解更多信息', 1, 1, 0, 0);
