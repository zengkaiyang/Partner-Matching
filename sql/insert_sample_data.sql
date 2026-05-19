-- ============================================
-- 样例数据插入脚本
-- 用于填充论坛、攻略、排行榜等功能的数据
-- 注意：所有字段名都与实际数据库表结构保持一致
-- ============================================

-- 1. 插入论坛帖子数据
INSERT INTO forum_post (title, content, summary, author_id, category, view_count, like_count, comment_count, is_delete, create_time, update_time) VALUES
('Spring Boot实战经验分享', '最近在学习Spring Boot,总结了一些实战经验,包括项目结构搭建、常用注解使用、异常处理等...', '最近在学习Spring Boot,总结了一些实战经验,包括项目结构搭建、常用注解使用、异常处理等', 1, 'tech', 156, 23, 8, 0, NOW(), NOW()),
('如何高效学习编程?', '作为一个编程初学者,经常感到迷茫。今天分享一些我的学习方法,希望能帮助到大家...', '作为一个编程初学者,经常感到迷茫。今天分享一些我的学习方法,希望能帮助到大家', 2, 'share', 234, 45, 12, 0, NOW(), NOW()),
('Vue3 + TypeScript最佳实践', '在使用Vue3和TypeScript开发项目时,遇到了一些坑,这里总结一下最佳实践方案...', '在使用Vue3和TypeScript开发项目时,遇到了一些坑,这里总结一下最佳实践方案', 3, 'tech', 189, 34, 15, 0, NOW(), NOW()),
('寻找前端开发伙伴', '正在做一个个人项目,需要找一位熟悉Vue或React的前端小伙伴一起合作...', '正在做一个个人项目,需要找一位熟悉Vue或React的前端小伙伴一起合作', 4, 'project', 98, 12, 5, 0, NOW(), NOW()),
('MySQL性能优化技巧', '分享几个MySQL查询优化的实用技巧,包括索引使用、查询重写、表结构设计等...', '分享几个MySQL查询优化的实用技巧,包括索引使用、查询重写、表结构设计等', 5, 'tech', 312, 67, 20, 0, NOW(), NOW()),
('Docker容器化部署指南', '详细介绍如何使用Docker进行应用容器化部署,包括镜像构建、容器编排等内容...', '详细介绍如何使用Docker进行应用容器化部署,包括镜像构建、容器编排等内容', 6, 'share', 278, 56, 18, 0, NOW(), NOW()),
('算法面试高频题解析', '整理了最近面试中遇到的算法题,包括链表、树、动态规划等经典题目...', '整理了最近面试中遇到的算法题,包括链表、树、动态规划等经典题目', 7, 'qa', 445, 89, 25, 0, NOW(), NOW()),
('微服务架构设计思考', '在微服务架构设计中,服务拆分、通信方式、数据一致性等都是需要考虑的问题...', '在微服务架构设计中,服务拆分、通信方式、数据一致性等都是需要考虑的问题', 8, 'tech', 367, 72, 22, 0, NOW(), NOW()),
('Git团队协作规范', '制定一套合理的Git工作流对于团队协作非常重要,这里分享我们的实践经验...', '制定一套合理的Git工作流对于团队协作非常重要,这里分享我们的实践经验', 9, 'share', 201, 38, 10, 0, NOW(), NOW()),
('Redis缓存策略详解', '深入探讨Redis的各种应用场景和缓存策略,包括缓存穿透、雪崩、击穿等问题...', '深入探讨Redis的各种应用场景和缓存策略,包括缓存穿透、雪崩、击穿等问题', 10, 'tech', 523, 98, 30, 0, NOW(), NOW());

-- 2. 插入论坛帖子标签关联
INSERT INTO post_tag (post_id, tag_name, create_time) VALUES
(1, 'Spring Boot', NOW()),
(1, 'Java', NOW()),
(2, '学习方法', NOW()),
(2, '编程入门', NOW()),
(3, 'Vue3', NOW()),
(3, 'TypeScript', NOW()),
(4, '前端', NOW()),
(4, '团队合作', NOW()),
(5, 'MySQL', NOW()),
(5, '性能优化', NOW()),
(6, 'Docker', NOW()),
(6, '部署', NOW()),
(7, '算法', NOW()),
(7, '面试', NOW()),
(8, '微服务', NOW()),
(8, '架构设计', NOW()),
(9, 'Git', NOW()),
(9, '团队协作', NOW()),
(10, 'Redis', NOW()),
(10, '缓存', NOW());

