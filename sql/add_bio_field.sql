-- 为 user 表添加 bio 字段（个人简介）

ALTER TABLE user ADD COLUMN bio VARCHAR(500) COMMENT '个人简介' AFTER planet_code;

-- 更新已有数据的 bio 字段为默认值
UPDATE user SET bio = '暂无简介' WHERE bio IS NULL;
