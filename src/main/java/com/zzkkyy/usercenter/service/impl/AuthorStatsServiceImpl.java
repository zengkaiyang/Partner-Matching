package com.zzkkyy.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzkkyy.usercenter.mapper.*;
import com.zzkkyy.usercenter.model.domain.ForumPost;
import com.zzkkyy.usercenter.model.domain.Strategy;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.service.AuthorStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者统计服务实现
 */
@Service
@Slf4j
public class AuthorStatsServiceImpl implements AuthorStatsService {

    @Resource
    private ForumPostMapper forumPostMapper;
    
    @Resource
    private StrategyMapper strategyMapper;
    
    @Resource
    private UserExperienceMapper userExperienceMapper;
    
    @Resource
    private UserMapper userMapper;

    @Override
    public Map<String, Object> getAuthorStats(long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 帖子数量
        stats.put("totalPosts", countUserPosts(userId));
        
        // 攻略数量
        stats.put("totalStrategies", countUserStrategies(userId));
        
        // 总获赞数（帖子+攻略）
        stats.put("totalLikes", countUserTotalLikes(userId));
        
        // 总收藏数（帖子+攻略）
        stats.put("totalFavorites", countUserTotalFavorites(userId));
        
        // 经验值和等级
        User user = userMapper.selectById(userId);
        if (user != null) {
            stats.put("experience", user.getPoints());
            stats.put("level", user.getLevel());
        }
        
        // 排名
        stats.put("rank", getUserRankByExperience(userId));
        
        return stats;
    }

    @Override
    public long countUserPosts(long userId) {
        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("author_id", userId);  // ForumPost使用author_id而不是user_id
        return forumPostMapper.selectCount(queryWrapper);
    }

    @Override
    public long countUserStrategies(long userId) {
        QueryWrapper<Strategy> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("author_id", userId);
        return strategyMapper.selectCount(queryWrapper);
    }

    @Override
    public long countUserTotalLikes(long userId) {
        long likes = 0L;
        
        // 计算帖子的点赞数总和
        QueryWrapper<ForumPost> postQuery = new QueryWrapper<>();
        postQuery.select("SUM(like_count) as total")
                 .eq("author_id", userId);
        List<Map<String, Object>> postResults = forumPostMapper.selectMaps(postQuery);
        if (postResults != null && !postResults.isEmpty() && postResults.get(0) != null) {
            Map<String, Object> postResult = postResults.get(0);
            if (postResult.get("total") != null) {
                likes += ((Number) postResult.get("total")).longValue();
            }
        }
        
        // 计算攻略的点赞数总和
        QueryWrapper<Strategy> strategyQuery = new QueryWrapper<>();
        strategyQuery.select("SUM(like_count) as total")
                    .eq("author_id", userId);
        List<Map<String, Object>> strategyResults = strategyMapper.selectMaps(strategyQuery);
        if (strategyResults != null && !strategyResults.isEmpty() && strategyResults.get(0) != null) {
            Map<String, Object> strategyResult = strategyResults.get(0);
            if (strategyResult.get("total") != null) {
                likes += ((Number) strategyResult.get("total")).longValue();
            }
        }
        
        return likes;
    }

    @Override
    public long countUserTotalFavorites(long userId) {
        long favorites = 0L;
        
        // 计算帖子的收藏数总和
        QueryWrapper<ForumPost> postQuery = new QueryWrapper<>();
        postQuery.select("SUM(favorite_count) as total")
                 .eq("author_id", userId);
        List<Map<String, Object>> postResults = forumPostMapper.selectMaps(postQuery);
        if (postResults != null && !postResults.isEmpty() && postResults.get(0) != null) {
            Map<String, Object> postResult = postResults.get(0);
            if (postResult.get("total") != null) {
                favorites += ((Number) postResult.get("total")).longValue();
            }
        }
        
        // 计算攻略的收藏数总和
        QueryWrapper<Strategy> strategyQuery = new QueryWrapper<>();
        strategyQuery.select("SUM(favorite_count) as total")
                    .eq("author_id", userId);
        List<Map<String, Object>> strategyResults = strategyMapper.selectMaps(strategyQuery);
        if (strategyResults != null && !strategyResults.isEmpty() && strategyResults.get(0) != null) {
            Map<String, Object> strategyResult = strategyResults.get(0);
            if (strategyResult.get("total") != null) {
                favorites += ((Number) strategyResult.get("total")).longValue();
            }
        }
        
        return favorites;
    }

    @Override
    public int getUserRankByExperience(long userId) {
        // 查询所有用户的等级并排序（User实体有@TableLogic注解，自动过滤已删除用户）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "level")
                   .orderByDesc("level");
        List<User> users = userMapper.selectList(queryWrapper);
        
        // 查找当前用户的排名
        for (int i = 0; i < users.size(); i++) {
            long userIdFromDb = users.get(i).getId();
            if (userIdFromDb == userId) {
                return i + 1;
            }
        }
        
        return -1; // 未找到
    }
}
