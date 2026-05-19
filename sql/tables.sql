-- ============================================
-- Partner Matching 项目完整数据库表结构
-- 生成时间: 2026-05-20
-- ============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. 用户相关表
-- ============================================

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
                        `username` VARCHAR(256) DEFAULT NULL COMMENT '昵称',
                        `userAccount` VARCHAR(256) DEFAULT NULL COMMENT '登录账号',
                        `avatarUrl` VARCHAR(1024) DEFAULT NULL COMMENT '头像',
                        `tags` TEXT COMMENT '标签列表json',
                        `gender` TINYINT DEFAULT NULL COMMENT '性别',
                        `userPassword` VARCHAR(512) NOT NULL COMMENT '密码',
                        `phone` VARCHAR(128) DEFAULT NULL COMMENT '电话',
                        `email` VARCHAR(512) DEFAULT NULL COMMENT '邮箱',
                        `userStatus` INT DEFAULT 0 NOT NULL COMMENT '用户状态',
                        `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        `isDelete` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
                        `userRole` INT DEFAULT 0 NOT NULL COMMENT '用户角色 0-普通用户 1-管理员',
                        `planetCode` VARCHAR(512) DEFAULT NULL COMMENT '星球编号',
                        `bio` TEXT COMMENT '个人简介',
                        `city` VARCHAR(100) DEFAULT NULL COMMENT '城市',
                        `birthday` DATE DEFAULT NULL COMMENT '生日',
                        `age` INT DEFAULT NULL COMMENT '年龄',
                        `level` INT DEFAULT 1 COMMENT '等级（V1-V10）',
                        `wechatOpenId` VARCHAR(128) DEFAULT NULL COMMENT '微信OpenID',
                        `qqOpenId` VARCHAR(128) DEFAULT NULL COMMENT 'QQ OpenID',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_userAccount` (`userAccount`),
                        KEY `idx_username` (`username`),
                        KEY `idx_city` (`city`),
                        KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 第三方账号绑定表
DROP TABLE IF EXISTS `third_party_account`;
CREATE TABLE `third_party_account` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                       `user_id` BIGINT NOT NULL COMMENT '关联的用户ID',
                                       `platform` VARCHAR(50) NOT NULL COMMENT '平台类型：wechat-微信，qq-QQ',
                                       `account` VARCHAR(256) NOT NULL COMMENT '第三方平台账号',
                                       `password` VARCHAR(512) DEFAULT NULL COMMENT '第三方平台密码（加密存储）',
                                       `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `is_delete` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除：0-未删除，1-已删除',
                                       PRIMARY KEY (`id`),
                                       KEY `idx_user_id` (`user_id`),
                                       KEY `idx_platform` (`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方账号绑定表';

