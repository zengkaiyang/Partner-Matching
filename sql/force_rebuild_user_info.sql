-- ============================================
-- 强制重建 user_info 表（包含扩展字段）
-- 注意：这会删除现有数据，请确保已备份！
-- ============================================

-- 1. 备份现有数据（如果表存在）
DROP TABLE IF EXISTS user_info_backup;
CREATE TABLE user_info_backup AS
SELECT * FROM user_info;

-- 2. 强制删除旧表
DROP TABLE IF EXISTS user_info;

-- 3. 创建新表（包含 city, level, experience, birthday 字段）
CREATE TABLE user_info (
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
COMMENT '用户基础信息表（已升级 - 包含扩展字段）'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;

-- 4. 从备份恢复数据（扩展字段设为 NULL）
INSERT OVERWRITE TABLE user_info
SELECT 
    id,
    username,
    user_account,
    avatar_url,
    gender,
    phone,
    email,
    user_status,
    user_role,
    planet_code,
    tags,
    bio,
    NULL as city,        -- 新增字段，暂时为空
    NULL as level,       -- 新增字段，暂时为空
    NULL as experience,  -- 新增字段，暂时为空
    NULL as birthday,    -- 新增字段，暂时为空
    create_time,
    update_time,
    is_delete
FROM user_info_backup;

-- 5. 验证新表结构
DESCRIBE user_info;

-- 6. 验证数据
SELECT COUNT(*) as total_records FROM user_info;
SELECT * FROM user_info LIMIT 5;

-- 7. 提示后续操作
-- SELECT '下一步：使用 Sqoop 从 MySQL 导入完整数据' as next_step;
