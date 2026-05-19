-- ============================================
-- 数据可视化 - Hive 数据分析 SQL 脚本
-- 用于支持后三个可视化视图（Hive为主，MySQL兜底）
-- ============================================

-- 使用前请切换到你的数据库
USE default;

-- ============================================
-- 视图4: 用户城市分布数据（地图可视化）
-- 数据来源：user_info 表
-- ============================================

-- 4.1 创建城市分布统计表
DROP TABLE IF EXISTS user_city_distribution;
CREATE TABLE IF NOT EXISTS user_city_distribution (
    city STRING COMMENT '城市名称',
    user_count BIGINT COMMENT '用户数量',
    percentage DOUBLE COMMENT '占比百分比',
    stat_date DATE COMMENT '统计日期'
)
COMMENT '用户城市分布统计表'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

-- 4.2 插入城市分布数据
INSERT OVERWRITE TABLE user_city_distribution
SELECT 
    city,
    COUNT(*) as user_count,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER(), 2) as percentage,
    CURRENT_DATE() as stat_date
FROM user_info
WHERE city IS NOT NULL 
    AND city != ''
    AND is_delete = 0
GROUP BY city
ORDER BY user_count DESC;

-- 4.3 创建城市分布视图（供查询使用）
DROP VIEW IF EXISTS view_user_city_distribution;
CREATE VIEW IF NOT EXISTS view_user_city_distribution AS
SELECT 
    city,
    user_count,
    percentage,
    stat_date
FROM user_city_distribution
ORDER BY user_count DESC;


-- ============================================
-- 视图5: 用户活跃度趋势数据（折线图）
-- 数据来源：user_info 表（按创建时间统计）
-- ============================================

-- 5.1 创建用户活跃度统计表
DROP TABLE IF EXISTS user_activity_trend;
CREATE TABLE IF NOT EXISTS user_activity_trend (
    stat_date DATE COMMENT '统计日期',
    new_users BIGINT COMMENT '新增用户数',
    active_users BIGINT COMMENT '活跃用户数（登录）',
    total_users BIGINT COMMENT '累计用户总数',
    growth_rate DOUBLE COMMENT '增长率百分比'
)
COMMENT '用户活跃度趋势统计表'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

-- 5.2 插入活跃度趋势数据（按天统计）
-- 使用窗口函数计算累计用户总数和增长率
INSERT OVERWRITE TABLE user_activity_trend
SELECT 
    TO_DATE(create_time) as stat_date,
    COUNT(*) as new_users,
    COUNT(*) as active_users,  -- 简化处理，实际应根据登录日志
    SUM(COUNT(*)) OVER(ORDER BY TO_DATE(create_time)) as total_users,
    ROUND(
        CASE 
            WHEN LAG(COUNT(*)) OVER(ORDER BY TO_DATE(create_time)) > 0 
            THEN (COUNT(*) - LAG(COUNT(*)) OVER(ORDER BY TO_DATE(create_time))) * 100.0 / LAG(COUNT(*)) OVER(ORDER BY TO_DATE(create_time))
            ELSE 0 
        END, 
    2) as growth_rate
FROM user_info
WHERE is_delete = 0
GROUP BY TO_DATE(create_time)
ORDER BY stat_date;

-- 5.3 创建活跃度趋势视图
DROP VIEW IF EXISTS view_user_activity_trend;
CREATE VIEW IF NOT EXISTS view_user_activity_trend AS
SELECT 
    stat_date,
    new_users,
    active_users,
    total_users,
    growth_rate
FROM user_activity_trend
ORDER BY stat_date;


-- ============================================
-- 视图6: 用户等级分布数据（雷达图）
-- 数据来源：user_info 表
-- ============================================

-- 6.1 创建用户等级分布统计表
DROP TABLE IF EXISTS user_level_distribution;
CREATE TABLE IF NOT EXISTS user_level_distribution (
    level INT COMMENT '等级(LV1-LV8)',
    user_count BIGINT COMMENT '用户数量',
    percentage DOUBLE COMMENT '占比百分比',
    avg_experience DOUBLE COMMENT '平均经验值',
    max_experience BIGINT COMMENT '最高经验值',
    min_experience BIGINT COMMENT '最低经验值',
    stat_date DATE COMMENT '统计日期'
)
COMMENT '用户等级分布统计表'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

-- 6.2 插入等级分布数据
INSERT OVERWRITE TABLE user_level_distribution
SELECT 
    level,
    COUNT(*) as user_count,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER(), 2) as percentage,
    ROUND(AVG(experience), 2) as avg_experience,
    MAX(experience) as max_experience,
    MIN(experience) as min_experience,
    CURRENT_DATE() as stat_date
FROM user_info
WHERE is_delete = 0
GROUP BY level
ORDER BY level;

-- 6.3 创建等级分布视图
DROP VIEW IF EXISTS view_user_level_distribution;
CREATE VIEW IF NOT EXISTS view_user_level_distribution AS
SELECT 
    level,
    user_count,
    percentage,
    avg_experience,
    max_experience,
    min_experience,
    stat_date
FROM user_level_distribution
ORDER BY level;


-- ============================================
-- 补充：增强版标签统计（用于前三个视图的Hive备份）
-- ============================================

