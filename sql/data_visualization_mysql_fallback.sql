-- ============================================
-- 数据可视化 - MySQL 兜底表结构
-- 当 Hive 不可用时，使用这些表提供数据
-- ============================================

USE your_database_name;

-- ============================================
-- 表4: 用户城市分布表（MySQL兜底）
-- ============================================
CREATE TABLE IF NOT EXISTS user_city_distribution (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    city VARCHAR(64) NOT NULL COMMENT '城市名称',
    user_count INT DEFAULT 0 COMMENT '用户数量',
    percentage DECIMAL(5, 2) DEFAULT 0 COMMENT '占比百分比',
    stat_date DATE NOT NULL COMMENT '统计日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_city_date (city, stat_date),
    INDEX idx_stat_date (stat_date),
    INDEX idx_user_count (user_count)
) COMMENT '用户城市分布统计表(MySQL兜底)';


-- ============================================
-- 表5: 用户活跃度趋势表（MySQL兜底）
-- ============================================
CREATE TABLE IF NOT EXISTS user_activity_trend (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    stat_date DATE NOT NULL COMMENT '统计日期',
    new_users INT DEFAULT 0 COMMENT '新增用户数',
    active_users INT DEFAULT 0 COMMENT '活跃用户数',
    total_users INT DEFAULT 0 COMMENT '累计用户总数',
    growth_rate DECIMAL(5, 2) DEFAULT 0 COMMENT '增长率百分比',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_stat_date (stat_date),
    INDEX idx_stat_date (stat_date)
) COMMENT '用户活跃度趋势统计表(MySQL兜底)';


-- ============================================
-- 表6: 用户等级分布表（MySQL兜底）
-- ============================================
CREATE TABLE IF NOT EXISTS user_level_distribution (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    level INT NOT NULL COMMENT '等级(LV1-LV8)',
    user_count INT DEFAULT 0 COMMENT '用户数量',
    percentage DECIMAL(5, 2) DEFAULT 0 COMMENT '占比百分比',
    avg_experience DECIMAL(10, 2) DEFAULT 0 COMMENT '平均经验值',
    max_experience INT DEFAULT 0 COMMENT '最高经验值',
    min_experience INT DEFAULT 0 COMMENT '最低经验值',
    stat_date DATE NOT NULL COMMENT '统计日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_level_date (level, stat_date),
    INDEX idx_stat_date (stat_date),
    INDEX idx_level (level)
) COMMENT '用户等级分布统计表(MySQL兜底)';


-- ============================================
-- 初始化数据示例（从现有表聚合）
-- ============================================

-- 初始化城市分布数据
INSERT INTO user_city_distribution (city, user_count, percentage, stat_date)
SELECT 
    city,
    COUNT(*) as user_count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM user WHERE isDelete = 0 AND city IS NOT NULL AND city != ''), 2) as percentage,
    CURDATE() as stat_date
FROM user
WHERE isDelete = 0 
    AND city IS NOT NULL 
    AND city != ''
GROUP BY city
ORDER BY user_count DESC;

-- 初始化活跃度趋势数据（最近30天）
-- 第一步：插入基础数据
INSERT INTO user_activity_trend (stat_date, new_users, active_users, total_users, growth_rate)
SELECT 
    DATE(createTime) as stat_date,
    COUNT(*) as new_users,
    COUNT(*) as active_users,
    0 as total_users,
    0.00 as growth_rate
FROM user
WHERE isDelete = 0
    AND createTime >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY DATE(createTime)
ORDER BY stat_date;

-- 第二步：计算累计用户总数（使用临时表）
DROP TEMPORARY TABLE IF EXISTS temp_cumulative;
CREATE TEMPORARY TABLE temp_cumulative AS
SELECT 
    stat_date,
    new_users,
    @total := @total + new_users as cumulative_total
FROM user_activity_trend,
(SELECT @total := 0) vars
ORDER BY stat_date;

-- 第三步：更新主表
UPDATE user_activity_trend t
INNER JOIN temp_cumulative c ON t.stat_date = c.stat_date
SET t.total_users = c.cumulative_total;

-- 清理临时表
DROP TEMPORARY TABLE IF EXISTS temp_cumulative;

