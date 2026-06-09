-- =====================================================
-- 宠物图片集功能 - 数据库表结构扩展
-- 版本: V1.0.6
-- 描述: 新增封面照片字段，改造图片集字段
-- =====================================================

USE pet_adoption;

-- 1. 新增封面照片字段
ALTER TABLE pet_adoption 
ADD COLUMN cover_photo_url VARCHAR(500) COMMENT '封面照片URL（主页展示）' AFTER description;

-- 2. 修改photo_urls字段类型和注释（改为图片集）
ALTER TABLE pet_adoption 
MODIFY COLUMN photo_urls TEXT COMMENT '图片集URL列表（JSON数组格式，最多9张）';

-- 3. 创建索引优化查询性能
CREATE INDEX idx_cover_photo ON pet_adoption(cover_photo_url(100));

-- 4. 数据迁移：将现有的photo_urls的第一张图片作为封面
UPDATE pet_adoption 
SET cover_photo_url = 
    CASE 
        WHEN photo_urls LIKE '[%' THEN 
            JSON_UNQUOTE(JSON_EXTRACT(photo_urls, '$[0]'))
        WHEN photo_urls IS NOT NULL AND photo_urls != '' THEN 
            SUBSTRING_INDEX(photo_urls, ',', 1)
        ELSE NULL
    END
WHERE photo_urls IS NOT NULL AND cover_photo_url IS NULL;

-- 5. 验证迁移结果
SELECT '表结构扩展完成' AS message;
SELECT COUNT(*) AS total_records FROM pet_adoption;
SELECT COUNT(*) AS with_cover FROM pet_adoption WHERE cover_photo_url IS NOT NULL;
SELECT COUNT(*) AS with_gallery FROM pet_adoption WHERE photo_urls IS NOT NULL;