-- 7.1 更新标签统计表（更详细的统计）
DROP TABLE IF EXISTS tag_statistics_enhanced;
CREATE TABLE IF NOT EXISTS tag_statistics_enhanced (
    tag STRING COMMENT '标签名称',
    tag_count BIGINT COMMENT '总使用次数',
    user_count BIGINT COMMENT '使用用户数',
    category STRING COMMENT '技术分类',
    avg_tags_per_user DOUBLE COMMENT '每用户平均标签数',
    popularity_score DOUBLE COMMENT '热度评分',
    stat_date DATE COMMENT '统计日期'
)
COMMENT '增强版标签统计表'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

-- 7.2 插入增强版标签统计数据
INSERT OVERWRITE TABLE tag_statistics_enhanced
SELECT 
    tag,
    COUNT(*) as tag_count,
    COUNT(DISTINCT user_id) as user_count,
    CASE 
        WHEN LOWER(tag) RLIKE '.*(java|spring|springboot|spring-boot|mybatis|hibernate|jpa|maven|gradle).*' THEN 'Java生态'
        WHEN LOWER(tag) RLIKE '.*(python|django|flask|fastapi|pandas|numpy|scipy).*' THEN 'Python生态'
        WHEN LOWER(tag) RLIKE '.*(javascript|typescript|nodejs|node.js|express|nestjs).*' THEN 'JavaScript生态'
        WHEN LOWER(tag) RLIKE '.*(vue|react|angular|svelte|nextjs|nuxt).*' THEN '前端框架'
        WHEN LOWER(tag) RLIKE '.*(mysql|postgresql|mongodb|redis|elasticsearch).*' THEN '数据库'
        WHEN LOWER(tag) RLIKE '.*(docker|kubernetes|jenkins|gitlab|ci/cd).*' THEN 'DevOps'
        WHEN LOWER(tag) RLIKE '.*(aws|azure|gcp|aliyun|cloud).*' THEN '云平台'
        WHEN LOWER(tag) RLIKE '.*(machine-learning|tensorflow|pytorch|ai|deep-learning).*' THEN '人工智能'
        WHEN LOWER(tag) RLIKE '.*(hadoop|spark|hive|flink|bigdata).*' THEN '大数据'
        ELSE '其他技术'
    END as category,
    ROUND(COUNT(*) * 1.0 / COUNT(DISTINCT user_id), 2) as avg_tags_per_user,
    ROUND((COUNT(*) * 0.6 + COUNT(DISTINCT user_id) * 0.4), 2) as popularity_score,
    CURRENT_DATE() as stat_date
FROM user_tags_detail
GROUP BY tag
ORDER BY user_count DESC;

-- 7.3 创建增强版标签统计视图
DROP VIEW IF EXISTS view_tag_statistics_enhanced;
CREATE VIEW IF NOT EXISTS view_tag_statistics_enhanced AS
SELECT 
    tag,
    tag_count,
    user_count,
    category,
    avg_tags_per_user,
    popularity_score,
    stat_date
FROM tag_statistics_enhanced
ORDER BY user_count DESC;


-- ============================================
-- 综合统计视图（用于数据概览）
-- ============================================

-- 8.1 创建综合统计视图
DROP VIEW IF EXISTS view_comprehensive_stats;
CREATE VIEW IF NOT EXISTS view_comprehensive_stats AS
SELECT 
    'total_users' as metric_name,
    CAST(COUNT(*) AS STRING) as metric_value,
    '用户总数' as metric_desc
FROM user_info
WHERE is_delete = 0

UNION ALL

SELECT 
    'total_tags' as metric_name,
    CAST(COUNT(DISTINCT tag) AS STRING) as metric_value,
    '标签总数' as metric_desc
FROM user_tags_detail

UNION ALL

SELECT 
    'total_cities' as metric_name,
    CAST(COUNT(DISTINCT city) AS STRING) as metric_value,
    '城市数量' as metric_desc
FROM user_info
WHERE city IS NOT NULL AND city != '' AND is_delete = 0

UNION ALL

SELECT 
    'avg_level' as metric_name,
    CAST(ROUND(AVG(level), 2) AS STRING) as metric_value,
    '平均等级' as metric_desc
FROM user_info
WHERE is_delete = 0

UNION ALL

SELECT 
    'max_level' as metric_name,
    CAST(MAX(level) AS STRING) as metric_value,
    '最高等级' as metric_desc
FROM user_info
WHERE is_delete = 0;


-- ============================================
-- 验证查询示例
-- ============================================

-- 查看城市分布前10名
SELECT * FROM view_user_city_distribution LIMIT 10;

-- 查看最近30天的活跃度趋势
SELECT * FROM view_user_activity_trend 
WHERE stat_date >= DATE_SUB(CURRENT_DATE(), 30)
ORDER BY stat_date;

-- 查看等级分布
SELECT * FROM view_user_level_distribution;

-- 查看热门标签Top 20
SELECT * FROM view_top_20_tags;

-- 查看综合统计
SELECT * FROM view_comprehensive_stats;


-- ============================================
-- 说明
-- ============================================
-- 1. 执行顺序：先执行建表语句，再执行INSERT语句，最后创建视图
-- 2. 数据来源：所有数据从 user_info 和 user_tags_detail 表聚合
-- 3. 更新频率：建议每天定时执行INSERT OVERWRITE更新数据
-- 4. MySQL兜底：如果Hive查询失败，后端会自动切换到MySQL的tag_statistics等表
-- 5. 性能优化：对于大数据量，可以考虑分区表或增量更新
