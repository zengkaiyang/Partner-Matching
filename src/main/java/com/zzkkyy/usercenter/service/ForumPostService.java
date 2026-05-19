package com.zzkkyy.usercenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzkkyy.usercenter.model.domain.ForumPost;

import java.util.List;
import java.util.Map;

/**
 * 论坛帖子服务
 */
public interface ForumPostService {
    
    /**
     * 发布帖子
     */
    long addPost(ForumPost post, List<String> tags);
    
    /**
     * 删除帖子
     */
    boolean deletePost(long postId, long userId);
    
    /**
     * 更新帖子
     */
    boolean updatePost(ForumPost post, List<String> tags);
    
    /**
     * 获取帖子详情
     */
    ForumPost getPostById(long postId);
    
    /**
     * 获取帖子详情（包含作者信息、标签、用户点赞/收藏状态）
     */
    Map<String, Object> getPostDetail(long postId, Long userId);
    
    /**
     * 获取帖子的评论列表
     */
    List<Map<String, Object>> getPostComments(long postId);
    
    /**
     * 分页查询帖子列表
     */
    Page<ForumPost> listPosts(int pageNum, int pageSize, String category, String sortBy, String tag);
    
    /**
     * 搜索帖子
     */
    Page<ForumPost> searchPosts(String keyword, int pageNum, int pageSize);
    
    /**
     * 点赞帖子
     */
    boolean likePost(long postId, long userId);
    
    /**
     * 取消点赞
     */
    boolean unlikePost(long postId, long userId);
    
    /**
     * 收藏帖子
     */
    boolean favoritePost(long postId, long userId);
    
    /**
     * 取消收藏
     */
    boolean unfavoritePost(long postId, long userId);
    
    /**
     * 增加浏览量
     */
    void incrementViewCount(long postId);
    
    /**
     * 增加转发数
     */
    void incrementShareCount(long postId);
    
    /**
     * 获取用户发布的帖子
     */
    Page<ForumPost> getUserPosts(long userId, int pageNum, int pageSize);
    
    /**
     * 获取用户收藏的帖子
     */
    Page<ForumPost> getUserFavorites(long userId, int pageNum, int pageSize);
    
    /**
     * 获取用户点赞的帖子
     */
    Page<ForumPost> getUserLikedPosts(long userId, int pageNum, int pageSize);
    
    /**
     * 获取热门帖子排行榜（按热度排序）
     */
    List<ForumPost> getHotPosts(int limit);
    
    /**
     * 计算热度分数
     * 热度 = 浏览量 × 0.3 + 点赞数 × 2 + 评论数 × 1.5
     */
    double calculateHeatScore(long viewCount, long likeCount, long commentCount);
    
    /**
     * 获取热门标签
     */
    List<Map<String, Object>> getHotTags(int limit);
    
    /**
     * 发表评论
     */
    long addComment(long postId, long userId, String content, Long parentId);
    
    /**
     * 删除评论
     */
    boolean deleteComment(long commentId, long userId);
}
