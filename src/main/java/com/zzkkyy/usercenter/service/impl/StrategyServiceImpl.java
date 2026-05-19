package com.zzkkyy.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzkkyy.usercenter.mapper.*;
import com.zzkkyy.usercenter.model.domain.*;
import com.zzkkyy.usercenter.service.StrategyService;
import com.zzkkyy.usercenter.service.UserExperienceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 攻略服务实现
 */
@Service
@Slf4j
public class StrategyServiceImpl implements StrategyService {

    @Resource
    private StrategyMapper strategyMapper;

    @Resource
    private StrategyTagMapper strategyTagMapper;

    @Resource
    private StrategyLikeMapper strategyLikeMapper;

    @Resource
    private StrategyFavoriteMapper strategyFavoriteMapper;

    @Resource
    private UserExperienceService userExperienceService;

    @Override
    @Transactional
    public long addStrategy(Strategy strategy, List<String> tags) {
        // 设置默认值
        strategy.setViewCount(0);
        strategy.setLikeCount(0);
        strategy.setFavoriteCount(0);
        strategy.setStatus(0);
        strategy.setCreateTime(new Date());
        strategy.setUpdateTime(new Date());

        // 插入攻略
        strategyMapper.insert(strategy);
        long strategyId = strategy.getId();

        // 插入标签关联
        if (tags != null && !tags.isEmpty()) {
            for (String tagName : tags) {
                StrategyTag strategyTag = new StrategyTag();
                strategyTag.setStrategyId(strategyId);
                strategyTag.setTagName(tagName);
                strategyTag.setCreateTime(new Date());
                strategyTagMapper.insert(strategyTag);
            }
        }

        // 增加用户经验（发帖+3）
        userExperienceService.addExperience(strategy.getAuthorId(), "post", strategyId, 3);

        log.info("发布攻略成功，strategyId: {}, authorId: {}", strategyId, strategy.getAuthorId());
        return strategyId;
    }

    @Override
    @Transactional
    public boolean deleteStrategy(long strategyId, long userId) {
        Strategy strategy = strategyMapper.selectById(strategyId);
        if (strategy == null) {
            throw new RuntimeException("攻略不存在");
        }
        if (!strategy.getAuthorId().equals(userId)) {
            throw new RuntimeException("无权删除该攻略");
        }

        // 逻辑删除
        strategy.setIsDelete(1);
        strategy.setUpdateTime(new Date());
        strategyMapper.updateById(strategy);

        log.info("删除攻略成功，strategyId: {}", strategyId);
        return true;
    }

    @Override
    @Transactional
    public boolean updateStrategy(Strategy strategy, List<String> tags) {
        Strategy existingStrategy = strategyMapper.selectById(strategy.getId());
        if (existingStrategy == null) {
            throw new RuntimeException("攻略不存在");
        }

        strategy.setUpdateTime(new Date());
        strategyMapper.updateById(strategy);

        // 更新标签：先删除旧标签，再插入新标签
        QueryWrapper<StrategyTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("strategy_id", strategy.getId());
        strategyTagMapper.delete(queryWrapper);

        if (tags != null && !tags.isEmpty()) {
            for (String tagName : tags) {
                StrategyTag strategyTag = new StrategyTag();
                strategyTag.setStrategyId(strategy.getId());
                strategyTag.setTagName(tagName);
                strategyTag.setCreateTime(new Date());
                strategyTagMapper.insert(strategyTag);
            }
        }

        log.info("更新攻略成功，strategyId: {}", strategy.getId());
        return true;
    }

    @Override
    public Strategy getStrategyById(long strategyId) {
        return strategyMapper.selectById(strategyId);
    }

