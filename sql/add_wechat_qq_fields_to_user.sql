-- 为 user 表添加微信和QQ OpenID字段
-- 这些字段用于存储第三方平台的用户标识

ALTER TABLE user 
ADD COLUMN wechat_open_id VARCHAR(128) DEFAULT NULL COMMENT '微信OpenID' AFTER planetCode,
ADD COLUMN qq_open_id VARCHAR(128) DEFAULT NULL COMMENT 'QQ OpenID' AFTER wechat_open_id;

-- 添加索引以提高查询性能
CREATE INDEX idx_wechat_open_id ON user(wechat_open_id);
CREATE INDEX idx_qq_open_id ON user(qq_open_id);

-- 验证字段是否添加成功
DESC user;
