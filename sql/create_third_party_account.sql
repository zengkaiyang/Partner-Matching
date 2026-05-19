-- 创建第三方账号绑定表
CREATE TABLE IF NOT EXISTS third_party_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '关联的用户ID',
    platform VARCHAR(20) NOT NULL COMMENT '平台类型：wechat-微信，qq-QQ',
    account VARCHAR(100) NOT NULL COMMENT '第三方平台账号',
    password VARCHAR(255) NOT NULL COMMENT '第三方平台密码（加密存储）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_platform_account (platform, account),
    UNIQUE KEY uk_platform_account (platform, account)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方账号绑定表';

-- 插入测试数据（可选）
-- 假设用户ID为1的用户绑定了微信和QQ账号
INSERT INTO third_party_account (user_id, platform, account, password) VALUES
(1, 'wechat', 'wechat_user001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
(1, 'qq', 'qq_user001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- 注意：上面的密码是加密后的 "123456"，实际使用时需要 bcrypt 加密
