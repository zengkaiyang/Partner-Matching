-- 为 team 表添加 tags 字段（用于分类）
ALTER TABLE team 
ADD COLUMN tags VARCHAR(512) DEFAULT NULL COMMENT '标签（JSON数组，用于分类）' AFTER description;

-- 验证字段是否添加成功
DESC team;

-- 示例：为已有队伍添加分类标签
-- UPDATE team SET tags = '["study"]' WHERE id = 1;
-- UPDATE team SET tags = '["work"]' WHERE id = 2;