-- 用户经验表
DROP TABLE IF EXISTS `user_experience`;
CREATE TABLE `user_experience` (
                                   `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                   `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                   `total_points` INT DEFAULT 0 COMMENT '总积分',
                                   `level` INT DEFAULT 1 COMMENT '等级V1-V10',
                                   `post_count` INT DEFAULT 0 COMMENT '发帖数',
                                   `comment_count` INT DEFAULT 0 COMMENT '评论数',
                                   `share_count` INT DEFAULT 0 COMMENT '转发数',
                                   `like_received` INT DEFAULT 0 COMMENT '获得点赞数',
                                   `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户经验表';

-- 用户行为记录表
DROP TABLE IF EXISTS `user_action_log`;
CREATE TABLE `user_action_log` (
                                   `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                   `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                   `action_type` VARCHAR(50) NOT NULL COMMENT '行为类型：post-发帖, comment-评论, share-转发, like-点赞',
                                   `target_id` BIGINT DEFAULT NULL COMMENT '目标ID（帖子ID或攻略ID）',
                                   `points_earned` INT DEFAULT 0 COMMENT '获得积分',
                                   `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   PRIMARY KEY (`id`),
                                   KEY `idx_user_id` (`user_id`),
                                   KEY `idx_action_type` (`action_type`),
                                   KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为记录表';

-- 用户关注表
DROP TABLE IF EXISTS `user_follow`;
CREATE TABLE `user_follow` (
                               `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                               `follower_id` BIGINT NOT NULL COMMENT '关注者ID',
                               `following_id` BIGINT NOT NULL COMMENT '被关注者ID',
                               `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_follower_following` (`follower_id`, `following_id`),
                               KEY `idx_following_id` (`following_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

-- 浏览历史表
DROP TABLE IF EXISTS `browse_history`;
CREATE TABLE `browse_history` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                  `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                  `content_type` VARCHAR(50) NOT NULL COMMENT '内容类型：post-帖子, strategy-攻略',
                                  `content_id` BIGINT NOT NULL COMMENT '内容ID',
                                  `browse_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_user_id` (`user_id`),
                                  KEY `idx_browse_time` (`browse_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览历史表';

-- ============================================
-- 2. 队伍相关表
-- ============================================

-- 队伍表
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'id',
                        `name` VARCHAR(256) NOT NULL COMMENT '队伍昵称',
                        `description` VARCHAR(1024) DEFAULT NULL COMMENT '描述',
                        `tags` TEXT COMMENT '标签（JSON数组，用于分类）',
                        `maxNum` INT DEFAULT 1 NOT NULL COMMENT '最大人数',
                        `expireTime` DATETIME DEFAULT NULL COMMENT '过期时间',
                        `userId` BIGINT DEFAULT NULL COMMENT '用户id',
                        `status` INT DEFAULT 0 NOT NULL COMMENT '0-公开，1-私有，2-加密',
                        `password` VARCHAR(512) DEFAULT NULL COMMENT '密码',
                        `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        `isDelete` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
                        PRIMARY KEY (`id`),
                        KEY `idx_name` (`name`),
                        KEY `idx_userId` (`userId`),
                        KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='队伍表';

-- 用户队伍关系表
DROP TABLE IF EXISTS `user_team`;
CREATE TABLE `user_team` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'id',
                             `userId` BIGINT DEFAULT NULL COMMENT '用户id',
                             `teamId` BIGINT DEFAULT NULL COMMENT '队伍id',
                             `joinTime` DATETIME DEFAULT NULL COMMENT '加入时间',
                             `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             `isDelete` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
                             PRIMARY KEY (`id`),
                             KEY `idx_userId` (`userId`),
                             KEY `idx_teamId` (`teamId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户队伍关系表';

-- ============================================
-- 3. 标签相关表
-- ============================================

-- 标签统计表
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
                       `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
                       `tag_name` VARCHAR(100) NOT NULL COMMENT '标签名称',
                       `user_count` INT DEFAULT 0 COMMENT '拥有该标签的用户数量',
                       `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                       `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                       `is_delete` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除(0-未删除, 1-已删除)',
                       PRIMARY KEY (`id`),
                       UNIQUE KEY `uk_tag_name` (`tag_name`),
                       KEY `idx_user_count` (`user_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签统计表';

-- ============================================
-- 4. 论坛相关表
-- ============================================

-- 论坛帖子表
DROP TABLE IF EXISTS `forum_post`;
CREATE TABLE `forum_post` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
                              `title` VARCHAR(256) NOT NULL COMMENT '标题',
                              `content` TEXT NOT NULL COMMENT '内容',
                              `summary` VARCHAR(512) DEFAULT NULL COMMENT '摘要',
                              `author_id` BIGINT NOT NULL COMMENT '作者ID',
                              `category` VARCHAR(50) NOT NULL COMMENT '分类：recruit-招聘队员, tech-技术交流, share-经验分享, qa-问答求助',
                              `status` TINYINT DEFAULT 0 COMMENT '状态：0-正常, 1-审核中, 2-已删除',
                              `view_count` INT DEFAULT 0 COMMENT '浏览量',
                              `like_count` INT DEFAULT 0 COMMENT '点赞数',
                              `comment_count` INT DEFAULT 0 COMMENT '评论数',
                              `share_count` INT DEFAULT 0 COMMENT '转发数',
                              `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `is_delete` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
                              PRIMARY KEY (`id`),
                              KEY `idx_author_id` (`author_id`),
                              KEY `idx_category` (`category`),
                              KEY `idx_create_time` (`create_time`),
                              KEY `idx_like_count` (`like_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子表';

-- 论坛评论表
DROP TABLE IF EXISTS `forum_comment`;
CREATE TABLE `forum_comment` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
                                 `post_id` BIGINT NOT NULL COMMENT '帖子ID',
                                 `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                 `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID，0表示顶级评论',
                                 `content` TEXT NOT NULL COMMENT '评论内容',
                                 `like_count` INT DEFAULT 0 COMMENT '点赞数',
                                 `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `is_delete` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_post_id` (`post_id`),
                                 KEY `idx_user_id` (`user_id`),
                                 KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛评论表';

-- 帖子标签关联表
DROP TABLE IF EXISTS `post_tag`;
CREATE TABLE `post_tag` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `post_id` BIGINT NOT NULL COMMENT '帖子ID',
                            `tag_name` VARCHAR(100) NOT NULL COMMENT '标签名称',
                            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            PRIMARY KEY (`id`),
                            KEY `idx_post_id` (`post_id`),
                            KEY `idx_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子标签关联表';

-- 帖子点赞表
DROP TABLE IF EXISTS `post_like`;
CREATE TABLE `post_like` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                             `post_id` BIGINT NOT NULL COMMENT '帖子ID',
                             `user_id` BIGINT NOT NULL COMMENT '用户ID',
                             `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
                             KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子点赞表';

-- 帖子收藏表
DROP TABLE IF EXISTS `post_favorite`;
CREATE TABLE `post_favorite` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                 `post_id` BIGINT NOT NULL COMMENT '帖子ID',
                                 `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                 `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
                                 KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子收藏表';

-- ============================================
-- 5. 攻略相关表
-- ============================================

-- 攻略表
DROP TABLE IF EXISTS `strategy`;
CREATE TABLE `strategy` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '攻略ID',
                            `title` VARCHAR(256) NOT NULL COMMENT '标题',
                            `content` TEXT NOT NULL COMMENT '内容',
                            `summary` VARCHAR(512) DEFAULT NULL COMMENT '摘要',
                            `cover_image` VARCHAR(512) DEFAULT NULL COMMENT '封面图片URL',
                            `author_id` BIGINT NOT NULL COMMENT '作者ID',
                            `category` VARCHAR(50) NOT NULL COMMENT '分类：study-学习, work-工作, game-游戏, life-生活',
                            `type` VARCHAR(20) NOT NULL DEFAULT 'manual' COMMENT '类型：manual-手写, ai-AI分析',
                            `status` TINYINT DEFAULT 0 COMMENT '状态：0-正常, 1-审核中, 2-已删除',
                            `view_count` INT DEFAULT 0 COMMENT '浏览量',
                            `like_count` INT DEFAULT 0 COMMENT '点赞数',
                            `favorite_count` INT DEFAULT 0 COMMENT '收藏数',
                            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `is_delete` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
                            PRIMARY KEY (`id`),
                            KEY `idx_author_id` (`author_id`),
                            KEY `idx_category` (`category`),
                            KEY `idx_type` (`type`),
                            KEY `idx_create_time` (`create_time`),
                            KEY `idx_view_count` (`view_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='攻略表';

-- 攻略标签关联表
DROP TABLE IF EXISTS `strategy_tag`;
CREATE TABLE `strategy_tag` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                `strategy_id` BIGINT NOT NULL COMMENT '攻略ID',
                                `tag_name` VARCHAR(100) NOT NULL COMMENT '标签名称',
                                `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                PRIMARY KEY (`id`),
                                KEY `idx_strategy_id` (`strategy_id`),
                                KEY `idx_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='攻略标签关联表';

-- 攻略点赞表
DROP TABLE IF EXISTS `strategy_like`;
CREATE TABLE `strategy_like` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                 `strategy_id` BIGINT NOT NULL COMMENT '攻略ID',
                                 `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                 `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_strategy_user` (`strategy_id`, `user_id`),
                                 KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='攻略点赞表';

-- 攻略收藏表
DROP TABLE IF EXISTS `strategy_favorite`;
CREATE TABLE `strategy_favorite` (
                                     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                     `strategy_id` BIGINT NOT NULL COMMENT '攻略ID',
                                     `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                     `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uk_strategy_user` (`strategy_id`, `user_id`),
                                     KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='攻略收藏表';

-- ============================================
-- 6. 大厅消息表
-- ============================================

-- 大厅消息表
DROP TABLE IF EXISTS `hall_message`;
CREATE TABLE `hall_message` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                `username` VARCHAR(256) DEFAULT NULL COMMENT '用户名',
                                `avatar_url` VARCHAR(1024) DEFAULT NULL COMMENT '头像URL',
                                `content` TEXT NOT NULL COMMENT '消息内容',
                                `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `is_delete` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
                                PRIMARY KEY (`id`),
                                KEY `idx_user_id` (`user_id`),
                                KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大厅消息表';

-- ============================================
-- 7. 数据可视化相关表
-- ============================================

-- Hive标签统计表（MySQL兜底）
DROP TABLE IF EXISTS `hive_tag_stats`;
CREATE TABLE `hive_tag_stats` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                  `tag_name` VARCHAR(100) NOT NULL COMMENT '标签名称',
                                  `category` VARCHAR(50) DEFAULT NULL COMMENT '标签分类',
                                  `total_count` INT DEFAULT 0 COMMENT '使用次数',
                                  `user_count` INT DEFAULT 0 COMMENT '使用用户数',
                                  `sync_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '同步时间',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_tag_name` (`tag_name`),
                                  KEY `idx_category` (`category`),
                                  KEY `idx_total_count` (`total_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Hive标签统计表（MySQL兜底）';

-- 用户城市分布表
DROP TABLE IF EXISTS `user_city_distribution`;
CREATE TABLE `user_city_distribution` (
                                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                          `city` VARCHAR(100) NOT NULL COMMENT '城市名称',
                                          `user_count` INT DEFAULT 0 COMMENT '用户数量',
                                          `percentage` DECIMAL(5, 2) DEFAULT 0 COMMENT '占比百分比',
                                          `stat_date` DATE NOT NULL COMMENT '统计日期',
                                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          PRIMARY KEY (`id`),
                                          UNIQUE KEY `uk_city_date` (`city`, `stat_date`),
                                          KEY `idx_stat_date` (`stat_date`),
                                          KEY `idx_user_count` (`user_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户城市分布统计表';

-- 用户活跃度趋势表
DROP TABLE IF EXISTS `user_activity_trend`;
CREATE TABLE `user_activity_trend` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                       `stat_date` DATE NOT NULL COMMENT '统计日期',
                                       `new_users` INT DEFAULT 0 COMMENT '新增用户数',
                                       `active_users` INT DEFAULT 0 COMMENT '活跃用户数',
                                       `total_users` INT DEFAULT 0 COMMENT '累计用户总数',
                                       `growth_rate` DECIMAL(5, 2) DEFAULT 0 COMMENT '增长率百分比',
                                       `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_stat_date` (`stat_date`),
                                       KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户活跃度趋势统计表';

-- 用户等级分布表
DROP TABLE IF EXISTS `user_level_distribution`;
CREATE TABLE `user_level_distribution` (
                                           `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                           `level` INT NOT NULL COMMENT '等级(LV1-LV8)',
                                           `user_count` INT DEFAULT 0 COMMENT '用户数量',
                                           `percentage` DECIMAL(5, 2) DEFAULT 0 COMMENT '占比百分比',
                                           `avg_experience` DECIMAL(10, 2) DEFAULT 0 COMMENT '平均经验值',
                                           `max_experience` INT DEFAULT 0 COMMENT '最高经验值',
                                           `min_experience` INT DEFAULT 0 COMMENT '最低经验值',
                                           `stat_date` DATE NOT NULL COMMENT '统计日期',
                                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY `uk_level_date` (`level`, `stat_date`),
                                           KEY `idx_stat_date` (`stat_date`),
                                           KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户等级分布统计表';

-- 用户活跃度统计表
DROP TABLE IF EXISTS `user_activity_stats`;
CREATE TABLE `user_activity_stats` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                       `stat_date` DATE NOT NULL COMMENT '统计日期',
                                       `active_users` INT DEFAULT 0 COMMENT '活跃用户数',
                                       `new_posts` INT DEFAULT 0 COMMENT '新发帖数',
                                       `new_comments` INT DEFAULT 0 COMMENT '新评论数',
                                       `sync_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '同步时间',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户活跃度统计表';

-- ============================================
-- 8. 系统配置表
-- ============================================

-- 系统配置表
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                 `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
                                 `config_value` TEXT COMMENT '配置值',
                                 `description` VARCHAR(256) DEFAULT NULL COMMENT '配置描述',
                                 `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 插入默认配置
INSERT INTO `system_config` (`config_key`, `config_value`, `description`) VALUES
                                                                              ('site_name', 'Partner Matching', '网站名称'),
                                                                              ('site_description', '智能伙伴匹配平台', '网站描述'),
                                                                              ('allow_register', 'true', '是否开放注册'),
                                                                              ('page_size', '10', '每页显示数量'),
                                                                              ('login_lock_enabled', 'true', '登录失败锁定开关'),
                                                                              ('login_max_attempts', '5', '登录最大尝试次数'),
                                                                              ('login_lock_duration', '30', '登录锁定时长(分钟)'),
                                                                              ('min_password_length', '6', '密码最小长度');

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 表结构说明
-- ============================================
-- 共创建 28 张表：
-- 1. user - 用户表
-- 2. third_party_account - 第三方账号绑定表
-- 3. user_experience - 用户经验表
-- 4. user_action_log - 用户行为记录表
-- 5. user_follow - 用户关注表
-- 6. browse_history - 浏览历史表
-- 7. team - 队伍表
-- 8. user_team - 用户队伍关系表
-- 9. tag - 标签统计表
-- 10. forum_post - 论坛帖子表
-- 11. forum_comment - 论坛评论表
-- 12. post_tag - 帖子标签关联表
-- 13. post_like - 帖子点赞表
-- 14. post_favorite - 帖子收藏表
-- 15. strategy - 攻略表
-- 16. strategy_tag - 攻略标签关联表
-- 17. strategy_like - 攻略点赞表
-- 18. strategy_favorite - 攻略收藏表
-- 19. hall_message - 大厅消息表
-- 20. hive_tag_stats - Hive标签统计表（MySQL兜底）
-- 21. user_city_distribution - 用户城市分布表
-- 22. user_activity_trend - 用户活跃度趋势表
-- 23. user_level_distribution - 用户等级分布表
-- 24. user_activity_stats - 用户活跃度统计表
-- 25. system_config - 系统配置表
--
-- 注意：请根据实际需求修改数据库名称和字符集
-- ============================================
