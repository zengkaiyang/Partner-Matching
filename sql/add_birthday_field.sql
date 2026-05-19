-- 为 user 表添加 birthday 字段（生日）

ALTER TABLE user ADD COLUMN birthday DATE COMMENT '生日' AFTER city;

-- 更新已有数据的 birthday 字段为 NULL(表示未设置)
UPDATE user SET birthday = NULL WHERE birthday IS NULL;
