-- ============================================
-- 数据可视化统计 - 分步执行版本
-- 避免使用复杂的窗口函数，降低 MapReduce 失败概率
-- ============================================

-- ============================================
-- 视图4: 用户城市分布数据
-- ============================================

-- Step 1: 计算每个城市的用户数
DROP TABLE IF EXISTS temp_city_count;
CREATE TABLE temp_city_count AS
SELECT 
    city,
    COUNT(*) as user_count
FROM user_info
WHERE city IS NOT NULL 
    AND city != ''
    AND is_delete = 0
GROUP BY city;

-- Step 2: 计算总用户数
DROP TABLE IF EXISTS temp_city_total;
CREATE TABLE temp_city_total AS
SELECT SUM(user_count) as total_count
FROM temp_city_count;

-- Step 3: 计算百分比并插入最终表
INSERT OVERWRITE TABLE user_city_distribution
SELECT 
    t.city,
    t.user_count,
    ROUND(t.user_count * 100.0 / tot.total_count, 2) as percentage,
    CURRENT_DATE() as stat_date
FROM temp_city_count t
CROSS JOIN temp_city_total tot
ORDER BY t.user_count DESC;

-- Step 4: 清理临时表
DROP TABLE IF EXISTS temp_city_count;
DROP TABLE IF EXISTS temp_city_total;

-- 验证
SELECT '✅ 城市分布统计完成' as status;
SELECT COUNT(*) as city_count FROM user_city_distribution;


-- ============================================
-- 视图5: 用户活跃度趋势数据
-- ============================================

-- Step 1: 按天统计新增用户
DROP TABLE IF EXISTS temp_daily_users;
CREATE TABLE temp_daily_users AS
SELECT 
    TO_DATE(create_time) as stat_date,
    COUNT(*) as new_users
FROM user_info
WHERE is_delete = 0
GROUP BY TO_DATE(create_time);

-- Step 2: 计算累计用户总数（使用自连接）
INSERT OVERWRITE TABLE user_activity_trend
SELECT 
    d.stat_date,
    d.new_users,
    d.new_users as active_users,  -- 简化处理
    (
        SELECT SUM(d2.new_users) 
        FROM temp_daily_users d2 
        WHERE d2.stat_date <= d.stat_date
    ) as total_users,
    0.00 as growth_rate
FROM temp_daily_users d
ORDER BY d.stat_date;

-- Step 3: 清理临时表
DROP TABLE IF EXISTS temp_daily_users;

-- 验证
SELECT '✅ 活跃度趋势统计完成' as status;
SELECT COUNT(*) as date_count FROM user_activity_trend;


-- ============================================
-- 视图6: 用户等级分布数据
-- ============================================

-- Step 1: 计算总用户数
DROP TABLE IF EXISTS temp_level_total;
CREATE TABLE temp_level_total AS
SELECT COUNT(*) as total_count
FROM user_info
WHERE is_delete = 0;

-- Step 2: 计算各等级分布
INSERT OVERWRITE TABLE user_level_distribution
SELECT 
    level,
    COUNT(*) as user_count,
    ROUND(COUNT(*) * 100.0 / tot.total_count, 2) as percentage,
    ROUND(AVG(experience), 2) as avg_experience,
    MAX(experience) as max_experience,
    MIN(experience) as min_experience,
    CURRENT_DATE() as stat_date
FROM user_info
CROSS JOIN temp_level_total tot
WHERE is_delete = 0
GROUP BY level, tot.total_count
ORDER BY level;

-- Step 3: 清理临时表
DROP TABLE IF EXISTS temp_level_total;

-- 验证
SELECT '✅ 等级分布统计完成' as status;
SELECT COUNT(*) as level_count FROM user_level_distribution;


-- ============================================
-- 增强版标签统计（可选）
-- ============================================

INSERT OVERWRITE TABLE tag_statistics_enhanced
SELECT 
    tag,
    COUNT(*) as tag_count,
    COUNT(DISTINCT user_id) as user_count,
    CASE 
        WHEN LOWER(tag) RLIKE '.*(java|spring|springboot|mybatis).*' THEN 'Java生态'
        WHEN LOWER(tag) RLIKE '.*(python|django|flask).*' THEN 'Python生态'
        WHEN LOWER(tag) RLIKE '.*(javascript|typescript|nodejs).*' THEN 'JavaScript生态'
        WHEN LOWER(tag) RLIKE '.*(vue|react|angular).*' THEN '前端框架'
        WHEN LOWER(tag) RLIKE '.*(mysql|postgresql|mongodb|redis).*' THEN '数据库'
        ELSE '其他技术'
    END as category,
    ROUND(COUNT(*) * 1.0 / COUNT(DISTINCT user_id), 2) as avg_tags_per_user,
    ROUND((COUNT(*) * 0.6 + COUNT(DISTINCT user_id) * 0.4), 2) as popularity_score,
    CURRENT_DATE() as stat_date
FROM user_tags_detail
GROUP BY tag
ORDER BY user_count DESC;

SELECT '✅ 标签统计完成' as status;


-- ============================================
-- 最终验证
-- ============================================

SELECT '========== 数据统计完成 ==========' as message;

SELECT 
    '城市分布' as table_name, 
    COUNT(*) as record_count 
FROM user_city_distribution

UNION ALL

SELECT 
    '活跃度趋势', 
    COUNT(*) 
FROM user_activity_trend

UNION ALL

SELECT 
    '等级分布', 
    COUNT(*) 
FROM user_level_distribution

UNION ALL

SELECT 
    '标签统计', 
    COUNT(*) 
FROM tag_statistics_enhanced;

SELECT '🎉 所有统计数据已生成！可以启动后端服务测试了。' as final_message;
