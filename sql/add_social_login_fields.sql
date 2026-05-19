-- 添加微信和QQ登录相关字段到用户表
ALTER TABLE user ADD COLUMN wechat_open_id VARCHAR(128) DEFAULT NULL COMMENT '微信OpenID';
ALTER TABLE user ADD COLUMN qq_open_id VARCHAR(128) DEFAULT NULL COMMENT 'QQ OpenID';

-- 为新增字段添加索引以提高查询性能
CREATE INDEX idx_wechat_open_id ON user(wechat_open_id);
CREATE INDEX idx_qq_open_id ON user(qq_open_id);