    @Override
    public Page<Strategy> listStrategies(int pageNum, int pageSize, String category, String type, String sortBy) {
        Page<Strategy> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Strategy> queryWrapper = new QueryWrapper<>();
        // Strategy实体有@TableLogic注解，自动处理逻辑删除

        // 按分类筛选（排除'all'，即显示所有分类）
        log.info("接收到分类参数: {}", category);
        if (category != null && !category.isEmpty()) {
            if ("all".equalsIgnoreCase(category)) {
                log.info("分类为'all'，不过滤，查询所有攻略");
            } else {
                log.info("按分类过滤: category = {}", category);
                queryWrapper.eq("category", category);
            }
        } else {
            log.info("未传递分类参数，查询所有攻略");
        }

        // 按类型筛选
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq("type", type);
        }

        // 排序逻辑优化
        if ("hottest".equals(sortBy)) {
            // 最热：综合浏览量和点赞数（浏览量权重更高）
            queryWrapper.orderByDesc("view_count + like_count");
        } else if ("likes".equals(sortBy)) {
            // 点赞最多：只按点赞数排序
            queryWrapper.orderByDesc("like_count");
        } else {
            // 最新：默认按创建时间排序
            queryWrapper.orderByDesc("create_time");
        }

        return strategyMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Page<Strategy> searchStrategies(String keyword, int pageNum, int pageSize) {
        Page<Strategy> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Strategy> queryWrapper = new QueryWrapper<>();
        // Strategy实体有@TableLogic注解，自动处理逻辑删除
        queryWrapper.and(wrapper -> wrapper.like("title", keyword)
                .or()
                .like("content", keyword));
        queryWrapper.orderByDesc("create_time");

        return strategyMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional
    public boolean likeStrategy(long strategyId, long userId) {
        // 检查是否已点赞
        QueryWrapper<StrategyLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("strategy_id", strategyId).eq("user_id", userId);
        Long count = strategyLikeMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("已经点赞过");
        }

        // 添加点赞记录
        StrategyLike strategyLike = new StrategyLike();
        strategyLike.setStrategyId(strategyId);
        strategyLike.setUserId(userId);
        strategyLike.setCreateTime(new Date());
        strategyLikeMapper.insert(strategyLike);

        // 更新攻略点赞数
        Strategy strategy = strategyMapper.selectById(strategyId);
        strategy.setLikeCount(strategy.getLikeCount() + 1);
        strategyMapper.updateById(strategy);

        // 增加作者经验（获得点赞+3）
        userExperienceService.addExperience(strategy.getAuthorId(), "like_received", strategyId, 3);

        log.info("点赞攻略成功，strategyId: {}, userId: {}", strategyId, userId);
        return true;
    }

    @Override
    @Transactional
    public boolean unlikeStrategy(long strategyId, long userId) {
        QueryWrapper<StrategyLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("strategy_id", strategyId).eq("user_id", userId);
        strategyLikeMapper.delete(queryWrapper);

        // 更新攻略点赞数
        Strategy strategy = strategyMapper.selectById(strategyId);
        if (strategy.getLikeCount() > 0) {
            strategy.setLikeCount(strategy.getLikeCount() - 1);
            strategyMapper.updateById(strategy);
        }

        log.info("取消点赞成功，strategyId: {}, userId: {}", strategyId, userId);
        return true;
    }

    @Override
    @Transactional
    public boolean favoriteStrategy(long strategyId, long userId) {
        // 检查是否已收藏
        QueryWrapper<StrategyFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("strategy_id", strategyId).eq("user_id", userId);
        Long count = strategyFavoriteMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("已经收藏过");
        }

        // 添加收藏记录
        StrategyFavorite strategyFavorite = new StrategyFavorite();
        strategyFavorite.setStrategyId(strategyId);
        strategyFavorite.setUserId(userId);
        strategyFavorite.setCreateTime(new Date());
        strategyFavoriteMapper.insert(strategyFavorite);

        // 更新收藏数
        Strategy strategy = strategyMapper.selectById(strategyId);
        strategy.setFavoriteCount(strategy.getFavoriteCount() + 1);
        strategyMapper.updateById(strategy);

