-- ============================================
-- 样例数据插入脚本（基于实际数据库表结构）
-- 用于填充论坛、攻略、排行榜等功能的数据
-- 表结构来源: new.sql
-- ============================================

-- 1. 插入论坛帖子数据
INSERT INTO post (title, content, user_id, category, view_count, like_count, comment_count, status, is_delete, create_time, update_time) VALUES
('Spring Boot实战经验分享', '最近在学习Spring Boot,总结了一些实战经验,包括项目结构搭建、常用注解使用、异常处理等...', 1, 'tech', 156, 23, 8, 0, 0, NOW(), NOW()),
('如何高效学习编程?', '作为一个编程初学者,经常感到迷茫。今天分享一些我的学习方法,希望能帮助到大家...', 2, 'share', 234, 45, 12, 0, 0, NOW(), NOW()),
('Vue3 + TypeScript最佳实践', '在使用Vue3和TypeScript开发项目时,遇到了一些坑,这里总结一下最佳实践方案...', 3, 'tech', 189, 34, 15, 0, 0, NOW(), NOW()),
('寻找前端开发伙伴', '正在做一个个人项目,需要找一位熟悉Vue或React的前端小伙伴一起合作...', 4, 'recruit', 98, 12, 5, 0, 0, NOW(), NOW()),
('MySQL性能优化技巧', '分享几个MySQL查询优化的实用技巧,包括索引使用、查询重写、表结构设计等...', 5, 'tech', 312, 67, 20, 0, 0, NOW(), NOW()),
('Docker容器化部署指南', '详细介绍如何使用Docker进行应用容器化部署,包括镜像构建、容器编排等内容...', 6, 'share', 278, 56, 18, 0, 0, NOW(), NOW()),
('算法面试高频题解析', '整理了最近面试中遇到的算法题,包括链表、树、动态规划等经典题目...', 7, 'qa', 445, 89, 25, 0, 0, NOW(), NOW()),
('微服务架构设计思考', '在微服务架构设计中,服务拆分、通信方式、数据一致性等都是需要考虑的问题...', 8, 'tech', 367, 72, 22, 0, 0, NOW(), NOW()),
('Git团队协作规范', '制定一套合理的Git工作流对于团队协作非常重要,这里分享我们的实践经验...', 9, 'share', 201, 38, 10, 0, 0, NOW(), NOW()),
('Redis缓存策略详解', '深入探讨Redis的各种应用场景和缓存策略,包括缓存穿透、雪崩、击穿等问题...', 10, 'tech', 523, 98, 30, 0, 0, NOW(), NOW());

-- 2. 插入帖子评论数据
INSERT INTO post_comment (post_id, user_id, parent_id, reply_user_id, content, like_count, is_delete, create_time, update_time) VALUES
(1, 2, 0, 0, '写得很好,学到了很多!', 5, 0, NOW(), NOW()),
(1, 3, 0, 0, 'Spring Boot确实很强大', 3, 0, NOW(), NOW()),
(2, 1, 0, 0, '感谢分享,很有帮助', 8, 0, NOW(), NOW()),
(3, 4, 0, 0, 'TypeScript的类型系统真的很棒', 6, 0, NOW(), NOW()),
(5, 2, 0, 0, '索引优化这部分讲得很清楚', 12, 0, NOW(), NOW()),
(7, 5, 0, 0, '动态规划确实是面试重点', 15, 0, NOW(), NOW()),
(10, 1, 0, 0, 'Redis的应用场景总结得很全面', 20, 0, NOW(), NOW()),
(1, 4, 1, 2, '谢谢认可!', 2, 0, NOW(), NOW()),
(2, 5, 1, 3, '是的,类型安全很重要', 1, 0, NOW(), NOW()),
(5, 6, 1, 2, '可以再看看执行计划', 4, 0, NOW(), NOW());

