-- ============================================
-- Hive 表重建脚本 - 使用 TEXTFILE 格式
-- 用于解决 ORC 格式 INSERT 时的 MapReduce 错误
-- ============================================

-- 删除旧表
DROP TABLE IF EXISTS user_info;
DROP TABLE IF EXISTS user_tags_detail;
DROP TABLE IF EXISTS tag_statistics;
DROP TABLE IF EXISTS tech_category_stats;
DROP TABLE IF EXISTS crawl_job_log;

-- 1. 用户基础信息表 - 使用 TEXTFILE
CREATE TABLE IF NOT EXISTS user_info (
    id BIGINT COMMENT '用户ID',
    username STRING COMMENT '用户名',
    user_account STRING COMMENT '用户账号',
    avatar_url STRING COMMENT '头像URL',
    gender TINYINT COMMENT '性别 0-男 1-女',
    phone STRING COMMENT '电话',
    email STRING COMMENT '邮箱',
    user_status TINYINT COMMENT '用户状态 0-正常',
    user_role TINYINT COMMENT '用户角色 0-普通用户 1-管理员',
    planet_code STRING COMMENT '星球编号',
    tags STRING COMMENT '标签JSON数组',
    bio STRING COMMENT '个人简介',
    city STRING COMMENT '城市',
    level INT COMMENT '等级(LV1-LV8)',
    experience BIGINT COMMENT '经验值',
    birthday DATE COMMENT '生日',
    create_time TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT COMMENT '是否删除 0-未删除'
)
COMMENT '用户基础信息表'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

-- 2. 用户标签明细表 - 使用 TEXTFILE
CREATE TABLE IF NOT EXISTS user_tags_detail (
    id BIGINT COMMENT '记录ID',
    user_id BIGINT COMMENT '用户ID',
    username STRING COMMENT '用户名',
    tag STRING COMMENT '标签名称',
    platform STRING COMMENT '来源平台',
    crawl_time TIMESTAMP COMMENT '爬取时间'
)
COMMENT '用户标签明细表（每个标签一条记录）'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

-- 3. 标签统计表 - 使用 TEXTFILE
CREATE TABLE IF NOT EXISTS tag_statistics (
    tag STRING COMMENT '标签名称',
    tag_count BIGINT COMMENT '使用次数',
    user_count BIGINT COMMENT '用户数量',
    category STRING COMMENT '技术分类',
    stat_date DATE COMMENT '统计日期'
)
COMMENT '标签统计表'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

-- 4. 技术栈分类统计表 - 使用 TEXTFILE
CREATE TABLE IF NOT EXISTS tech_category_stats (
    category STRING COMMENT '技术分类（后端/前端/数据库等）',
    tech_count BIGINT COMMENT '技术数量',
    user_count BIGINT COMMENT '用户数量',
    percentage DOUBLE COMMENT '占比百分比',
    stat_date DATE COMMENT '统计日期'
)
COMMENT '技术栈分类统计表'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

-- 5. 爬取任务记录表 - 使用 TEXTFILE
CREATE TABLE IF NOT EXISTS crawl_job_log (
    job_id STRING COMMENT '任务ID',
    start_time TIMESTAMP COMMENT '开始时间',
    end_time TIMESTAMP COMMENT '结束时间',
    status STRING COMMENT '状态 success/failed',
    crawled_count INT COMMENT '爬取数量',
    cleaned_count INT COMMENT '清洗数量',
    error_message STRING COMMENT '错误信息'
)
COMMENT '爬取任务日志表'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

-- ============================================
-- 重新创建视图
-- ============================================
CREATE VIEW IF NOT EXISTS view_top_20_tags AS
SELECT 
    tag,
    tag_count,
    user_count,
    ROUND(user_count * 100.0 / SUM(user_count) OVER(), 2) as percentage
FROM tag_statistics
ORDER BY user_count DESC
LIMIT 20;

CREATE VIEW IF NOT EXISTS view_tech_distribution AS
SELECT 
    category,
    SUM(user_count) as total_users,
    ROUND(SUM(user_count) * 100.0 / SUM(SUM(user_count)) OVER(), 2) as percentage
FROM tech_category_stats
GROUP BY category
ORDER BY total_users DESC;
