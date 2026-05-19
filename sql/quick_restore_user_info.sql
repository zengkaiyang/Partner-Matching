-- ============================================
-- 快速重建 user_info 表数据
-- 适用于：表结构正确，但数据被删除的情况
-- ============================================

-- 1. 检查表是否为空
SELECT COUNT(*) as current_count FROM user_info;

-- 2. 检查表结构是否完整
DESCRIBE user_info;

-- 3. 验证扩展字段是否存在
SELECT 
    COUNT(*) as has_city,
    COUNT(level) as has_level,
    COUNT(experience) as has_experience,
    COUNT(birthday) as has_birthday
FROM user_info
WHERE 1=0;  -- 这个查询会失败如果字段不存在

-- 提示：请根据你的实际情况选择以下方案之一

-- ============================================
-- 方案A：使用 Sqoop 从 MySQL 导入（推荐）
-- ============================================
-- 在终端执行以下命令：
-- 
-- sqoop import \
--   --connect jdbc:mysql://localhost:3306/zzkkyy \
--   --username root \
--   --password root \
--   --table user \
--   --hive-import \
--   --hive-table user_info \
--   --hive-overwrite \
--   --fields-terminated-by ',' \
--   --null-string '\\N' \
--   --null-non-string '\\N' \
--   --m 1

-- ============================================
-- 方案B：插入测试数据（快速验证）
-- ============================================

-- 如果暂时无法从 MySQL 导入，可以先插入一些测试数据验证功能

INSERT INTO user_info VALUES
(1, '张三', 'zhangsan', '/avatar/1.jpg', 0, '13800138000', 'zhang@test.com', 
 0, 0, '001', '["Java","Spring","MySQL"]', 'Java后端开发工程师，5年经验', 
 '北京', 5, 1500, '1990-01-15', 
 '2026-01-10 10:00:00', '2026-05-18 10:00:00', 0),

(2, '李四', 'lisi', '/avatar/2.jpg', 1, '13900139000', 'li@test.com', 
 0, 0, '002', '["Python","Django","Redis"]', 'Python全栈工程师', 
 '上海', 4, 1200, '1992-05-20', 
 '2026-01-15 11:00:00', '2026-05-18 11:00:00', 0),

(3, '王五', 'wangwu', '/avatar/3.jpg', 0, '13700137000', 'wang@test.com', 
 0, 0, '003', '["Vue","React","TypeScript"]', '前端开发专家，专注于用户体验', 
 '深圳', 6, 2000, '1988-08-10', 
 '2026-02-01 12:00:00', '2026-05-18 12:00:00', 0),

(4, '赵六', 'zhaoliu', '/avatar/4.jpg', 1, '13600136000', 'zhao@test.com', 
 0, 0, '004', '["Go","Kubernetes","Docker"]', 'DevOps工程师，云原生爱好者', 
 '杭州', 5, 1800, '1991-03-25', 
 '2026-02-10 13:00:00', '2026-05-18 13:00:00', 0),

(5, '孙七', 'sunqi', '/avatar/5.jpg', 0, '13500135000', 'sun@test.com', 
 0, 0, '005', '["JavaScript","Node.js","MongoDB"]', '全栈开发工程师', 
 '成都', 3, 900, '1995-11-30', 
 '2026-02-20 14:00:00', '2026-05-18 14:00:00', 0),

(6, '周八', 'zhouba', '/avatar/6.jpg', 0, '13400134000', 'zhou@test.com', 
 0, 0, '006', '["Java","SpringBoot","微服务"]', '架构师，专注于分布式系统', 
 '北京', 7, 2500, '1985-06-18', 
 '2026-03-01 15:00:00', '2026-05-18 15:00:00', 0),

(7, '吴九', 'wujiu', '/avatar/7.jpg', 1, '13300133000', 'wu@test.com', 
 0, 0, '007', '["Python","机器学习","TensorFlow"]', 'AI算法工程师', 
 '上海', 4, 1300, '1993-09-05', 
 '2026-03-10 16:00:00', '2026-05-18 16:00:00', 0),

(8, '郑十', 'zhengshi', '/avatar/8.jpg', 0, '13200132000', 'zheng@test.com', 
 0, 0, '008', '["Vue","ElementUI","ECharts"]', '前端可视化专家', 
 '广州', 5, 1600, '1990-12-12', 
 '2026-03-20 17:00:00', '2026-05-18 17:00:00', 0),

(9, '钱十一', 'qianshiyi', '/avatar/9.jpg', 0, '13100131000', 'qian@test.com', 
 0, 0, '009', '["MySQL","PostgreSQL","数据库优化"]', '数据库管理员', 
 '深圳', 6, 2100, '1987-04-22', 
 '2026-04-01 18:00:00', '2026-05-18 18:00:00', 0),

(10, '陈十二', 'chenshier', '/avatar/10.jpg', 1, '13000130000', 'chen@test.com', 
 0, 0, '010', '["Linux","Shell","自动化运维"]', '系统运维工程师', 
 '杭州', 4, 1100, '1994-07-08', 
 '2026-04-10 19:00:00', '2026-05-18 19:00:00', 0);

-- 4. 验证数据导入
SELECT COUNT(*) as total_records FROM user_info;

-- 5. 检查数据分布
SELECT 
    city,
    COUNT(*) as user_count,
    ROUND(AVG(level), 1) as avg_level,
    ROUND(AVG(experience), 0) as avg_experience
FROM user_info
WHERE city IS NOT NULL AND city != ''
GROUP BY city
ORDER BY user_count DESC;

-- 6. 提示后续操作
SELECT '✅ 测试数据已插入，可以执行数据可视化统计了' as message;
SELECT '📌 建议：使用 Sqoop 从 MySQL 导入真实数据' as suggestion;
