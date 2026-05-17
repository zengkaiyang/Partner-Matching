# API接口完整列表

## 📋 目录
- [论坛模块](#论坛模块)
- [攻略模块](#攻略模块)
- [排行榜模块](#排行榜模块)
- [待开发模块](#待开发模块)

---

## 论坛模块

**基础路径**: `/api/forum`

### 1. 帖子管理

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| POST | `/post/add` | 发布帖子 | body: ForumPost, query: tags(List) |
| POST | `/post/delete` | 删除帖子 | postId, userId |
| POST | `/post/update` | 更新帖子 | body: ForumPost, query: tags(可选) |
| GET | `/post/detail` | 帖子详情 | postId |
| GET | `/post/list` | 帖子列表 | pageNum, pageSize, category(可选), sortBy |
| GET | `/post/search` | 搜索帖子 | keyword, pageNum, pageSize |

### 2. 互动功能

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| POST | `/post/like` | 点赞帖子 | postId, userId |
| POST | `/post/unlike` | 取消点赞 | postId, userId |
| POST | `/post/favorite` | 收藏帖子 | postId, userId |
| POST | `/post/unfavorite` | 取消收藏 | postId, userId |

### 3. 统计数据

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/post/hot` | 热门帖子 | limit(默认10) |
| GET | `/tags/hot` | 热门标签 | limit(默认20) |

### 4. 用户相关

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/post/user` | 用户发布的帖子 | userId, pageNum, pageSize |
| GET | `/post/favorites` | 用户收藏的帖子 | userId, pageNum, pageSize |

---

## 攻略模块

**基础路径**: `/api/strategy`

### 1. 攻略管理

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| POST | `/add` | 发布攻略 | body: Strategy, query: tags(List) |
| POST | `/delete` | 删除攻略 | strategyId, userId |
| POST | `/update` | 更新攻略 | body: Strategy, query: tags(可选) |
| GET | `/detail` | 攻略详情 | strategyId |
| GET | `/list` | 攻略列表 | pageNum, pageSize, category, type, sortBy |
| GET | `/search` | 搜索攻略 | keyword, pageNum, pageSize |

### 2. 互动功能

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| POST | `/like` | 点赞攻略 | strategyId, userId |
| POST | `/unlike` | 取消点赞 | strategyId, userId |
| POST | `/favorite` | 收藏攻略 | strategyId, userId |
| POST | `/unfavorite` | 取消收藏 | strategyId, userId |

### 3. 统计数据

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/hot` | 热门攻略 | limit(默认10) |

### 4. 用户相关

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/user` | 用户发布的攻略 | userId, pageNum, pageSize |
| GET | `/favorites` | 用户收藏的攻略 | userId, pageNum, pageSize |

---

## 排行榜模块

**基础路径**: `/api/ranking`

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/strategy` | 热门攻略排行榜 | limit(默认10) |
| GET | `/forum` | 热门论坛排行榜 | limit(默认10) |
| GET | `/authors` | 优质作者排行榜 | limit(默认10), period(all/30days) |

---

## 待开发模块

### 个人信息模块（待开发）

**建议路径**: `/api/profile`

| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| GET | `/info` | 获取个人信息 | ⏳ |
| POST | `/update` | 更新个人信息 | ⏳ |
| GET | `/posts` | 我的帖子 | ⏳ |
| GET | `/strategies` | 我的攻略 | ⏳ |
| GET | `/history` | 浏览历史 | ⏳ |
| GET | `/favorites` | 我的收藏 | ⏳ |
| POST | `/follow` | 关注用户 | ⏳ |
| POST | `/unfollow` | 取消关注 | ⏳ |
| GET | `/followers` | 粉丝列表 | ⏳ |
| GET | `/following` | 关注列表 | ⏳ |

### 数据可视化模块（待开发）

**建议路径**: `/api/data`

| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| GET | `/tag-stats` | 标签统计数据 | ⏳ |
| GET | `/city-distribution` | 城市分布数据 | ⏳ |
| GET | `/activity-trend` | 活跃度趋势 | ⏳ |
| GET | `/level-distribution` | 等级分布 | ⏳ |

### 后台管理模块（待开发）

**建议路径**: `/api/admin`

| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| GET | `/users` | 用户列表 | ⏳ |
| POST | `/user/update` | 更新用户 | ⏳ |
| POST | `/user/disable` | 禁用用户 | ⏳ |
| POST | `/user/enable` | 启用用户 | ⏳ |
| POST | `/user/delete` | 删除用户 | ⏳ |
| GET | `/settings` | 系统设置 | ⏳ |
| POST | `/settings/update` | 更新系统设置 | ⏳ |

### 队伍管理增强（待开发）

在现有 `/api/team` 基础上添加：

| 方法 | 路径 | 说明 | 状态 |
|------|------|------|------|
| POST | `/dismiss` | 解散队伍 | ⏳ |
| POST | `/kick` | 踢出队员 | ⏳ |

---

## 📊 经验计算规则

所有涉及经验的操作会自动触发以下积分规则：

| 行为 | 积分 | 受益人 |
|------|------|--------|
| 发帖 | +3 | 作者 |
| 评论 | +5 | 评论者 |
| 转发 | +10 | 转发者 |
| 获得点赞 | +3 | 作者 |

---

## 🔐 权限说明

### 公开接口（无需登录）
- `/api/forum/post/list` - 帖子列表
- `/api/forum/post/detail` - 帖子详情
- `/api/strategy/list` - 攻略列表
- `/api/strategy/detail` - 攻略详情
- `/api/ranking/*` - 所有排行榜

### 需要登录的接口
- 所有POST操作（发布、点赞、收藏等）
- 用户个人相关接口

### 需要管理员权限
- `/api/admin/*` - 所有后台管理接口

---

## 📝 请求示例

### 发布帖子
```bash
curl -X POST "http://localhost:8080/api/forum/post/add?tags=Java&tags=后端" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Java学习路线",
    "content": "详细内容...",
    "summary": "摘要...",
    "authorId": 1,
    "category": "tech"
  }'
```

### 获取帖子列表
```bash
curl -X GET "http://localhost:8080/api/forum/post/list?pageNum=1&pageSize=10&sortBy=hot"
```

### 点赞帖子
```bash
curl -X POST "http://localhost:8080/api/forum/post/like?postId=1&userId=2"
```

### 获取优质作者排行榜
```bash
curl -X GET "http://localhost:8080/api/ranking/authors?limit=10&period=all"
```

---

## 🎯 响应格式

所有接口统一返回格式：

```json
{
  "code": 0,
  "message": "success",
  "data": { ... }
}
```

**常见code**:
- `0`: 成功
- `400`: 请求参数错误
- `401`: 未登录
- `403`: 无权限
- `500`: 服务器内部错误

---

**最后更新**: 2026-05-18  
**已实现接口**: 29个  
**待开发接口**: 约30个
