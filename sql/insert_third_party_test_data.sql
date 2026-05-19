-- ============================================
-- 第三方登录测试数据 - 正确版本
-- ============================================

-- 说明：
-- 1. third_party_account 表使用纯MD5加密（不加盐）
-- 2. 密码 "123456" 的 MD5 值是：e10adc3949ba59abbe56e057f20f883e
-- 3. 需要先确保 user 表中存在对应的用户

-- 步骤1：确认用户ID为1的用户存在
SELECT id, username, userAccount FROM user WHERE id = 1;

-- 如果不存在，创建一个测试用户
-- INSERT INTO user (username, userAccount, userPassword, planetCode, tags, level) 
-- VALUES ('测试用户', 'test_user', 'e10adc3949ba59abbe56e057f20f883e', '001', '["Java"]', 1);

-- 步骤2：删除旧的测试数据
DELETE FROM third_party_account WHERE account IN ('test_wechat', 'test_qq');

-- 步骤3：插入测试数据
-- 密码是 "123456"，使用纯MD5加密：MD5("123456") = e10adc3949ba59abbe56e057f20f883e
INSERT INTO third_party_account (user_id, platform, account, password) 
VALUES 
(1, 'wechat', 'test_wechat', 'e10adc3949ba59abbe56e057f20f883e'),
(1, 'qq', 'test_qq', 'e10adc3949ba59abbe56e057f20f883e');

-- 步骤4：验证数据
SELECT * FROM third_party_account WHERE account IN ('test_wechat', 'test_qq');

-- ============================================
-- 测试账号信息
-- ============================================
-- 微信账号：test_wechat，密码：123456
-- QQ账号：test_qq，密码：123456
-- 
-- 这两个账号都绑定到 user_id = 1 的用户
-- 登录成功后，会以 user_id = 1 的身份登录系统
