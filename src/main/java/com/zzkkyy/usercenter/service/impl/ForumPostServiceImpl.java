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
    public Page<ForumPost> listPosts(int pageNum, int pageSize, String category, String sortBy) {
        Page<ForumPost> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);

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
        queryWrapper.eq("is_delete", 0)
                .and(wrapper -> wrapper.like("title", keyword)
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

        log.info("收藏帖子成功，postId: {}, userId: {}", postId, userId);
        return true;
    }

    @Override
    @Transactional
    public boolean unfavoritePost(long postId, long userId) {
        QueryWrapper<PostFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId).eq("user_id", userId);
        postFavoriteMapper.delete(queryWrapper);

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
        queryWrapper.eq("is_delete", 0)
                .eq("author_id", userId)
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
        postQuery.in("id", postIds)
                .eq("is_delete", 0)
                .orderByDesc("create_time");

        return forumPostMapper.selectPage(page, postQuery);
    }

    @Override
    public List<ForumPost> getHotPosts(int limit) {
        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0)
                .orderByDesc("like_count", "view_count")
                .last("LIMIT " + limit);

        return forumPostMapper.selectList(queryWrapper);
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
}