        log.info("收藏攻略成功，strategyId: {}, userId: {}", strategyId, userId);
        return true;
    }

    @Override
    @Transactional
    public boolean unfavoriteStrategy(long strategyId, long userId) {
        QueryWrapper<StrategyFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("strategy_id", strategyId).eq("user_id", userId);
        strategyFavoriteMapper.delete(queryWrapper);

        // 更新收藏数
        Strategy strategy = strategyMapper.selectById(strategyId);
        if (strategy.getFavoriteCount() > 0) {
            strategy.setFavoriteCount(strategy.getFavoriteCount() - 1);
            strategyMapper.updateById(strategy);
        }

        log.info("取消收藏成功，strategyId: {}, userId: {}", strategyId, userId);
        return true;
    }

    @Override
    public void incrementViewCount(long strategyId) {
        Strategy strategy = strategyMapper.selectById(strategyId);
        if (strategy != null) {
            strategy.setViewCount(strategy.getViewCount() + 1);
            strategyMapper.updateById(strategy);
        }
    }

    @Override
    public Page<Strategy> getUserStrategies(long userId, int pageNum, int pageSize) {
        Page<Strategy> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Strategy> queryWrapper = new QueryWrapper<>();
        // Strategy实体有@TableLogic注解，自动处理逻辑删除
        queryWrapper.eq("author_id", userId)
                .orderByDesc("create_time");

        return strategyMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Page<Strategy> getUserFavorites(long userId, int pageNum, int pageSize) {
        // 先查询用户收藏的攻略ID列表
        QueryWrapper<StrategyFavorite> favQuery = new QueryWrapper<>();
        favQuery.eq("user_id", userId).orderByDesc("create_time");
        List<StrategyFavorite> favorites = strategyFavoriteMapper.selectList(favQuery);
        List<Long> strategyIds = favorites.stream()
                .map(StrategyFavorite::getStrategyId)
                .collect(java.util.stream.Collectors.toList());

        // 分页查询攻略详情
        Page<Strategy> page = new Page<>(pageNum, pageSize);
        if (strategyIds.isEmpty()) {
            return page;
        }

        QueryWrapper<Strategy> strategyQuery = new QueryWrapper<>();
        // Strategy实体有@TableLogic注解，自动处理逻辑删除
        strategyQuery.in("id", strategyIds)
                .orderByDesc("create_time");

        return strategyMapper.selectPage(page, strategyQuery);
    }

    @Override
    public Page<Strategy> getUserLikedStrategies(long userId, int pageNum, int pageSize) {
        // 先查询用户点赞的攻略ID列表
        QueryWrapper<StrategyLike> likeQuery = new QueryWrapper<>();
        likeQuery.eq("user_id", userId).orderByDesc("create_time");
        List<StrategyLike> likes = strategyLikeMapper.selectList(likeQuery);
        List<Long> strategyIds = likes.stream()
                .map(StrategyLike::getStrategyId)
                .collect(java.util.stream.Collectors.toList());

        // 分页查询攻略详情
        Page<Strategy> page = new Page<>(pageNum, pageSize);
        if (strategyIds.isEmpty()) {
            return page;
        }

        QueryWrapper<Strategy> strategyQuery = new QueryWrapper<>();
        // Strategy实体有@TableLogic注解，自动处理逻辑删除
        strategyQuery.in("id", strategyIds)
                .orderByDesc("create_time");

        return strategyMapper.selectPage(page, strategyQuery);
    }

    @Override
    public List<Strategy> getHotStrategies(int limit) {
        QueryWrapper<Strategy> queryWrapper = new QueryWrapper<>();
        // Strategy实体有@TableLogic注解，自动处理逻辑删除
        // 按热度排序：浏览量×0.3 + 点赞数×2 + 收藏数×1.5
        queryWrapper.last("ORDER BY (view_count * 0.3 + like_count * 2 + favorite_count * 1.5) DESC LIMIT " + limit);
        
        return strategyMapper.selectList(queryWrapper);
    }
    
    @Override
    public double calculateHeatScore(long viewCount, long likeCount, long favoriteCount) {
        return viewCount * 0.3 + likeCount * 2 + favoriteCount * 1.5;
    }
}
