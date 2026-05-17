package com.zzkkyy.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.model.domain.Strategy;
import com.zzkkyy.usercenter.service.StrategyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 攻略控制器
 */
@RestController
@RequestMapping("/api/strategy")
@Tag(name = "攻略管理", description = "攻略相关接口")
@Slf4j
public class StrategyController {

    @Resource
    private StrategyService strategyService;

    @PostMapping("/add")
    @Operation(summary = "发布攻略")
    public BaseResponse<Long> addStrategy(@RequestBody Strategy strategy, @RequestParam List<String> tags) {
        long strategyId = strategyService.addStrategy(strategy, tags);
        return ResultUtils.success(strategyId);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除攻略")
    public BaseResponse<Boolean> deleteStrategy(@RequestParam long strategyId, @RequestParam long userId) {
        boolean result = strategyService.deleteStrategy(strategyId, userId);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    @Operation(summary = "更新攻略")
    public BaseResponse<Boolean> updateStrategy(@RequestBody Strategy strategy, @RequestParam(required = false) List<String> tags) {
        boolean result = strategyService.updateStrategy(strategy, tags);
        return ResultUtils.success(result);
    }

    @GetMapping("/detail")
    @Operation(summary = "获取攻略详情")
    public BaseResponse<Strategy> getStrategyDetail(@RequestParam long strategyId) {
        strategyService.incrementViewCount(strategyId);
        Strategy strategy = strategyService.getStrategyById(strategyId);
        return ResultUtils.success(strategy);
    }

    @GetMapping("/list")
    @Operation(summary = "攻略列表")
    public BaseResponse<Page<Strategy>> listStrategies(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "newest") String sortBy) {
        Page<Strategy> page = strategyService.listStrategies(pageNum, pageSize, category, type, sortBy);
        return ResultUtils.success(page);
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
    @Operation(summary = "用户发布的攻略")
    public BaseResponse<Page<Strategy>> getUserStrategies(
            @RequestParam long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<Strategy> page = strategyService.getUserStrategies(userId, pageNum, pageSize);
        return ResultUtils.success(page);
    }

    @GetMapping("/favorites")
    @Operation(summary = "用户收藏的攻略")
    public BaseResponse<Page<Strategy>> getUserFavorites(
            @RequestParam long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<Strategy> page = strategyService.getUserFavorites(userId, pageNum, pageSize);
        return ResultUtils.success(page);
    }
}
