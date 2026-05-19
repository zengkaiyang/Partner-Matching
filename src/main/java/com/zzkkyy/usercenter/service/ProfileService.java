package com.zzkkyy.usercenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzkkyy.usercenter.model.domain.BrowseHistory;
import com.zzkkyy.usercenter.model.domain.ForumPost;
import com.zzkkyy.usercenter.model.domain.Strategy;
import com.zzkkyy.usercenter.model.domain.User;

import java.util.List;

/**
 * 个人信息服务
 */
public interface ProfileService {
    
    /**
     * 获取用户完整信息（包含经验、等级等）
     */
    User getUserProfile(long userId);
    
    /**
     * 更新用户信息
     */
    boolean updateUserProfile(User user);
    
    /**
     * 记录浏览历史
     */
    void addBrowseHistory(long userId, String contentType, long contentId);
    
    /**
     * 获取浏览历史
     * @param userId 用户ID
     * @param type 类型：forum-论坛帖子，strategy-攻略
     * @param limit 限制数量
     */
    List<BrowseHistory> getBrowseHistory(long userId, String type, int limit);
    
    /**
     * 关注用户
     */
    boolean followUser(long followerId, long followingId);
    
    /**
     * 取消关注
     */
    boolean unfollowUser(long followerId, long followingId);
    
    /**
     * 获取粉丝列表
     */
    List<User> getFollowers(long userId, int pageNum, int pageSize);
    
    /**
     * 获取关注列表
     */
    List<User> getFollowing(long userId, int pageNum, int pageSize);
    
    /**
     * 检查是否已关注
     */
    boolean isFollowing(long followerId, long followingId);

    /**
     * 获取用户的收藏列表
     * @param userId 用户ID
     * @param type 类型：strategy-攻略，forum-论坛帖子
     * @return 收藏列表
     */
    List<?> getFavorites(long userId, String type);

    /**
     * 取消收藏
     * @param favoriteId 收藏记录ID
     * @return 是否成功
     */
    boolean unfavorite(long favoriteId);

}
