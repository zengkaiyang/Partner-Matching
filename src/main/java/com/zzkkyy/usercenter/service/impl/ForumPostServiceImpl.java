package com.zzkkyy.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzkkyy.usercenter.mapper.*;
import com.zzkkyy.usercenter.model.domain.*;
import com.zzkkyy.usercenter.service.ForumPostService;
import com.zzkkyy.usercenter.service.UserExperienceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 论坛帖子服务实现
 */
@Service
@Slf4j
public class ForumPostServiceImpl implements ForumPostService {

    @Resource
    private ForumPostMapper forumPostMapper;

    @Resource
    private PostTagMapper postTagMapper;

    @Resource
    private PostLikeMapper postLikeMapper;

    @Resource
    private PostFavoriteMapper postFavoriteMapper;

    @Resource
    private UserExperienceService userExperienceService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ForumCommentMapper forumCommentMapper;

    @Override
    @Transactional
    public long addPost(ForumPost post, List<String> tags) {
        // 设置默认值
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setShareCount(0);
        post.setStatus(0);
        post.setCreateTime(new Date());
        post.setUpdateTime(new Date());

        // 插入帖子
        forumPostMapper.insert(post);
        long postId = post.getId();

        // 插入标签关联
        if (tags != null && !tags.isEmpty()) {
            for (String tagName : tags) {
                PostTag postTag = new PostTag();
                postTag.setPostId(postId);
                postTag.setTagName(tagName);
                postTag.setCreateTime(new Date());
                postTagMapper.insert(postTag);
            }
        }

        // 增加用户经验（发帖+3）
        userExperienceService.addExperience(post.getAuthorId(), "post", postId, 3);

        log.info("发布帖子成功，postId: {}, authorId: {}", postId, post.getAuthorId());
        return postId;
    }

    @Override
    @Transactional
    public boolean deletePost(long postId, long userId) {
        ForumPost post = forumPostMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }
        if (!post.getAuthorId().equals(userId)) {
            throw new RuntimeException("无权删除该帖子");
        }

        // 逻辑删除
        post.setIsDelete(1);
        post.setUpdateTime(new Date());
        forumPostMapper.updateById(post);

