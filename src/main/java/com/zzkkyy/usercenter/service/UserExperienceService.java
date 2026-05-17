package com.zzkkyy.usercenter.service;

/**
 * 用户经验服务
 */
public interface UserExperienceService {
    
    /**
     * 增加经验
     * @param userId 用户ID
     * @param actionType 行为类型
     * @param targetId 目标ID
     * @param points 积分
     */
    void addExperience(Long userId, String actionType, Long targetId, int points);
    
    /**
     * 获取用户经验
     */
    com.zzkkyy.usercenter.model.domain.UserExperience getUserExperience(Long userId);
    
    /**
     * 根据积分计算等级
     */
    int calculateLevel(int totalPoints);
    
    /**
     * 获取优质作者排行榜
     */
    java.util.List<com.zzkkyy.usercenter.model.domain.UserExperience> getTopAuthors(int limit, String period);
}
