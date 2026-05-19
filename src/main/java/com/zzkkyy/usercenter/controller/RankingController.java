package com.zzkkyy.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ErrorCode;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.model.domain.Strategy;
import com.zzkkyy.usercenter.model.domain.ForumPost;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.service.AuthorStatsService;
import com.zzkkyy.usercenter.service.StrategyService;
import com.zzkkyy.usercenter.service.ForumPostService;
import com.zzkkyy.usercenter.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 排行榜控制器
 */
@RestController
@RequestMapping("/ranking")
@Tag(name = "排行榜", description = "热门攻略、热门论坛、优质作者排行榜")
@Slf4j
public class RankingController {

    @Resource
    private StrategyService strategyService;

    @Resource
    private ForumPostService forumPostService;

    @Resource
    private UserService userService;
    
    @Resource
    private AuthorStatsService authorStatsService;

    /**
     * 热门攻略排行榜
     */
    @GetMapping("/strategy")
    @Operation(summary = "热门攻略排行榜")
    public BaseResponse<List<Map<String, Object>>> getStrategyRanking(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Strategy> strategies = strategyService.getHotStrategies(limit);
            
            List<Map<String, Object>> result = strategies.stream().map(strategy -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", strategy.getId());
                map.put("title", strategy.getTitle());
                map.put("summary", strategy.getSummary());
                map.put("coverImage", strategy.getCoverImage());
                map.put("viewCount", strategy.getViewCount());
                map.put("likeCount", strategy.getLikeCount());
                map.put("favoriteCount", strategy.getFavoriteCount());
                map.put("heatScore", strategyService.calculateHeatScore(
                        strategy.getViewCount(), 
                        strategy.getLikeCount(), 
                        strategy.getFavoriteCount()
                ));
                
                // 查询作者信息
                if (strategy.getAuthorId() != null) {
                    User author = userService.getUserById(strategy.getAuthorId());
                    if (author != null) {
                        map.put("authorId", author.getId());
                        map.put("authorName", author.getUsername());
                        map.put("authorAvatar", author.getAvatarUrl());
                    }
                }
                
                return map;
            }).collect(Collectors.toList());
            
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("获取攻略排行榜失败", e);
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 热门论坛帖子排行榜
     */
    @GetMapping("/forum")
    @Operation(summary = "热门论坛帖子排行榜")
    public BaseResponse<List<Map<String, Object>>> getForumRanking(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ForumPost> posts = forumPostService.getHotPosts(limit);
            
            List<Map<String, Object>> result = posts.stream().map(post -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", post.getId());
                map.put("title", post.getTitle());
                map.put("content", post.getContent());
                map.put("category", post.getCategory());
                map.put("commentCount", post.getCommentCount());
                map.put("likeCount", post.getLikeCount());
                map.put("viewCount", post.getViewCount());
                map.put("heatScore", forumPostService.calculateHeatScore(
                        post.getViewCount(), 
                        post.getLikeCount(), 
                        post.getCommentCount()
                ));
                
                // 查询作者信息
                if (post.getAuthorId() != null) {
                    User author = userService.getUserById(post.getAuthorId());
                    if (author != null) {
                        map.put("authorId", author.getId());
                        map.put("authorName", author.getUsername());
                        map.put("authorAvatar", author.getAvatarUrl());
                    }
                }
                
                return map;
            }).collect(Collectors.toList());
            
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("获取论坛排行榜失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 优质作者排行榜（按等级排序）
     */
    @GetMapping("/authors")
    @Operation(summary = "优质作者排行榜")
    public BaseResponse<List<Map<String, Object>>> getAuthorRanking(
            HttpServletRequest request,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "all") String period) {
        try {
            // 查询用户列表并按等级排序
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id", "username", "avatarUrl", "level", "bio")
                    .orderByDesc("level")
                    .last("LIMIT " + limit);
            List<User> users = userService.list(queryWrapper);
            
            List<Map<String, Object>> result = users.stream().map(user -> {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", user.getId());
                map.put("username", user.getUsername());
                map.put("avatarUrl", user.getAvatarUrl());
                map.put("experience", user.getLevel());
                map.put("level", user.getLevel());
                map.put("bio", user.getBio());
                
                return map;
            }).collect(Collectors.toList());
            
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("获取作者排行榜失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取作者的统计数据
     */
    @GetMapping("/author-stats")
    @Operation(summary = "获取作者统计数据")
    public BaseResponse<Map<String, Object>> getAuthorStats(
            @RequestParam long userId) {
        try {
            Map<String, Object> stats = authorStatsService.getAuthorStats(userId);
            return ResultUtils.success(stats);
        } catch (Exception e) {
            log.error("获取作者统计失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取数据失败: " + e.getMessage());
        }
    }
}
