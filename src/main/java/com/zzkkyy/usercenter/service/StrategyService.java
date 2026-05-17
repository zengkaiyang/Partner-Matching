package com.zzkkyy.usercenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzkkyy.usercenter.model.domain.Strategy;

import java.util.List;

/**
 * 攻略服务
 */
public interface StrategyService {
    
    /**
     * 发布攻略
     */
    long addStrategy(Strategy strategy, List<String> tags);
    
    /**
     * 删除攻略
     */
    boolean deleteStrategy(long strategyId, long userId);
    
    /**
     * 更新攻略
     */
    boolean updateStrategy(Strategy strategy, List<String> tags);
    
    /**
     * 获取攻略详情
     */
    Strategy getStrategyById(long strategyId);
    
    /**
     * 分页查询攻略列表
     */
    Page<Strategy> listStrategies(int pageNum, int pageSize, String category, String type, String sortBy);
    
    /**
     * 搜索攻略
     */
    Page<Strategy> searchStrategies(String keyword, int pageNum, int pageSize);
    
    /**
     * 点赞攻略
     */
    boolean likeStrategy(long strategyId, long userId);
    
    /**
     * 取消点赞
     */
    boolean unlikeStrategy(long strategyId, long userId);
    
    /**
     * 收藏攻略
     */
    boolean favoriteStrategy(long strategyId, long userId);
    
    /**
     * 取消收藏
     */
    boolean unfavoriteStrategy(long strategyId, long userId);
    
    /**
     * 增加浏览量
     */
    void incrementViewCount(long strategyId);
    
    /**
     * 获取用户发布的攻略
     */
    Page<Strategy> getUserStrategies(long userId, int pageNum, int pageSize);
    
    /**
     * 获取用户收藏的攻略
     */
    Page<Strategy> getUserFavorites(long userId, int pageNum, int pageSize);
    
    /**
     * 获取热门攻略排行榜
     */
    List<Strategy> getHotStrategies(int limit);
}
