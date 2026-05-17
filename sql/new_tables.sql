-- =============================================
-- 论坛模块表
-- =============================================

-- 论坛帖子表
CREATE TABLE IF NOT EXISTS forum_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '帖子ID',
    title VARCHAR(256) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    summary VARCHAR(512) COMMENT '摘要',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    category VARCHAR(50) NOT NULL COMMENT '分类：recruit-招聘队员, tech-技术交流, share-经验分享, qa-问答求助',
    status TINYINT DEFAULT 0 COMMENT '状态：0-正常, 1-审核中, 2-已删除',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    share_count INT DEFAULT 0 COMMENT '转发数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
    INDEX idx_author_id (author_id),
    INDEX idx_category (category),
    INDEX idx_create_time (create_time),
    INDEX idx_like_count (like_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子表';

-- 论坛评论表
CREATE TABLE IF NOT EXISTS forum_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父评论ID，0表示顶级评论',
    content TEXT NOT NULL COMMENT '评论内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛评论表';

-- 帖子标签关联表
CREATE TABLE IF NOT EXISTS post_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    tag_name VARCHAR(100) NOT NULL COMMENT '标签名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_post_id (post_id),
    INDEX idx_tag_name (tag_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子标签关联表';

-- 帖子点赞表
CREATE TABLE IF NOT EXISTS post_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_post_user (post_id, user_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子点赞表';

-- 帖子收藏表
CREATE TABLE IF NOT EXISTS post_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE KEY uk_post_user (post_id, user_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子收藏表';

-- =============================================
-- 攻略模块表
-- =============================================

-- 攻略表
CREATE TABLE IF NOT EXISTS strategy (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '攻略ID',
    title VARCHAR(256) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    summary VARCHAR(512) COMMENT '摘要',
    cover_image VARCHAR(512) COMMENT '封面图片URL',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    category VARCHAR(50) NOT NULL COMMENT '分类：study-学习, work-工作, game-游戏, life-生活',
    type VARCHAR(20) NOT NULL DEFAULT 'manual' COMMENT '类型：manual-手写, ai-AI分析',
    status TINYINT DEFAULT 0 COMMENT '状态：0-正常, 1-审核中, 2-已删除',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    favorite_count INT DEFAULT 0 COMMENT '收藏数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
    INDEX idx_author_id (author_id),
    INDEX idx_category (category),
    INDEX idx_type (type),
    INDEX idx_create_time (create_time),
    INDEX idx_view_count (view_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='攻略表';

-- 攻略标签关联表
CREATE TABLE IF NOT EXISTS strategy_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    strategy_id BIGINT NOT NULL COMMENT '攻略ID',
    tag_name VARCHAR(100) NOT NULL COMMENT '标签名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_strategy_id (strategy_id),
    INDEX idx_tag_name (tag_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='攻略标签关联表';

-- 攻略点赞表
CREATE TABLE IF NOT EXISTS strategy_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    strategy_id BIGINT NOT NULL COMMENT '攻略ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_strategy_user (strategy_id, user_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='攻略点赞表';

-- 攻略收藏表
CREATE TABLE IF NOT EXISTS strategy_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    strategy_id BIGINT NOT NULL COMMENT '攻略ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE KEY uk_strategy_user (strategy_id, user_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='攻略收藏表';

-- =============================================
-- 用户经验与等级表
-- =============================================

-- 用户经验表
CREATE TABLE IF NOT EXISTS user_experience (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_points INT DEFAULT 0 COMMENT '总积分',
    level INT DEFAULT 1 COMMENT '等级V1-V10',
    post_count INT DEFAULT 0 COMMENT '发帖数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    share_count INT DEFAULT 0 COMMENT '转发数',
    like_received INT DEFAULT 0 COMMENT '获得点赞数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户经验表';

-- 用户行为记录表（用于统计经验）
CREATE TABLE IF NOT EXISTS user_action_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    action_type VARCHAR(50) NOT NULL COMMENT '行为类型：post-发帖, comment-评论, share-转发, like-点赞',
    target_id BIGINT COMMENT '目标ID（帖子ID或攻略ID）',
    points_earned INT DEFAULT 0 COMMENT '获得积分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_action_type (action_type),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为记录表';

-- =============================================
-- 浏览历史表
-- =============================================

-- 浏览历史表
CREATE TABLE IF NOT EXISTS browse_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content_type VARCHAR(50) NOT NULL COMMENT '内容类型：post-帖子, strategy-攻略',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    browse_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    INDEX idx_user_id (user_id),
    INDEX idx_browse_time (browse_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览历史表';

-- =============================================
-- 关注关系表
-- =============================================

-- 用户关注表
CREATE TABLE IF NOT EXISTS user_follow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    follower_id BIGINT NOT NULL COMMENT '关注者ID',
    following_id BIGINT NOT NULL COMMENT '被关注者ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    UNIQUE KEY uk_follower_following (follower_id, following_id),
    INDEX idx_following_id (following_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

-- =============================================
-- 数据可视化相关表
-- =============================================

-- Hive数据同步表（MySQL兜底表）
CREATE TABLE IF NOT EXISTS hive_tag_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    tag_name VARCHAR(100) NOT NULL COMMENT '标签名称',
    category VARCHAR(50) COMMENT '标签分类',
    total_count INT DEFAULT 0 COMMENT '使用次数',
    user_count INT DEFAULT 0 COMMENT '使用用户数',
    sync_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '同步时间',
    INDEX idx_tag_name (tag_name),
    INDEX idx_category (category),
    INDEX idx_total_count (total_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Hive标签统计表（MySQL兜底）';

-- 用户城市分布表
CREATE TABLE IF NOT EXISTS user_city_distribution (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    city VARCHAR(100) NOT NULL COMMENT '城市名称',
    user_count INT DEFAULT 0 COMMENT '用户数量',
    sync_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '同步时间',
    INDEX idx_city (city)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户城市分布表';

-- 用户活跃度统计表
CREATE TABLE IF NOT EXISTS user_activity_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    stat_date DATE NOT NULL COMMENT '统计日期',
    active_users INT DEFAULT 0 COMMENT '活跃用户数',
    new_posts INT DEFAULT 0 COMMENT '新发帖数',
    new_comments INT DEFAULT 0 COMMENT '新评论数',
    sync_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '同步时间',
    UNIQUE KEY uk_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户活跃度统计表';

-- =============================================
-- 系统配置表
-- =============================================

-- 系统配置表
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    description VARCHAR(256) COMMENT '配置描述',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 插入默认配置
INSERT INTO system_config (config_key, config_value, description) VALUES
('site_name', 'Partner Matching', '网站名称'),
('site_description', '智能伙伴匹配平台', '网站描述'),
('allow_register', 'true', '是否开放注册'),
('page_size', '10', '每页显示数量'),
('login_lock_enabled', 'true', '登录失败锁定开关'),
('login_max_attempts', '5', '登录最大尝试次数'),
('login_lock_duration', '30', '登录锁定时长(分钟)'),
('min_password_length', '6', '密码最小长度');
