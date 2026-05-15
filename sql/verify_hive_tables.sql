-- ============================================
-- Hive 表验证脚本
-- 用于检查表是否正确创建
-- ============================================

-- 1. 查看所有表
SHOW TABLES;

-- 2. 检查每个表的结构
DESCRIBE user_info;
DESCRIBE user_tags_detail;
DESCRIBE tag_statistics;
DESCRIBE tech_category_stats;
DESCRIBE crawl_job_log;

-- 3. 检查表中是否有数据
SELECT COUNT(*) FROM user_info;
SELECT COUNT(*) FROM user_tags_detail;
SELECT COUNT(*) FROM tag_statistics;
SELECT COUNT(*) FROM tech_category_stats;
SELECT COUNT(*) FROM crawl_job_log;

-- 4. 测试插入一条数据
INSERT INTO user_info VALUES (
    999999, 
    'test_user', 
    'test_account', 
    'http://test.com/avatar.jpg', 
    0, 
    '123456789', 
    'test@example.com', 
    0, 
    0, 
    'TEST001', 
    '["java", "spring"]', 
    'Test bio', 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP, 
    0
);

-- 5. 验证插入的数据
SELECT * FROM user_info WHERE id = 999999;

-- 6. 删除测试数据
DELETE FROM user_info WHERE id = 999999;
