-- ============================================
-- Hive SQL 错误调试脚本
-- 用于诊断 MapReduce 执行失败的原因
-- ============================================

-- 1. 检查表结构和数据
DESCRIBE user_info;

-- 2. 检查是否有数据
SELECT COUNT(*) as total_count FROM user_info;

-- 3. 检查 city 字段的数据质量
SELECT 
    COUNT(*) as total,
    COUNT(city) as with_city,
    COUNT(CASE WHEN city = '' THEN 1 END) as empty_city,
    COUNT(CASE WHEN city IS NULL THEN 1 END) as null_city
FROM user_info;

-- 4. 查看示例数据（检查格式）
SELECT id, username, city, is_delete FROM user_info LIMIT 10;

-- 5. 检查 is_delete 字段的值分布
SELECT is_delete, COUNT(*) as count 
FROM user_info 
GROUP BY is_delete;

-- 6. 尝试简化查询（不加聚合函数）
SELECT city 
FROM user_info 
WHERE city IS NOT NULL 
    AND city != ''
    AND is_delete = 0
LIMIT 10;

-- 7. 尝试小范围测试（只查前100条）
SELECT 
    city,
    COUNT(*) as user_count
FROM user_info
WHERE city IS NOT NULL 
    AND city != ''
    AND is_delete = 0
GROUP BY city
ORDER BY user_count DESC
LIMIT 10;

-- 8. 检查是否有特殊字符或异常数据
SELECT DISTINCT city 
FROM user_info 
WHERE city IS NOT NULL 
    AND city != ''
LIMIT 20;

-- 9. 检查表的存储格式和位置
DESCRIBE FORMATTED user_info;

-- 10. 检查 HDFS 上的数据文件
-- 在 shell 中执行：
-- hdfs dfs -ls /user/hive/warehouse/user_info
-- hdfs dfs -head /user/hive/warehouse/user_info/*.txt | head -20
