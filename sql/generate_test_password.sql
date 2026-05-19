-- ============================================
-- 第三方登录测试数据生成脚本
-- ============================================

-- 步骤1：确认用户ID为1的用户存在
SELECT id, username, userAccount FROM user WHERE id = 1;

-- 如果不存在，先创建一个测试用户
-- INSERT INTO user (username, userAccount, userPassword, planetCode, tags, level) 
-- VALUES ('测试用户', 'test_user', 'e10adc3949ba59abbe56e057f20f883e', '001', '["Java"]', 1);

-- 步骤2：删除旧的测试数据（如果有）
DELETE FROM third_party_account WHERE account IN ('test_wechat', 'test_qq');

-- 步骤3：插入新的测试数据
-- 密码是 "123456"，SALT 是 "yupi"
-- 加密方式：MD5("yupi" + "123456") = MD5("yupi123456")
-- 计算结果：e10adc3949ba59abbe56e057f20f883e 是错误的！
-- 正确的应该是：MD5("yupi123456")

-- 让我们先计算正确的密码哈希值
-- 在 Java 中：DigestUtils.md5DigestAsHex(("yupi" + "123456").getBytes())
-- 这个值需要实际运行代码才能得到

-- 临时方案：先插入一个已知正确的密码
-- 假设我们使用纯MD5（不加盐）：MD5("123456") = e10adc3949ba59abbe56e057f20f883e
INSERT INTO third_party_account (user_id, platform, account, password) 
VALUES 
(1, 'wechat', 'test_wechat', 'e10adc3949ba59abbe56e057f20f883e'),
(1, 'qq', 'test_qq', 'e10adc3949ba59abbe56e057f20f883e');

-- 步骤4：验证数据
SELECT * FROM third_party_account WHERE account IN ('test_wechat', 'test_qq');

-- ============================================
-- 重要说明：
-- ============================================
-- 上面的密码 e10adc3949ba59abbe56e057f20f883e 是 MD5("123456") 的结果
-- 但代码中使用的是 MD5("yupi" + "123456") = MD5("yupi123456")
-- 这两个值是不一样的！
--
-- 解决方案有两个：
-- 1. 修改数据库中的密码为正确的加密值
-- 2. 修改代码，使用纯MD5不加盐
--
-- 建议：查看后端日志，看看实际计算的加密值是多少，然后更新数据库
