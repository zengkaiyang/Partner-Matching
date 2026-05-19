package com.zzkkyy.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ErrorCode;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.mapper.UserMapper;
import com.zzkkyy.usercenter.model.domain.Strategy;
import com.zzkkyy.usercenter.model.domain.StrategyLike;
import com.zzkkyy.usercenter.model.domain.StrategyFavorite;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.service.StrategyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzkkyy.usercenter.mapper.StrategyTagMapper;
import com.zzkkyy.usercenter.mapper.StrategyLikeMapper;
import com.zzkkyy.usercenter.mapper.StrategyFavoriteMapper;
import com.zzkkyy.usercenter.model.domain.StrategyTag;

/**
 * 攻略控制器
 */
@RestController
@RequestMapping("/strategy")
@Tag(name = "攻略管理", description = "攻略相关接口")
@Slf4j
public class StrategyController {

    @Resource
    private StrategyService strategyService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private StrategyTagMapper strategyTagMapper;

    @Resource
    private StrategyLikeMapper strategyLikeMapper;

    @Resource
    private StrategyFavoriteMapper strategyFavoriteMapper;

    @PostMapping("/add")
    @Operation(summary = "发布攻略")
    public BaseResponse<Long> addStrategy(
            @RequestBody Strategy strategy, 
            @RequestParam String tags) {
        try {
            // 解析JSON格式的标签
            List<String> tagList = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(tags, new com.fasterxml.jackson.core.type.TypeReference<List<String>>(){});
            long strategyId = strategyService.addStrategy(strategy, tagList);
            return ResultUtils.success(strategyId);
        } catch (Exception e) {
            log.error("发布攻略失败", e);
            return ResultUtils.error(ErrorCode.SAVE_ERROR, "发布失败: " + e.getMessage());
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "删除攻略")
    public BaseResponse<Boolean> deleteStrategy(@RequestParam long strategyId, @RequestParam long userId) {
        boolean result = strategyService.deleteStrategy(strategyId, userId);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    @Operation(summary = "更新攻略")
    public BaseResponse<Boolean> updateStrategy(
            @RequestBody Strategy strategy, 
            @RequestParam(required = false) String tags) {
        try {
            List<String> tagList = null;
            if (tags != null && !tags.isEmpty()) {
                // 解析JSON格式的标签
                tagList = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(tags, new com.fasterxml.jackson.core.type.TypeReference<List<String>>(){});
            }
            boolean result = strategyService.updateStrategy(strategy, tagList);
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("更新攻略失败", e);
            return ResultUtils.error(ErrorCode.SAVE_ERROR, "更新失败: " + e.getMessage());
        }
    }

    @GetMapping("/detail")
    @Operation(summary = "获取攻略详情")
    public BaseResponse<Map<String, Object>> getStrategyDetail(
            @RequestParam long strategyId,
            @RequestParam(required = false) Long userId) {
        strategyService.incrementViewCount(strategyId);
        Strategy strategy = strategyService.getStrategyById(strategyId);
        
        Map<String, Object> detail = new HashMap<>();
        detail.put("id", strategy.getId());
        detail.put("title", strategy.getTitle());
        detail.put("content", strategy.getContent());
        detail.put("summary", strategy.getSummary());
        detail.put("coverImage", strategy.getCoverImage());
        detail.put("category", strategy.getCategory());
        detail.put("type", strategy.getType());
        detail.put("viewCount", strategy.getViewCount());
        detail.put("likeCount", strategy.getLikeCount());
        detail.put("favoriteCount", strategy.getFavoriteCount());
        detail.put("createTime", strategy.getCreateTime());
        
        // 查询作者信息
        if (strategy.getAuthorId() != null) {
            User author = userMapper.selectById(strategy.getAuthorId());
            if (author != null) {
                detail.put("authorName", author.getUsername());
                detail.put("authorAvatar", author.getAvatarUrl());
                detail.put("authorId", author.getId());
            }
        }
        
        // 查询标签
        QueryWrapper<StrategyTag> tagQuery = new QueryWrapper<>();
        tagQuery.eq("strategy_id", strategyId);
        List<StrategyTag> tags = strategyTagMapper.selectList(tagQuery);
        detail.put("tags", tags.stream().map(StrategyTag::getTagName).collect(Collectors.toList()));
        
        // 查询用户点赞/收藏状态
        if (userId != null) {
            QueryWrapper<StrategyLike> likeQuery = new QueryWrapper<>();
            likeQuery.eq("strategy_id", strategyId).eq("user_id", userId);
            detail.put("isLiked", strategyLikeMapper.selectCount(likeQuery) > 0);
            
            QueryWrapper<StrategyFavorite> favoriteQuery = new QueryWrapper<>();
            favoriteQuery.eq("strategy_id", strategyId).eq("user_id", userId);
            detail.put("isFavorited", strategyFavoriteMapper.selectCount(favoriteQuery) > 0);
        } else {
            detail.put("isLiked", false);
            detail.put("isFavorited", false);
        }
        
        return ResultUtils.success(detail);
    }

    @GetMapping("/list")
    @Operation(summary = "攻略列表")
    public BaseResponse<Map<String, Object>> listStrategies(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(required = false) Long userId) {
        Page<Strategy> page = strategyService.listStrategies(pageNum, pageSize, category, type, sortBy);
        
        // 组装完整数据，包含作者信息
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords().stream().map(strategy -> {
            Map<String, Object> strategyMap = new HashMap<>();
            strategyMap.put("id", strategy.getId());
            strategyMap.put("title", strategy.getTitle());
            strategyMap.put("content", strategy.getContent());
            strategyMap.put("summary", strategy.getSummary());
            strategyMap.put("coverImage", strategy.getCoverImage());
            strategyMap.put("category", strategy.getCategory());
            strategyMap.put("type", strategy.getType());
            strategyMap.put("viewCount", strategy.getViewCount());
            strategyMap.put("likeCount", strategy.getLikeCount());
            strategyMap.put("favoriteCount", strategy.getFavoriteCount());
            strategyMap.put("createTime", strategy.getCreateTime());
            strategyMap.put("authorId", strategy.getAuthorId());
            
            // 查询作者信息
            if (strategy.getAuthorId() != null) {
                User author = userMapper.selectById(strategy.getAuthorId());
                if (author != null) {
                    strategyMap.put("authorName", author.getUsername());
                    strategyMap.put("authorAvatar", author.getAvatarUrl());
                }
            }
            
            // 查询标签
            QueryWrapper<StrategyTag> tagQuery = new QueryWrapper<>();
            tagQuery.eq("strategy_id", strategy.getId());
            List<StrategyTag> tags = strategyTagMapper.selectList(tagQuery);
            strategyMap.put("tags", tags.stream().map(StrategyTag::getTagName).collect(Collectors.toList()));
            
            // 查询当前用户的点赞和收藏状态
            if (userId != null) {
                QueryWrapper<StrategyLike> likeQuery = new QueryWrapper<>();
                likeQuery.eq("strategy_id", strategy.getId()).eq("user_id", userId);
                strategyMap.put("isLiked", strategyLikeMapper.selectCount(likeQuery) > 0);
                
                QueryWrapper<StrategyFavorite> favoriteQuery = new QueryWrapper<>();
                favoriteQuery.eq("strategy_id", strategy.getId()).eq("user_id", userId);
                strategyMap.put("isFavorited", strategyFavoriteMapper.selectCount(favoriteQuery) > 0);
            } else {
                strategyMap.put("isLiked", false);
                strategyMap.put("isFavorited", false);
            }
            
            return strategyMap;
        }).toArray());
        result.put("total", page.getTotal());
        result.put("current", page.getCurrent());
        result.put("size", page.getSize());
        
        return ResultUtils.success(result);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索攻略")
    public BaseResponse<Page<Strategy>> searchStrategies(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<Strategy> page = strategyService.searchStrategies(keyword, pageNum, pageSize);
        return ResultUtils.success(page);
    }

    @PostMapping("/like")
    @Operation(summary = "点赞攻略")
    public BaseResponse<Boolean> likeStrategy(@RequestParam long strategyId, @RequestParam long userId) {
        boolean result = strategyService.likeStrategy(strategyId, userId);
        return ResultUtils.success(result);
    }

    @PostMapping("/unlike")
    @Operation(summary = "取消点赞")
    public BaseResponse<Boolean> unlikeStrategy(@RequestParam long strategyId, @RequestParam long userId) {
        boolean result = strategyService.unlikeStrategy(strategyId, userId);
        return ResultUtils.success(result);
    }

    @PostMapping("/favorite")
    @Operation(summary = "收藏攻略")
    public BaseResponse<Boolean> favoriteStrategy(@RequestParam long strategyId, @RequestParam long userId) {
        boolean result = strategyService.favoriteStrategy(strategyId, userId);
        return ResultUtils.success(result);
    }

    @PostMapping("/unfavorite")
    @Operation(summary = "取消收藏")
    public BaseResponse<Boolean> unfavoriteStrategy(@RequestParam long strategyId, @RequestParam long userId) {
        boolean result = strategyService.unfavoriteStrategy(strategyId, userId);
        return ResultUtils.success(result);
    }

    @GetMapping("/hot")
    @Operation(summary = "热门攻略")
    public BaseResponse<List<Strategy>> getHotStrategies(@RequestParam(defaultValue = "10") int limit) {
        List<Strategy> strategies = strategyService.getHotStrategies(limit);
        return ResultUtils.success(strategies);
    }

    @GetMapping("/user")
    @Operation(summary = "获取用户发布的攻略")
    public BaseResponse<Map<String, Object>> getUserStrategies(
            @RequestParam long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            Page<Strategy> page = strategyService.getUserStrategies(userId, pageNum, pageSize);
            
            // 组装完整数据
            List<Map<String, Object>> records = page.getRecords().stream().map(strategy -> {
                Map<String, Object> strategyMap = new HashMap<>();
                strategyMap.put("id", strategy.getId());
                strategyMap.put("title", strategy.getTitle());
                strategyMap.put("summary", strategy.getSummary());
                strategyMap.put("coverImage", strategy.getCoverImage());
                strategyMap.put("category", strategy.getCategory());
                strategyMap.put("viewCount", strategy.getViewCount());
                strategyMap.put("likeCount", strategy.getLikeCount());
                strategyMap.put("favoriteCount", strategy.getFavoriteCount());
                strategyMap.put("createTime", strategy.getCreateTime());
                return strategyMap;
            }).collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("records", records);
            result.put("total", page.getTotal());
            result.put("pageNum", page.getCurrent());
            result.put("pageSize", page.getSize());
            
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("获取用户攻略失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/favorites")
    @Operation(summary = "用户收藏的攻略")
    public BaseResponse<Map<String, Object>> getUserFavorites(
            @RequestParam long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            Page<Strategy> page = strategyService.getUserFavorites(userId, pageNum, pageSize);
            
            // 组装完整数据
            List<Map<String, Object>> records = page.getRecords().stream().map(strategy -> {
                Map<String, Object> strategyMap = new HashMap<>();
                strategyMap.put("id", strategy.getId());
                strategyMap.put("title", strategy.getTitle());
                strategyMap.put("summary", strategy.getSummary());
                strategyMap.put("coverImage", strategy.getCoverImage());
                strategyMap.put("category", strategy.getCategory());
                strategyMap.put("viewCount", strategy.getViewCount());
                strategyMap.put("likeCount", strategy.getLikeCount());
                strategyMap.put("favoriteCount", strategy.getFavoriteCount());
                strategyMap.put("createTime", strategy.getCreateTime());
                return strategyMap;
            }).collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("records", records);
            result.put("total", page.getTotal());
            result.put("pageNum", page.getCurrent());
            result.put("pageSize", page.getSize());
            
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("获取用户收藏攻略失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 用户点赞的攻略
     */
    @GetMapping("/liked")
    @Operation(summary = "用户点赞的攻略")
    public BaseResponse<Map<String, Object>> getUserLikedStrategies(
            @RequestParam long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            Page<Strategy> page = strategyService.getUserLikedStrategies(userId, pageNum, pageSize);
            
            // 组装完整数据
            List<Map<String, Object>> records = page.getRecords().stream().map(strategy -> {
                Map<String, Object> strategyMap = new HashMap<>();
                strategyMap.put("id", strategy.getId());
                strategyMap.put("title", strategy.getTitle());
                strategyMap.put("summary", strategy.getSummary());
                strategyMap.put("coverImage", strategy.getCoverImage());
                strategyMap.put("category", strategy.getCategory());
                strategyMap.put("type", strategy.getType());
                strategyMap.put("viewCount", strategy.getViewCount());
                strategyMap.put("likeCount", strategy.getLikeCount());
                strategyMap.put("favoriteCount", strategy.getFavoriteCount());
                strategyMap.put("createTime", strategy.getCreateTime());
                
                // 查询作者信息
                if (strategy.getAuthorId() != null) {
                    User author = userMapper.selectById(strategy.getAuthorId());
                    if (author != null) {
                        strategyMap.put("authorId", author.getId());
                        strategyMap.put("authorName", author.getUsername());
                        strategyMap.put("authorAvatar", author.getAvatarUrl());
                    }
                }
                
                return strategyMap;
            }).collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("records", records);
            result.put("total", page.getTotal());
            result.put("pageNum", page.getCurrent());
            result.put("pageSize", page.getSize());
            
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("获取用户点赞攻略失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取数据失败: " + e.getMessage());
        }
    }
}
