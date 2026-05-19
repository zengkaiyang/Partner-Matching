package com.zzkkyy.usercenter.service;

import java.util.Map;

/**
 * 作者统计服务
 */
public interface AuthorStatsService {
    
    /**
     * 获取作者的统计数据
     * @param userId 用户ID
     * @return 统计信息（帖子数、攻略数、获赞数、收藏数等）
     */
    Map<String, Object> getAuthorStats(long userId);
    
    /**
     * 获取作者发布的帖子数量
     */
    long countUserPosts(long userId);
    
    /**
     * 获取作者发布的攻略数量
     */
    long countUserStrategies(long userId);
    
    /**
     * 获取作者获得的总点赞数（帖子点赞+攻略点赞）
     */
    long countUserTotalLikes(long userId);
    
    /**
     * 获取作者获得的总收藏数（帖子收藏+攻略收藏）
     */
    long countUserTotalFavorites(long userId);
    
    /**
     * 获取用户的排名
     */
    int getUserRankByExperience(long userId);
}
