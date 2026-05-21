-- 创建管理员账号示例SQL
-- 注意：请根据实际情况修改密码和相关信息

-- 插入管理员用户（密码需要BCrypt加密，这里使用 'admin123' 的加密值）
INSERT INTO `user` (
    `username`,
    `userAccount`, 
    `avatarUrl`,
    `gender`,
    `userPassword`,
    `phone`,
    `email`,
    `userStatus`,
    `userRole`,
    `planetCode`,
    `tags`,
    `createTime`,
    `updateTime`,
    `isDelete`
) VALUES (
    '系统管理员',                    -- username: 显示名称
    'admin',                         -- userAccount: 登录账号
    'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',  -- avatarUrl: 头像
    0,                               -- gender: 性别（0-未知，1-男，2-女）
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',  -- userPassword: BCrypt加密后的密码（admin123）
    '13800138000',                   -- phone: 手机号
    'admin@example.com',             -- email: 邮箱
    0,                               -- userStatus: 用户状态（0-正常）
    1,                               -- userRole: 用户角色（1-管理员，0-普通用户）
    'ADMIN001',                      -- planetCode: 星球编号
    '["管理员","系统维护"]',          -- tags: 标签JSON数组
    NOW(),                           -- createTime: 创建时间
    NOW(),                           -- updateTime: 更新时间
    0                                -- isDelete: 是否删除（0-未删除）
);

-- 查询刚创建的管理员账号
SELECT id, username, userAccount, userRole, email, createTime 
FROM user 
WHERE userAccount = 'admin';

-- 如果需要重置管理员密码，可以使用以下语句（将密码重置为 admin123）
-- UPDATE user 
-- SET userPassword = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi'
-- WHERE userAccount = 'admin';