        log.info("删除帖子成功，postId: {}", postId);
        return true;
    }

    @Override
    @Transactional
    public boolean updatePost(ForumPost post, List<String> tags) {
        ForumPost existingPost = forumPostMapper.selectById(post.getId());
        if (existingPost == null) {
            throw new RuntimeException("帖子不存在");
        }

        post.setUpdateTime(new Date());
        forumPostMapper.updateById(post);

        // 更新标签：先删除旧标签，再插入新标签
        QueryWrapper<PostTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", post.getId());
        postTagMapper.delete(queryWrapper);

        if (tags != null && !tags.isEmpty()) {
            for (String tagName : tags) {
                PostTag postTag = new PostTag();
                postTag.setPostId(post.getId());
                postTag.setTagName(tagName);
                postTag.setCreateTime(new Date());
                postTagMapper.insert(postTag);
            }
        }

        log.info("更新帖子成功，postId: {}", post.getId());
        return true;
    }

    @Override
    public ForumPost getPostById(long postId) {
        return forumPostMapper.selectById(postId);
    }

    @Override
    public Page<ForumPost> listPosts(int pageNum, int pageSize, String category, String sortBy, String tag) {
        Page<ForumPost> page = new Page<>(pageNum, pageSize);
        
        // 如果指定了标签，先查询包含该标签的帖子ID
        if (tag != null && !tag.isEmpty()) {
            QueryWrapper<PostTag> tagQuery = new QueryWrapper<>();
            tagQuery.eq("tag_name", tag);
            List<PostTag> postTags = postTagMapper.selectList(tagQuery);
            List<Long> postIds = postTags.stream()
                    .map(PostTag::getPostId)
                    .collect(Collectors.toList());
            
            if (postIds.isEmpty()) {
                return page; // 没有包含该标签的帖子
            }
            
            QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
            // ForumPost实体有@TableLogic注解，自动处理逻辑删除
            queryWrapper.in("id", postIds);
            
            // 排序
            if ("hot".equals(sortBy)) {
                queryWrapper.orderByDesc("like_count", "view_count");
            } else {
                queryWrapper.orderByDesc("create_time");
            }
            
            return forumPostMapper.selectPage(page, queryWrapper);
        }
        
        // 没有指定标签，按原有逻辑查询
        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
        // ForumPost实体有@TableLogic注解，自动处理逻辑删除

        // 按分类筛选
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }

        // 排序
        if ("hot".equals(sortBy)) {
            queryWrapper.orderByDesc("like_count", "view_count");
        } else {
            queryWrapper.orderByDesc("create_time");
        }

        return forumPostMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Page<ForumPost> searchPosts(String keyword, int pageNum, int pageSize) {
        Page<ForumPost> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
        // ForumPost实体有@TableLogic注解，自动处理逻辑删除
        queryWrapper.and(wrapper -> wrapper.like("title", keyword)
                .or()
                .like("content", keyword));
        queryWrapper.orderByDesc("create_time");

        return forumPostMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean likePost(long postId, long userId) {
        // 检查是否已点赞
        QueryWrapper<PostLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId).eq("user_id", userId);
        Long count = postLikeMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("已经点赞过");
        }

        // 添加点赞记录
        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLike.setCreateTime(new Date());
        postLikeMapper.insert(postLike);

        // 更新帖子点赞数
        ForumPost post = forumPostMapper.selectById(postId);
        post.setLikeCount(post.getLikeCount() + 1);
        forumPostMapper.updateById(post);

        // 增加作者经验（获得点赞+3）
        userExperienceService.addExperience(post.getAuthorId(), "like_received", postId, 3);

        log.info("点赞帖子成功，postId: {}, userId: {}", postId, userId);
        return true;
    }

    @Override
    @Transactional
    public boolean unlikePost(long postId, long userId) {
        QueryWrapper<PostLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId).eq("user_id", userId);
        postLikeMapper.delete(queryWrapper);

        // 更新帖子点赞数
        ForumPost post = forumPostMapper.selectById(postId);
        if (post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
            forumPostMapper.updateById(post);
        }

        log.info("取消点赞成功，postId: {}, userId: {}", postId, userId);
        return true;
    }

    @Override
    @Transactional
    public boolean favoritePost(long postId, long userId) {
        // 检查是否已收藏
        QueryWrapper<PostFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId).eq("user_id", userId);
        Long count = postFavoriteMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("已经收藏过");
        }

        // 添加收藏记录
        PostFavorite postFavorite = new PostFavorite();
        postFavorite.setPostId(postId);
        postFavorite.setUserId(userId);
        postFavorite.setCreateTime(new Date());
        postFavoriteMapper.insert(postFavorite);

        // 更新帖子收藏数
        ForumPost post = forumPostMapper.selectById(postId);
        post.setFavoriteCount(post.getFavoriteCount() + 1);
        forumPostMapper.updateById(post);

        log.info("收藏帖子成功，postId: {}, userId: {}", postId, userId);
        return true;
    }

    @Override
    @Transactional
    public boolean unfavoritePost(long postId, long userId) {
        QueryWrapper<PostFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId).eq("user_id", userId);
        postFavoriteMapper.delete(queryWrapper);

        // 更新帖子收藏数
        ForumPost post = forumPostMapper.selectById(postId);
        if (post.getFavoriteCount() > 0) {
            post.setFavoriteCount(post.getFavoriteCount() - 1);
            forumPostMapper.updateById(post);
        }

        log.info("取消收藏成功，postId: {}, userId: {}", postId, userId);
        return true;
    }

    @Override
    public void incrementViewCount(long postId) {
        ForumPost post = forumPostMapper.selectById(postId);
        if (post != null) {
            post.setViewCount(post.getViewCount() + 1);
            forumPostMapper.updateById(post);
        }
    }

    @Override
    @Transactional
    public void incrementShareCount(long postId) {
        ForumPost post = forumPostMapper.selectById(postId);
        if (post != null) {
            post.setShareCount(post.getShareCount() + 1);
            forumPostMapper.updateById(post);

            // 增加转发者经验（转发+10）
            // 这里需要从请求中获取当前用户ID，暂时省略
        }
    }

    @Override
    public Page<ForumPost> getUserPosts(long userId, int pageNum, int pageSize) {
        Page<ForumPost> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
        // ForumPost实体有@TableLogic注解，自动处理逻辑删除
        queryWrapper.eq("author_id", userId)
                .orderByDesc("create_time");

        return forumPostMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Page<ForumPost> getUserFavorites(long userId, int pageNum, int pageSize) {
        // 先查询用户收藏的帖子ID列表
        QueryWrapper<PostFavorite> favQuery = new QueryWrapper<>();
        favQuery.eq("user_id", userId).orderByDesc("create_time");
        List<PostFavorite> favorites = postFavoriteMapper.selectList(favQuery);
        List<Long> postIds = favorites.stream()
                .map(PostFavorite::getPostId)
                .collect(Collectors.toList());

        // 分页查询帖子详情
        Page<ForumPost> page = new Page<>(pageNum, pageSize);
        if (postIds.isEmpty()) {
            return page;
        }

        QueryWrapper<ForumPost> postQuery = new QueryWrapper<>();
        // ForumPost实体有@TableLogic注解，自动处理逻辑删除
        postQuery.in("id", postIds)
                .orderByDesc("create_time");

        return forumPostMapper.selectPage(page, postQuery);
    }

    @Override
    public Page<ForumPost> getUserLikedPosts(long userId, int pageNum, int pageSize) {
        // 先查询用户点赞的帖子ID列表
        QueryWrapper<PostLike> likeQuery = new QueryWrapper<>();
        likeQuery.eq("user_id", userId).orderByDesc("create_time");
        List<PostLike> likes = postLikeMapper.selectList(likeQuery);
        List<Long> postIds = likes.stream()
                .map(PostLike::getPostId)
                .collect(Collectors.toList());

        // 分页查询帖子详情
        Page<ForumPost> page = new Page<>(pageNum, pageSize);
        if (postIds.isEmpty()) {
            return page;
        }

        QueryWrapper<ForumPost> postQuery = new QueryWrapper<>();
        // ForumPost实体有@TableLogic注解，自动处理逻辑删除
        postQuery.in("id", postIds)
                .orderByDesc("create_time");

        return forumPostMapper.selectPage(page, postQuery);
    }

    @Override
    public List<ForumPost> getHotPosts(int limit) {
        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
        // ForumPost实体有@TableLogic注解，自动处理逻辑删除
        // 按热度排序：浏览量×0.3 + 点赞数×2 + 评论数×1.5
        queryWrapper.last("ORDER BY (view_count * 0.3 + like_count * 2 + comment_count * 1.5) DESC LIMIT " + limit);
        
        return forumPostMapper.selectList(queryWrapper);
    }
    
    @Override
    public double calculateHeatScore(long viewCount, long likeCount, long commentCount) {
        return viewCount * 0.3 + likeCount * 2 + commentCount * 1.5;
    }

    @Override
    public List<Map<String, Object>> getHotTags(int limit) {
        // 统计每个标签的使用次数
        QueryWrapper<PostTag> queryWrapper = new QueryWrapper<>();
        List<PostTag> allTags = postTagMapper.selectList(queryWrapper);

        // 按标签名分组统计
        Map<String, Long> tagCountMap = allTags.stream()
                .collect(Collectors.groupingBy(
                        PostTag::getTagName,
                        Collectors.counting()
                ));

        // 转换为列表并排序
        List<Map<String, Object>> hotTags = tagCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> tag = new HashMap<>();
                    tag.put("name", entry.getKey());
                    tag.put("count", entry.getValue());
                    return tag;
                })
                .collect(Collectors.toList());

        return hotTags;
    }

    @Override
    public Map<String, Object> getPostDetail(long postId, Long userId) {
        ForumPost post = forumPostMapper.selectById(postId);
        if (post == null || post.getIsDelete() == 1) {
            throw new RuntimeException("帖子不存在");
        }

        Map<String, Object> detail = new HashMap<>();
        detail.put("id", post.getId());
        detail.put("title", post.getTitle());
        detail.put("content", post.getContent());
        detail.put("summary", post.getSummary());
        detail.put("category", post.getCategory());
        detail.put("viewCount", post.getViewCount());
        detail.put("likeCount", post.getLikeCount());
        detail.put("commentCount", post.getCommentCount());
        detail.put("favoriteCount", post.getFavoriteCount());
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        detail.put("createTime", sdf.format(post.getCreateTime()));

        // 查询作者信息
        if (post.getAuthorId() != null) {
            User author = userMapper.selectById(post.getAuthorId());
            if (author != null) {
                Map<String, Object> authorInfo = new HashMap<>();
                authorInfo.put("id", author.getId());
                authorInfo.put("name", author.getUsername());
                authorInfo.put("avatar", author.getAvatarUrl());
                detail.put("author", authorInfo);
            }
        }

        // 查询标签
        QueryWrapper<PostTag> tagQuery = new QueryWrapper<>();
        tagQuery.eq("post_id", postId);
        List<PostTag> tags = postTagMapper.selectList(tagQuery);
        detail.put("tags", tags.stream().map(PostTag::getTagName).collect(Collectors.toList()));

        // 查询用户点赞/收藏状态
        if (userId != null) {
            QueryWrapper<PostLike> likeQuery = new QueryWrapper<>();
            likeQuery.eq("post_id", postId).eq("user_id", userId);
            detail.put("isLiked", postLikeMapper.selectCount(likeQuery) > 0);

            QueryWrapper<PostFavorite> favoriteQuery = new QueryWrapper<>();
            favoriteQuery.eq("post_id", postId).eq("user_id", userId);
            detail.put("isFavorited", postFavoriteMapper.selectCount(favoriteQuery) > 0);
        } else {
            detail.put("isLiked", false);
            detail.put("isFavorited", false);
        }

        return detail;
    }

    @Override
    public List<Map<String, Object>> getPostComments(long postId) {
        // 获取帖子的所有评论
        QueryWrapper<ForumComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId)
                .orderByDesc("create_time");
        List<ForumComment> comments = forumCommentMapper.selectList(queryWrapper);

        // 组装评论数据，包含用户信息
        return comments.stream().map(comment -> {
            Map<String, Object> commentMap = new HashMap<>();
            commentMap.put("id", comment.getId());
            commentMap.put("postId", comment.getPostId());
            commentMap.put("parentId", comment.getParentId());
            commentMap.put("content", comment.getContent());
            commentMap.put("likeCount", comment.getLikeCount());
            commentMap.put("createTime", comment.getCreateTime());

            // 查询用户信息
            if (comment.getUserId() != null) {
                User user = userMapper.selectById(comment.getUserId());
                if (user != null) {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", user.getId());
                    userInfo.put("name", user.getUsername());
                    userInfo.put("avatar", user.getAvatarUrl());
                    commentMap.put("user", userInfo);
                }
            }

            // 如果是回复评论，查询被回复的用户信息
            if (comment.getParentId() != null && comment.getParentId() > 0) {
                ForumComment parentComment = forumCommentMapper.selectById(comment.getParentId());
                if (parentComment != null && parentComment.getUserId() != null) {
                    User parentUser = userMapper.selectById(parentComment.getUserId());
                    if (parentUser != null) {
                        commentMap.put("replyToUserName", parentUser.getUsername());
                    }
                }
            }

            return commentMap;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public long addComment(long postId, long userId, String content, Long parentId) {
        // 检查帖子是否存在
        ForumPost post = forumPostMapper.selectById(postId);
        if (post == null || post.getIsDelete() == 1) {
            throw new RuntimeException("帖子不存在");
        }

        // 创建评论
        ForumComment comment = new ForumComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setParentId(parentId != null ? parentId : 0L);
        comment.setContent(content);
        comment.setLikeCount(0);
        comment.setCreateTime(new Date());
        forumCommentMapper.insert(comment);

        // 更新帖子评论数
        post.setCommentCount(post.getCommentCount() + 1);
        forumPostMapper.updateById(post);

        // 增加用户经验（评论+2）
        userExperienceService.addExperience(userId, "comment", postId, 2);

        log.info("发表评论成功，postId: {}, userId: {}, commentId: {}", postId, userId, comment.getId());
        return comment.getId();
    }

    @Override
    @Transactional
    public boolean deleteComment(long commentId, long userId) {
        ForumComment comment = forumCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该评论");
        }

        // 逻辑删除评论
        comment.setIsDelete(1);
        forumCommentMapper.updateById(comment);

        // 更新帖子评论数
        ForumPost post = forumPostMapper.selectById(comment.getPostId());
        if (post != null && post.getCommentCount() > 0) {
            post.setCommentCount(post.getCommentCount() - 1);
            forumPostMapper.updateById(post);
        }

        log.info("删除评论成功，commentId: {}", commentId);
        return true;
    }

    @Override
    public ForumPostMapper getPostMapper() {
        return forumPostMapper;
    }
}
