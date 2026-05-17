# Partner-Matching 后端完整设计方案

## 📋 目录
- [一、数据库表设计](#一数据库表设计)
- [二、实体类说明](#二实体类说明)
- [三、核心功能模块](#三核心功能模块)
- [四、API接口设计](#四api接口设计)
- [五、业务逻辑说明](#五业务逻辑说明)

---

## 一、数据库表设计

### 1. 论坛模块（5张表）
- `forum_post` - 论坛帖子表
- `forum_comment` - 论坛评论表
- `post_tag` - 帖子标签关联表
- `post_like` - 帖子点赞表
- `post_favorite` - 帖子收藏表

### 2. 攻略模块（4张表）
- `strategy` - 攻略表
- `strategy_tag` - 攻略标签关联表
- `strategy_like` - 攻略点赞表
- `strategy_favorite` - 攻略收藏表

### 3. 用户经验与等级（2张表）
- `user_experience` - 用户经验表
- `user_action_log` - 用户行为记录表

### 4. 其他功能表（4张表）
- `browse_history` - 浏览历史表
- `user_follow` - 用户关注表
- `hive_tag_stats` - Hive标签统计表（MySQL兜底）
- `user_city_distribution` - 用户城市分布表
- `user_activity_stats` - 用户活跃度统计表
- `system_config` - 系统配置表

**SQL文件位置**: `sql/new_tables.sql`

---

## 二、实体类说明

所有实体类位于 `com.zzkkyy.usercenter.model.domain` 包下：

### 论坛相关
- `ForumPost.java` - 论坛帖子
- `ForumComment.java` - 论坛评论
- `PostTag.java` - 帖子标签关联
- `PostLike.java` - 帖子点赞
- `PostFavorite.java` - 帖子收藏

### 攻略相关
- `Strategy.java` - 攻略
- `StrategyTag.java` - 攻略标签关联
- `StrategyLike.java` - 攻略点赞
- `StrategyFavorite.java` - 攻略收藏

### 用户经验相关
- `UserExperience.java` - 用户经验
- `UserActionLog.java` - 用户行为记录

### 其他
- `BrowseHistory.java` - 浏览历史
- `UserFollow.java` - 用户关注
- `HiveTagStats.java` - Hive标签统计
- `UserCityDistribution.java` - 用户城市分布
- `UserActivityStats.java` - 用户活跃度统计
- `SystemConfig.java` - 系统配置

---

## 三、核心功能模块

### 1. 用户认证模块
**功能**:
- 用户登录、注册、忘记密码
- Session管理（使用Redis）
- 权限拦截器（未登录只能访问主页）

**已有代码**: 
- `UserController.java`
- `UserService.java`

### 2. 主页匹配模块
**功能**:
- 热点用户加载（从Redis获取，首次加载6个用户）
- 编辑距离算法匹配（最多60个用户，分页展示）
- 热门标签推荐匹配

**已有代码**:
- `UserController.java` 中的匹配逻辑
- Redis缓存机制

### 3. 队伍管理模块
**功能**:
- 创建队伍、加入队伍、退出队伍
- 解散队伍（仅队长可操作）
- 踢出队员（仅队长可操作）
- 查看队伍详情及队员信息

**已有代码**:
- `TeamController.java`
- `TeamService.java`

**需要新增**:
- 解散队伍接口
- 踢出队员接口

### 4. 论坛模块 ⭐NEW
**功能**:
- 发布帖子（支持分类：招聘队员、技术交流、经验分享、问答求助）
- 帖子列表（支持最新/最热排序）
- 帖子详情（浏览量统计）
- 点赞、评论、转发
- 收藏帖子
- 标签管理
- 搜索帖子

**经验计算**:
- 发帖: +3积分
- 评论: +5积分
- 转发: +10积分

**需要创建**:
- `ForumPostController.java`
- `ForumPostServiceImpl.java`
- `ForumCommentServiceImpl.java`

### 5. 攻略模块 ⭐NEW
**功能**:
- 发布攻略（手写/AI分析两种类型）
- 攻略分类（学习、工作、游戏、生活）
- 攻略列表（支持最新/最热/点赞最多排序）
- 攻略详情
- 点赞、收藏
- 搜索攻略

**需要创建**:
- `StrategyController.java`
- `StrategyServiceImpl.java`

### 6. 排行榜模块 ⭐NEW
**功能**:
- 热门攻略排行榜（按浏览量、点赞数）
- 热门论坛帖子排行榜（按评论数、点赞数、转发数）
- 优质作者排行榜（按积分等级V1-V10）

**经验计算规则**:
- 点赞: +3积分
- 评论: +5积分
- 转发: +10积分
- 等级计算: V1(0-100), V2(101-300), V3(301-600)... V10(10000+)

**需要创建**:
- `RankingController.java`
- `RankingServiceImpl.java`
- `UserExperienceServiceImpl.java`

### 7. 个人信息模块 ⭐NEW
**功能**:
- 查看个人信息
- 编辑个人资料
- 我的帖子
- 我的攻略
- 浏览历史
- 我的收藏（攻略收藏、论坛收藏）
- 关注/粉丝列表

**需要创建**:
- `ProfileController.java`
- `ProfileServiceImpl.java`
- `BrowseHistoryServiceImpl.java`

### 8. 数据可视化模块 ⭐NEW
**功能**:
- 热门标签词云图
- 标签分类分布饼图
- 用户城市分布地图
- 用户活跃度趋势图
- 用户等级分布雷达图

**兜底策略**:
- 优先从Hive表获取数据
- 如果Hive获取失败，从MySQL兜底表获取

**需要创建**:
- `DataVisualizationController.java` (已有部分)
- `HiveDataService.java`
- `HiveDataServiceImpl.java`

### 9. 后台管理模块 ⭐NEW
**功能**:
- 用户管理（查看、编辑、禁用、删除）
- 系统设置（网站配置、邮件配置、安全配置）
- 内容审核（帖子审核、攻略审核）
- 数据统计

**需要创建**:
- `AdminController.java`
- `AdminServiceImpl.java`

---

## 四、API接口设计

### 论坛接口 `/api/forum/*`
```
POST   /api/forum/post/add          - 发布帖子
POST   /api/forum/post/delete       - 删除帖子
POST   /api/forum/post/update       - 更新帖子
GET    /api/forum/post/detail       - 帖子详情
GET    /api/forum/post/list         - 帖子列表
GET    /api/forum/post/search       - 搜索帖子
POST   /api/forum/post/like         - 点赞帖子
POST   /api/forum/post/unlike       - 取消点赞
POST   /api/forum/post/favorite     - 收藏帖子
POST   /api/forum/post/unfavorite   - 取消收藏
GET    /api/forum/post/hot          - 热门帖子
GET    /api/forum/tags/hot          - 热门标签
```

### 攻略接口 `/api/strategy/*`
```
POST   /api/strategy/add            - 发布攻略
POST   /api/strategy/delete         - 删除攻略
POST   /api/strategy/update         - 更新攻略
GET    /api/strategy/detail         - 攻略详情
GET    /api/strategy/list           - 攻略列表
GET    /api/strategy/search         - 搜索攻略
POST   /api/strategy/like           - 点赞攻略
POST   /api/strategy/unlike         - 取消点赞
POST   /api/strategy/favorite       - 收藏攻略
POST   /api/strategy/unfavorite     - 取消收藏
GET    /api/strategy/hot            - 热门攻略
```

### 排行榜接口 `/api/ranking/*`
```
GET    /api/ranking/strategy        - 热门攻略排行榜
GET    /api/ranking/forum           - 热门论坛排行榜
GET    /api/ranking/authors         - 优质作者排行榜
```

### 个人信息接口 `/api/profile/*`
```
GET    /api/profile/info            - 获取个人信息
POST   /api/profile/update          - 更新个人信息
GET    /api/profile/posts           - 我的帖子
GET    /api/profile/strategies      - 我的攻略
GET    /api/profile/history         - 浏览历史
GET    /api/profile/favorites       - 我的收藏
POST   /api/profile/follow          - 关注用户
POST   /api/profile/unfollow        - 取消关注
GET    /api/profile/followers       - 粉丝列表
GET    /api/profile/following       - 关注列表
```

### 数据可视化接口 `/api/data/*`
```
GET    /api/data/tag-stats          - 标签统计数据
GET    /api/data/city-distribution  - 城市分布数据
GET    /api/data/activity-trend     - 活跃度趋势
GET    /api/data/level-distribution - 等级分布
```

### 后台管理接口 `/api/admin/*`
```
GET    /api/admin/users             - 用户列表
POST   /api/admin/user/update       - 更新用户
POST   /api/admin/user/disable      - 禁用用户
POST   /api/admin/user/enable       - 启用用户
POST   /api/admin/user/delete       - 删除用户
GET    /api/admin/settings          - 系统设置
POST   /api/admin/settings/update   - 更新系统设置
```

---

## 五、业务逻辑说明

### 1. 经验计算逻辑
```java
// 用户行为对应的积分
POST_COMMENT = 3;    // 发帖 +3
COMMENT = 5;         // 评论 +5
SHARE = 10;          // 转发 +10
LIKE = 3;            // 点赞 +3

// 等级计算
V1: 0-100
V2: 101-300
V3: 301-600
V4: 601-1000
V5: 1001-1500
V6: 1501-2200
V7: 2201-3100
V8: 3101-4200
V9: 4201-5500
V10: 5501+
```

### 2. 编辑距离算法匹配
复用现有代码中的编辑距离算法，用于用户标签匹配。

### 3. Redis缓存策略
- 热点用户缓存: `hot:users` (List, 最多6个)
- 用户经验缓存: `user:exp:{userId}` (Hash)
- 热门标签缓存: `hot:tags` (ZSet)

### 4. Hive数据同步策略
```java
// 伪代码
try {
    data = hiveService.queryFromHive(sql);
} catch (Exception e) {
    log.warn("Hive查询失败，使用MySQL兜底", e);
    data = mysqlMapper.selectList(queryWrapper);
}
```

### 5. 权限控制
- 未登录用户: 只能访问主页 `/home`
- 已登录用户: 可访问所有页面
- 管理员: 可访问后台管理 `/admin`

使用拦截器实现:
```java
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) {
        User user = userService.getCurrentUser(request);
        if (user == null && !isPublicPath(request)) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}
```

---

## 六、下一步工作

### 已完成 ✅
1. ✅ 数据库表设计 (`sql/new_tables.sql`)
2. ✅ 所有实体类 (18个Domain类)
3. ✅ 所有Mapper接口 (18个Mapper)

### 待完成 📝
1. ⏳ Service接口和实现类
2. ⏳ Controller控制器
3. ⏳ VO和DTO类
4. ⏳ 拦截器和权限控制
5. ⏳ Redis缓存逻辑
6. ⏳ Hive数据同步逻辑
7. ⏳ 单元测试

---

## 七、快速开始

### 1. 执行SQL建表
```bash
mysql -u root -p partner_matching < sql/new_tables.sql
```

### 2. 编译项目
```bash
mvn clean package
```

### 3. 运行项目
```bash
mvn spring-boot:run
```

### 4. 访问Swagger文档
```
http://localhost:8080/api/doc.html
```

---

## 八、技术栈
- **框架**: Spring Boot 3.5.5
- **ORM**: MyBatis-Plus 3.5.14
- **数据库**: MySQL 8.0 + Apache Hive 3.1.3
- **缓存**: Redis + Redisson 3.52.0
- **爬虫**: WebMagic 0.10.0
- **文档**: Knife4j 4.5.0
- **Java版本**: JDK 21

---

**生成时间**: 2026-05-18  
**项目**: Partner-Matching 伙伴匹配系统
