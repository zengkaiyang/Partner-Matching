-- ============================================
-- 伙伴匹配系统 - 完整数据库表结构
-- 执行前请替换 your_database_name 为你的数据库名
-- ============================================

USE your_database_name;

-- 1. 扩展用户表字段
ALTER TABLE user ADD COLUMN IF NOT EXISTS tags VARCHAR(1024) COMMENT '用户标签(JSON数组)';
ALTER TABLE user ADD COLUMN IF NOT EXISTS bio VARCHAR(512) COMMENT '个人简介';
ALTER TABLE user ADD COLUMN IF NOT EXISTS experience INT DEFAULT 0 COMMENT '经验值';
ALTER TABLE user ADD COLUMN IF NOT EXISTS level INT DEFAULT 1 COMMENT '等级(LV1-LV8)';
ALTER TABLE user ADD COLUMN IF NOT EXISTS city VARCHAR(64) COMMENT '城市';
ALTER TABLE user ADD COLUMN IF NOT EXISTS avatar_frame VARCHAR(256) COMMENT '头像框';
ALTER TABLE user ADD COLUMN IF NOT EXISTS last_login_time DATETIME COMMENT '最后登录时间';
ALTER TABLE user ADD COLUMN IF NOT EXISTS login_count INT DEFAULT 0 COMMENT '登录次数';

-- 2. 用户标签关系表
CREATE TABLE IF NOT EXISTS user_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    user_id BIGINT NOT NULL COMMENT '用户id',
    tag_name VARCHAR(128) NOT NULL COMMENT '标签名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    is_delete TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
    INDEX idx_user_id (user_id),
    INDEX idx_tag_name (tag_name)
) COMMENT '用户标签关系表';

-- 3. 论坛帖子表
CREATE TABLE IF NOT EXISTS post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    title VARCHAR(256) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    user_id BIGINT NOT NULL COMMENT '作者id',
    category VARCHAR(64) COMMENT '分类',
    cover_image VARCHAR(512) COMMENT '封面图',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    share_count INT DEFAULT 0 COMMENT '转发数',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶',
    is_essence TINYINT DEFAULT 0 COMMENT '是否精华',
    status TINYINT DEFAULT 0 COMMENT '状态:0-正常,1-审核中,2-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
    INDEX idx_user_id (user_id),
    INDEX idx_category (category),
    INDEX idx_create_time (create_time)
) COMMENT '论坛帖子表';

-- 4. 帖子评论表
CREATE TABLE IF NOT EXISTS post_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    post_id BIGINT NOT NULL COMMENT '帖子id',
    user_id BIGINT NOT NULL COMMENT '评论者id',
    parent_id BIGINT DEFAULT 0 COMMENT '父评论id(0表示一级评论)',
    reply_user_id BIGINT DEFAULT 0 COMMENT '回复的用户id',
    content TEXT NOT NULL COMMENT '评论内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id)
) COMMENT '帖子评论表';

-- 5. 帖子点赞表
CREATE TABLE IF NOT EXISTS post_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    post_id BIGINT NOT NULL COMMENT '帖子id',
    user_id BIGINT NOT NULL COMMENT '用户id',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_post_user (post_id, user_id),
    INDEX idx_user_id (user_id)
) COMMENT '帖子点赞表';

-- 6. 攻略表
CREATE TABLE IF NOT EXISTS strategy (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    title VARCHAR(256) NOT NULL COMMENT '标题',
    content LONGTEXT COMMENT '内容(支持Markdown)',
    user_id BIGINT NOT NULL COMMENT '作者id',
    category VARCHAR(64) COMMENT '分类',
    difficulty VARCHAR(32) COMMENT '难度:简单/中等/困难',
    cover_image VARCHAR(512) COMMENT '封面图',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    collect_count INT DEFAULT 0 COMMENT '收藏数',
    share_count INT DEFAULT 0 COMMENT '转发数',
    is_essence TINYINT DEFAULT 0 COMMENT '是否精华',
    status TINYINT DEFAULT 0 COMMENT '状态:0-正常,1-审核中,2-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
    INDEX idx_user_id (user_id),
    INDEX idx_category (category),
    INDEX idx_create_time (create_time)
) COMMENT '攻略表';

-- 7. 攻略收藏表
CREATE TABLE IF NOT EXISTS strategy_collect (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    strategy_id BIGINT NOT NULL COMMENT '攻略id',
    user_id BIGINT NOT NULL COMMENT '用户id',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_strategy_user (strategy_id, user_id),
    INDEX idx_user_id (user_id)
) COMMENT '攻略收藏表';

-- 8. 经验记录表
CREATE TABLE IF NOT EXISTS experience_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    user_id BIGINT NOT NULL COMMENT '用户id',
    type VARCHAR(32) NOT NULL COMMENT '类型:post-发帖,comment-评论,share-转发,like-点赞',
    source_id BIGINT COMMENT '来源id(帖子id或评论id)',
    experience INT NOT NULL COMMENT '获得的经验值',
    description VARCHAR(256) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) COMMENT '经验记录表';

-- 9. 排行榜缓存表
CREATE TABLE IF NOT EXISTS ranking_cache (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    ranking_type VARCHAR(32) NOT NULL COMMENT '排行类型:experience-经验榜,activity-活跃榜',
    user_id BIGINT NOT NULL COMMENT '用户id',
    rank_no INT NOT NULL COMMENT '排名',
    score DECIMAL(10, 2) NOT NULL COMMENT '分数/经验值',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_type_user (ranking_type, user_id),
    INDEX idx_ranking_type (ranking_type),
    INDEX idx_rank_no (rank_no)
) COMMENT '排行榜缓存表';

-- 10. 标签统计表(MySQL兜底)
CREATE TABLE IF NOT EXISTS tag_statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    tag_name VARCHAR(128) NOT NULL COMMENT '标签名称',
    category VARCHAR(64) COMMENT '分类',
    total_count INT DEFAULT 0 COMMENT '总使用次数',
    user_count INT DEFAULT 0 COMMENT '使用用户数',
    trend_data JSON COMMENT '趋势数据(近7天)',
    stat_date DATE NOT NULL COMMENT '统计日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_tag_date (tag_name, stat_date),
    INDEX idx_stat_date (stat_date),
    INDEX idx_category (category)
) COMMENT '标签统计表(MySQL兜底)';

-- 11. 用户活跃度统计表
CREATE TABLE IF NOT EXISTS user_activity_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    stat_date DATE NOT NULL COMMENT '统计日期',
    active_users INT DEFAULT 0 COMMENT '活跃用户数',
    new_users INT DEFAULT 0 COMMENT '新增用户数',
    login_count INT DEFAULT 0 COMMENT '登录次数',
    post_count INT DEFAULT 0 COMMENT '发帖数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_stat_date (stat_date),
    INDEX idx_stat_date (stat_date)
) COMMENT '用户活跃度统计表';

-- 12. 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    admin_id BIGINT NOT NULL COMMENT '管理员id',
    operation_type VARCHAR(64) NOT NULL COMMENT '操作类型',
    operation_desc VARCHAR(512) COMMENT '操作描述',
    target_type VARCHAR(64) COMMENT '目标类型',
    target_id BIGINT COMMENT '目标id',
    ip_address VARCHAR(64) COMMENT 'IP地址',
    user_agent VARCHAR(512) COMMENT '用户代理',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_admin_id (admin_id),
    INDEX idx_create_time (create_time)
) COMMENT '操作日志表';

-- 13. 系统配置表
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    config_key VARCHAR(128) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_desc VARCHAR(256) COMMENT '配置描述',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_config_key (config_key)
) COMMENT '系统配置表';