-- 初始化等级分布数据
INSERT INTO user_level_distribution (level, user_count, percentage, avg_experience, max_experience, min_experience, stat_date)
SELECT 
    level,
    COUNT(*) as user_count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM user WHERE isDelete = 0), 2) as percentage,
    ROUND(AVG(experience), 2) as avg_experience,
    MAX(experience) as max_experience,
    MIN(experience) as min_experience,
    CURDATE() as stat_date
FROM user
WHERE isDelete = 0
GROUP BY level
ORDER BY level;


-- ============================================
-- 定时更新存储过程（可选）
-- ============================================

DELIMITER //

-- 更新城市分布数据
CREATE PROCEDURE IF NOT EXISTS sp_update_city_distribution()
BEGIN
    TRUNCATE TABLE user_city_distribution;
    
    INSERT INTO user_city_distribution (city, user_count, percentage, stat_date)
    SELECT 
        city,
        COUNT(*) as user_count,
        ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM user WHERE isDelete = 0 AND city IS NOT NULL AND city != ''), 2) as percentage,
        CURDATE() as stat_date
    FROM user
    WHERE isDelete = 0 
        AND city IS NOT NULL 
        AND city != ''
    GROUP BY city
    ORDER BY user_count DESC;
END //

-- 更新活跃度趋势数据
CREATE PROCEDURE IF NOT EXISTS sp_update_activity_trend()
BEGIN
    -- 只更新最新一天的数据
    DELETE FROM user_activity_trend WHERE stat_date = CURDATE();
    
    -- 第一步：插入新数据
    INSERT INTO user_activity_trend (stat_date, new_users, active_users, total_users, growth_rate)
    SELECT 
        CURDATE() as stat_date,
        COUNT(*) as new_users,
        COUNT(*) as active_users,
        0 as total_users,
        0.00 as growth_rate
    FROM user
    WHERE isDelete = 0
        AND DATE(createTime) = CURDATE();
    
    -- 第二步：计算累计用户总数（使用临时表）
    DROP TEMPORARY TABLE IF EXISTS temp_cumulative;
    CREATE TEMPORARY TABLE temp_cumulative AS
    SELECT 
        stat_date,
        new_users,
        @total := @total + new_users as cumulative_total
    FROM user_activity_trend,
    (SELECT @total := 0) vars
    ORDER BY stat_date;
    
    -- 第三步：更新主表
    UPDATE user_activity_trend t
    INNER JOIN temp_cumulative c ON t.stat_date = c.stat_date
    SET t.total_users = c.cumulative_total;
    
    -- 清理临时表
    DROP TEMPORARY TABLE IF EXISTS temp_cumulative;
END //

-- 更新等级分布数据
CREATE PROCEDURE IF NOT EXISTS sp_update_level_distribution()
BEGIN
    DELETE FROM user_level_distribution WHERE stat_date = CURDATE();
    
    INSERT INTO user_level_distribution (level, user_count, percentage, avg_experience, max_experience, min_experience, stat_date)
    SELECT 
        level,
        COUNT(*) as user_count,
        ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM user WHERE isDelete = 0), 2) as percentage,
        ROUND(AVG(experience), 2) as avg_experience,
        MAX(experience) as max_experience,
        MIN(experience) as min_experience,
        CURDATE() as stat_date
    FROM user
    WHERE isDelete = 0
    GROUP BY level
    ORDER BY level;
END //

DELIMITER ;


-- ============================================
-- 验证查询
-- ============================================

-- 查看城市分布前10名
SELECT * FROM user_city_distribution ORDER BY user_count DESC LIMIT 10;

-- 查看最近7天的活跃度趋势
SELECT * FROM user_activity_trend ORDER BY stat_date DESC LIMIT 7;

-- 查看等级分布
SELECT * FROM user_level_distribution ORDER BY level;


-- ============================================
-- 说明
-- ============================================
-- 1. 这些表作为 Hive 的兜底方案，当 Hive 不可用时使用
-- 2. 建议通过定时任务（如每天凌晨）执行存储过程更新数据
-- 3. 后端服务会优先查询 Hive，失败后自动切换到 MySQL
-- 4. 可以根据实际需求调整统计维度和更新频率