-- 3. 插入帖子点赞记录
INSERT INTO post_like (post_id, user_id, create_time) VALUES
(1, 2, NOW()),
(1, 3, NOW()),
(1, 5, NOW()),
(2, 1, NOW()),
(2, 4, NOW()),
(3, 2, NOW()),
(3, 6, NOW()),
(5, 1, NOW()),
(5, 3, NOW()),
(5, 7, NOW()),
(7, 2, NOW()),
(7, 4, NOW()),
(7, 8, NOW()),
(10, 1, NOW()),
(10, 2, NOW()),
(10, 4, NOW()),
(10, 5, NOW()),
(10, 8, NOW());

-- 4. 插入攻略数据
INSERT INTO strategy (title, content, user_id, category, difficulty, cover_image, view_count, like_count, collect_count, status, is_delete, create_time, update_time) VALUES
('Java后端开发学习路线', '从Java基础到高级框架,完整的学习路径规划,适合初学者循序渐进地学习...', 1, 'study', '中等', 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg', 567, 89, 45, 0, 0, NOW(), NOW()),
('前端工程化实践指南', '介绍现代前端工程化的各个环节,包括模块化、组件化、自动化构建、性能优化等...', 2, 'study', '困难', 'https://fuss10.elemecdn.com/a/3f/3302e58f9a181d2509f3dc0fa68b0jpeg.jpeg', 423, 67, 34, 0, 0, NOW(), NOW()),
('Python数据分析入门', '从零开始学习Python数据分析,包括NumPy、Pandas、Matplotlib等常用库的使用...', 3, 'study', '简单', 'https://fuss10.elemecdn.com/9/bd/eae1c8e47aa0783b1282f838f6ed3jpg.jpeg', 389, 54, 28, 0, 0, NOW(), NOW()),
('Linux常用命令大全', '整理Linux系统中最常用的命令及其用法,包括文件操作、权限管理、进程管理等...', 4, 'work', '中等', 'https://fuss10.elemecdn.com/c/dd/d2688fb6244cf4397bd4075add928jpg.jpeg', 678, 123, 67, 0, 0, NOW(), NOW()),
('Git版本控制完全指南', '详细讲解Git的使用方法,从基础概念到高级技巧,帮助团队更好地进行版本控制...', 5, 'work', '中等', 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg', 512, 98, 52, 0, 0, NOW(), NOW()),
('王者荣耀上分攻略', '分享王者荣耀的排位上分技巧,包括英雄选择、出装思路、团战配合等要点...', 6, 'game', '困难', 'https://fuss10.elemecdn.com/a/3f/3302e58f9a181d2509f3dc0fa68b0jpeg.jpeg', 834, 156, 89, 0, 0, NOW(), NOW()),
('时间管理方法论', '介绍几种经典的时间管理方法,如番茄工作法、GTD等,帮助你提高工作效率...', 7, 'life', '简单', 'https://fuss10.elemecdn.com/9/bd/eae1c8e47aa0783b1282f838f6ed3jpg.jpeg', 445, 76, 38, 0, 0, NOW(), NOW()),
('健身减脂计划制定', '科学制定健身减脂计划,包括饮食安排、训练计划、休息恢复等方面的建议...', 8, 'life', '中等', 'https://fuss10.elemecdn.com/c/dd/d2688fb6244cf4397bd4075add928jpg.jpeg', 567, 92, 48, 0, 0, NOW(), NOW()),
('面试准备完全手册', '从简历制作到面试技巧,全面准备技术面试,提高求职成功率...', 9, 'work', '困难', 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg', 723, 134, 78, 0, 0, NOW(), NOW()),
('摄影入门教程', '零基础学习摄影,包括构图技巧、光线运用、后期处理等基础知识...', 10, 'life', '简单', 'https://fuss10.elemecdn.com/a/3f/3302e58f9a181d2509f3dc0fa68b0jpeg.jpeg', 389, 65, 32, 0, 0, NOW(), NOW());

-- 5. 插入攻略收藏记录
INSERT INTO strategy_collect (strategy_id, user_id, create_time) VALUES
(1, 2, NOW()),
(1, 4, NOW()),
(1, 6, NOW()),
(2, 3, NOW()),
(2, 7, NOW()),
(4, 1, NOW()),
(4, 5, NOW()),
(4, 8, NOW()),
(5, 2, NOW()),
(5, 9, NOW()),
(6, 1, NOW()),
(6, 3, NOW()),
(6, 5, NOW()),
(6, 7, NOW()),
(9, 2, NOW()),
(9, 4, NOW()),
(9, 10, NOW()),
(10, 1, NOW()),
(10, 8, NOW());

-- 6. 插入用户标签数据
INSERT INTO user_tag (user_id, tag_name, is_delete, create_time) VALUES
(1, 'Java', 0, NOW()),
(1, 'Spring Boot', 0, NOW()),
(1, '后端开发', 0, NOW()),
(2, '前端', 0, NOW()),
(2, 'Vue', 0, NOW()),
(2, 'React', 0, NOW()),
(3, 'Python', 0, NOW()),
(3, '数据分析', 0, NOW()),
(3, '机器学习', 0, NOW()),
(4, 'Linux', 0, NOW()),
(4, '运维', 0, NOW()),
(5, 'Git', 0, NOW()),
(5, 'DevOps', 0, NOW()),
(6, '游戏', 0, NOW()),
(6, '王者荣耀', 0, NOW()),
(7, '效率', 0, NOW()),
(7, '时间管理', 0, NOW()),
(8, '健身', 0, NOW()),
(8, '运动', 0, NOW()),
(9, '面试', 0, NOW()),
(9, '求职', 0, NOW()),
(10, '摄影', 0, NOW()),
(10, '艺术', 0, NOW());

-- 7. 插入经验记录数据
INSERT INTO experience_log (user_id, type, source_id, experience, description, create_time) VALUES
(1, 'post', 1, 50, '发布帖子: Spring Boot实战经验分享', NOW()),
(1, 'like', 1, 10, '获得点赞', NOW()),
(2, 'post', 2, 50, '发布帖子: 如何高效学习编程?', NOW()),
(2, 'comment', 1, 20, '评论帖子', NOW()),
(3, 'post', 3, 50, '发布帖子: Vue3 + TypeScript最佳实践', NOW()),
(4, 'post', 4, 50, '发布帖子: 寻找前端开发伙伴', NOW()),
(5, 'post', 5, 50, '发布帖子: MySQL性能优化技巧', NOW()),
(5, 'strategy', 5, 80, '发布攻略: Git版本控制完全指南', NOW()),
(6, 'strategy', 6, 80, '发布攻略: 王者荣耀上分攻略', NOW()),
(7, 'strategy', 7, 80, '发布攻略: 时间管理方法论', NOW()),
(8, 'strategy', 8, 80, '发布攻略: 健身减脂计划制定', NOW()),
(9, 'strategy', 9, 80, '发布攻略: 面试准备完全手册', NOW()),
(10, 'strategy', 10, 80, '发布攻略: 摄影入门教程', NOW());

-- 8. 更新用户的经验和等级（基于经验记录统计）
UPDATE user SET experience = 280, level = 3 WHERE id = 1;
UPDATE user SET experience = 220, level = 3 WHERE id = 2;
UPDATE user SET experience = 150, level = 2 WHERE id = 3;
UPDATE user SET experience = 120, level = 2 WHERE id = 4;
UPDATE user SET experience = 380, level = 4 WHERE id = 5;
UPDATE user SET experience = 200, level = 3 WHERE id = 6;
UPDATE user SET experience = 180, level = 2 WHERE id = 7;
UPDATE user SET experience = 180, level = 2 WHERE id = 8;
UPDATE user SET experience = 180, level = 2 WHERE id = 9;
UPDATE user SET experience = 180, level = 2 WHERE id = 10;

-- 9. 插入排行榜缓存数据
INSERT INTO ranking_cache (ranking_type, user_id, rank_no, score, update_time) VALUES
('experience', 5, 1, 380.00, NOW()),
('experience', 1, 2, 280.00, NOW()),
('experience', 2, 3, 220.00, NOW()),
('experience', 6, 4, 200.00, NOW()),
('experience', 7, 5, 180.00, NOW()),
('experience', 8, 6, 180.00, NOW()),
('experience', 9, 7, 180.00, NOW()),
('experience', 10, 8, 180.00, NOW()),
('experience', 3, 9, 150.00, NOW()),
('experience', 4, 10, 120.00, NOW());

-- 10. 插入标签统计数据
INSERT INTO tag_statistics (tag_name, category, total_count, user_count, stat_date, create_time, update_time) VALUES
('Java', 'tech', 156, 45, CURDATE(), NOW(), NOW()),
('Spring Boot', 'tech', 134, 38, CURDATE(), NOW(), NOW()),
('前端', 'tech', 189, 52, CURDATE(), NOW(), NOW()),
('Vue', 'tech', 167, 43, CURDATE(), NOW(), NOW()),
('Python', 'tech', 145, 41, CURDATE(), NOW(), NOW()),
('数据分析', 'tech', 98, 28, CURDATE(), NOW(), NOW()),
('Linux', 'tech', 123, 35, CURDATE(), NOW(), NOW()),
('Git', 'tech', 178, 48, CURDATE(), NOW(), NOW()),
('游戏', 'life', 234, 67, CURDATE(), NOW(), NOW()),
('健身', 'life', 156, 42, CURDATE(), NOW(), NOW()),
('面试', 'work', 198, 56, CURDATE(), NOW(), NOW()),
('摄影', 'life', 89, 25, CURDATE(), NOW(), NOW());

-- 11. 插入用户活跃度统计数据
INSERT INTO user_activity_stats (stat_date, active_users, new_users, login_count, post_count, comment_count, create_time, update_time) VALUES
(CURDATE(), 10, 2, 45, 10, 10, NOW(), NOW()),
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 8, 1, 38, 8, 7, NOW(), NOW()),
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 6, 0, 32, 5, 5, NOW(), NOW()),
(DATE_SUB(CURDATE(), INTERVAL 3 DAY), 7, 1, 35, 6, 6, NOW(), NOW()),
(DATE_SUB(CURDATE(), INTERVAL 4 DAY), 5, 0, 28, 4, 4, NOW(), NOW()),
(DATE_SUB(CURDATE(), INTERVAL 5 DAY), 9, 2, 42, 9, 8, NOW(), NOW()),
(DATE_SUB(CURDATE(), INTERVAL 6 DAY), 7, 1, 36, 7, 6, NOW(), NOW());

-- ============================================
-- 验证数据插入
-- ============================================
SELECT '✅ 论坛帖子数量:' as info, COUNT(*) as count FROM post WHERE is_delete = 0;
SELECT '✅ 帖子评论数量:' as info, COUNT(*) as count FROM post_comment WHERE is_delete = 0;
SELECT '✅ 帖子点赞数量:' as info, COUNT(*) as count FROM post_like;
SELECT '✅ 攻略数量:' as info, COUNT(*) as count FROM strategy WHERE is_delete = 0;
SELECT '✅ 攻略收藏数量:' as info, COUNT(*) as count FROM strategy_collect;
SELECT '✅ 用户标签数量:' as info, COUNT(*) as count FROM user_tag WHERE is_delete = 0;
SELECT '✅ 经验记录数量:' as info, COUNT(*) as count FROM experience_log;
SELECT '✅ 排行榜数据:' as info, COUNT(*) as count FROM ranking_cache;
SELECT '✅ 标签统计数量:' as info, COUNT(*) as count FROM tag_statistics;
SELECT '✅ 活跃度统计:' as info, COUNT(*) as count FROM user_activity_stats;
