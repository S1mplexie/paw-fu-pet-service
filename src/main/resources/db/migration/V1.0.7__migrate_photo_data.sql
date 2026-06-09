-- =====================================================
-- 宠物图片集功能 - 数据迁移脚本
-- 版本: V1.0.7
-- 描述: 验证数据迁移结果，创建辅助视图
-- =====================================================

USE pet_adoption;

-- 1. 查看迁移后的数据状态
SELECT '迁移后数据状态：' AS message;
SELECT 
    COUNT(*) AS total,
    SUM(CASE WHEN cover_photo_url IS NOT NULL THEN 1 ELSE 0 END) AS has_cover,
    SUM(CASE WHEN photo_urls IS NOT NULL THEN 1 ELSE 0 END) AS has_gallery
FROM pet_adoption;

-- 2. 显示部分数据示例
SELECT '数据示例：' AS message;
SELECT 
    pet_id,
    pet_name,
    cover_photo_url,
    photo_urls,
    CASE 
        WHEN photo_urls LIKE '[%' THEN JSON_LENGTH(photo_urls)
        WHEN photo_urls IS NOT NULL AND photo_urls != '' THEN 
            LENGTH(photo_urls) - LENGTH(REPLACE(photo_urls, ',', '')) + 1
        ELSE 0
    END AS gallery_count
FROM pet_adoption
LIMIT 5;

-- 3. 数据格式说明
SELECT '数据格式说明：' AS message;
SELECT 'cover_photo_url: 单张封面照片URL（用于主页列表展示）' AS field_info;
SELECT 'photo_urls: 支持两种格式 - 旧格式(逗号分隔): url1,url2,url3 | 新格式(JSON数组): ["url1","url2","url3"]' AS format_info;

-- 4. 迁移完成提示
SELECT '迁移完成！系统支持向后兼容格式' AS message;