-- 3. 插入攻略数据
INSERT INTO strategy (title, content, summary, author_id, category, type, cover_image, view_count, like_count, is_delete, create_time, update_time) VALUES
('Java后端开发学习路线', '从Java基础到高级框架,完整的学习路径规划,适合初学者循序渐进地学习...', '从Java基础到高级框架,完整的学习路径规划,适合初学者循序渐进地学习', 1, 'study', 'manual', 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg', 567, 89, 0, NOW(), NOW()),
('前端工程化实践指南', '介绍现代前端工程化的各个环节,包括模块化、组件化、自动化构建、性能优化等...', '介绍现代前端工程化的各个环节,包括模块化、组件化、自动化构建、性能优化等', 2, 'study', 'manual', 'https://fuss10.elemecdn.com/a/3f/3302e58f9a181d2509f3dc0fa68b0jpeg.jpeg', 423, 67, 0, NOW(), NOW()),
('Python数据分析入门', '从零开始学习Python数据分析,包括NumPy、Pandas、Matplotlib等常用库的使用...', '从零开始学习Python数据分析,包括NumPy、Pandas、Matplotlib等常用库的使用', 3, 'study', 'ai', 'https://fuss10.elemecdn.com/9/bd/eae1c8e47aa0783b1282f838f6ed3jpg.jpeg', 389, 54, 0, NOW(), NOW()),
('Linux常用命令大全', '整理Linux系统中最常用的命令及其用法,包括文件操作、权限管理、进程管理等...', '整理Linux系统中最常用的命令及其用法,包括文件操作、权限管理、进程管理等', 4, 'work', 'manual', 'https://fuss10.elemecdn.com/c/dd/d2688fb6244cf4397bd4075add928jpg.jpeg', 678, 123, 0, NOW(), NOW()),
('Git版本控制完全指南', '详细讲解Git的使用方法,从基础概念到高级技巧,帮助团队更好地进行版本控制...', '详细讲解Git的使用方法,从基础概念到高级技巧,帮助团队更好地进行版本控制', 5, 'work', 'manual', 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg', 512, 98, 0, NOW(), NOW()),
('王者荣耀上分攻略', '分享王者荣耀的排位上分技巧,包括英雄选择、出装思路、团战配合等要点...', '分享王者荣耀的排位上分技巧,包括英雄选择、出装思路、团战配合等要点', 6, 'game', 'ai', 'https://fuss10.elemecdn.com/a/3f/3302e58f9a181d2509f3dc0fa68b0jpeg.jpeg', 834, 156, 0, NOW(), NOW()),
('时间管理方法论', '介绍几种经典的时间管理方法,如番茄工作法、GTD等,帮助你提高工作效率...', '介绍几种经典的时间管理方法,如番茄工作法、GTD等,帮助你提高工作效率', 7, 'life', 'manual', 'https://fuss10.elemecdn.com/9/bd/eae1c8e47aa0783b1282f838f6ed3jpg.jpeg', 445, 76, 0, NOW(), NOW()),
('健身减脂计划制定', '科学制定健身减脂计划,包括饮食安排、训练计划、休息恢复等方面的建议...', '科学制定健身减脂计划,包括饮食安排、训练计划、休息恢复等方面的建议', 8, 'life', 'ai', 'https://fuss10.elemecdn.com/c/dd/d2688fb6244cf4397bd4075add928jpg.jpeg', 567, 92, 0, NOW(), NOW()),
('面试准备完全手册', '从简历制作到面试技巧,全面准备技术面试,提高求职成功率...', '从简历制作到面试技巧,全面准备技术面试,提高求职成功率', 9, 'work', 'manual', 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg', 723, 134, 0, NOW(), NOW()),
('摄影入门教程', '零基础学习摄影,包括构图技巧、光线运用、后期处理等基础知识...', '零基础学习摄影,包括构图技巧、光线运用、后期处理等基础知识', 10, 'life', 'manual', 'https://fuss10.elemecdn.com/a/3f/3302e58f9a181d2509f3dc0fa68b0jpeg.jpeg', 389, 65, 0, NOW(), NOW());

-- 4. 插入攻略标签关联
INSERT INTO strategy_tag (strategy_id, tag_name, create_time) VALUES
(1, 'Java', NOW()),
(1, '后端', NOW()),
(2, '前端', NOW()),
(2, '工程化', NOW()),
(3, 'Python', NOW()),
(3, '数据分析', NOW()),
(4, 'Linux', NOW()),
(4, '运维', NOW()),
(5, 'Git', NOW()),
(5, '版本控制', NOW()),
(6, '王者荣耀', NOW()),
(6, '游戏', NOW()),
(7, '时间管理', NOW()),
(7, '效率', NOW()),
(8, '健身', NOW()),
(8, '减脂', NOW()),
(9, '面试', NOW()),
(9, '求职', NOW()),
(10, '摄影', NOW()),
(10, '教程', NOW());

-- 5. 插入用户经验数据(用于排行榜)
INSERT INTO user_experience (user_id, total_points, level, post_count, comment_count, share_count, like_received, update_time) VALUES
(1, 2580, 5, 15, 45, 12, 234, NOW()),
(2, 3200, 6, 20, 60, 18, 345, NOW()),
(3, 1890, 4, 12, 35, 8, 189, NOW()),
(4, 4100, 7, 25, 80, 22, 456, NOW()),
(5, 5600, 8, 30, 100, 30, 678, NOW()),
(6, 1200, 3, 8, 20, 5, 120, NOW()),
(7, 2900, 5, 18, 50, 15, 289, NOW()),
(8, 3800, 6, 22, 70, 20, 398, NOW()),
(9, 1500, 4, 10, 28, 6, 156, NOW()),
(10, 4500, 7, 28, 90, 25, 567, NOW());

-- 6. 插入点赞记录（增加互动数据）
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
(10, 1, NOW()),
(10, 2, NOW()),
(10, 4, NOW()),
(10, 5, NOW()),
(10, 8, NOW());

-- 7. 插入攻略点赞记录
INSERT INTO strategy_like (strategy_id, user_id, create_time) VALUES
(1, 2, NOW()),
(1, 5, NOW()),
(2, 3, NOW()),
(2, 7, NOW()),
(4, 1, NOW()),
(4, 6, NOW()),
(5, 2, NOW()),
(5, 8, NOW()),
(6, 1, NOW()),
(6, 3, NOW()),
(6, 5, NOW()),
(9, 2, NOW()),
(9, 4, NOW()),
(10, 1, NOW()),
(10, 7, NOW());

-- 8. 插入收藏记录
INSERT INTO post_favorite (post_id, user_id, create_time) VALUES
(1, 3, NOW()),
(2, 5, NOW()),
(5, 2, NOW()),
(10, 1, NOW()),
(10, 4, NOW());

INSERT INTO strategy_favorite (strategy_id, user_id, create_time) VALUES
(1, 4, NOW()),
(2, 6, NOW()),
(4, 2, NOW()),
(5, 8, NOW()),
(6, 1, NOW()),
(9, 3, NOW());

-- 9. 插入浏览历史记录
INSERT INTO browse_history (user_id, content_type, content_id, title, browse_time) VALUES
(1, 'forum', 1, 'Spring Boot实战经验分享', NOW()),
(1, 'strategy', 2, '前端工程化实践指南', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(2, 'forum', 5, 'MySQL性能优化技巧', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(2, 'strategy', 4, 'Linux常用命令大全', DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(3, 'forum', 3, 'Vue3 + TypeScript最佳实践', DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(3, 'strategy', 1, 'Java后端开发学习路线', DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(4, 'forum', 7, '算法面试高频题解析', DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(4, 'strategy', 9, '面试准备完全手册', DATE_SUB(NOW(), INTERVAL 7 HOUR)),
(5, 'forum', 10, 'Redis缓存策略详解', DATE_SUB(NOW(), INTERVAL 8 HOUR)),
(5, 'strategy', 6, '王者荣耀上分攻略', DATE_SUB(NOW(), INTERVAL 9 HOUR));

-- 10. 插入关注关系
INSERT INTO user_follow (follower_id, following_id, create_time) VALUES
(1, 2, NOW()),
(1, 5, NOW()),
(2, 3, NOW()),
(2, 7, NOW()),
(3, 1, NOW()),
(3, 4, NOW()),
(4, 5, NOW()),
(4, 8, NOW()),
(5, 2, NOW()),
(5, 10, NOW()),
(6, 1, NOW()),
(6, 3, NOW()),
(7, 2, NOW()),
(7, 5, NOW()),
(8, 4, NOW()),
(8, 9, NOW()),
(9, 1, NOW()),
(9, 6, NOW()),
(10, 5, NOW()),
(10, 7, NOW());

-- ============================================
-- 验证数据插入
-- ============================================
SELECT '✅ 论坛帖子数量:' as info, COUNT(*) as count FROM forum_post WHERE is_delete = 0;
SELECT '✅ 攻略数量:' as info, COUNT(*) as count FROM strategy WHERE is_delete = 0;
SELECT '✅ 用户经验记录:' as info, COUNT(*) as count FROM user_experience;
SELECT '✅ 点赞记录:' as info, COUNT(*) as count FROM post_like_relation;
SELECT '✅ 收藏记录:' as info, COUNT(*) as count FROM (SELECT * FROM post_favorite UNION ALL SELECT * FROM strategy_favorite) as t;
SELECT '✅ 浏览历史:' as info, COUNT(*) as count FROM browse_history;
SELECT '✅ 关注关系:' as info, COUNT(*) as count FROM user_follow;
