package com.zzkkyy.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.model.domain.BrowseHistory;
import com.zzkkyy.usercenter.model.domain.ForumPost;
import com.zzkkyy.usercenter.model.domain.Strategy;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 个人信息控制器
 */
@RestController
@RequestMapping("/profile")
@Tag(name = "个人信息", description = "用户个人资料相关接口")
@Slf4j
public class ProfileController {

    @Resource
    private ProfileService profileService;

    @Resource
    private ForumPostService forumPostService;

    @Resource
    private StrategyService strategyService;

    @GetMapping("/info")
    @Operation(summary = "获取个人信息")
    public BaseResponse<User> getUserProfile(@RequestParam long userId) {
        User user = profileService.getUserProfile(userId);
        return ResultUtils.success(user);
    }

    @PostMapping("/update")
    @Operation(summary = "更新个人信息")
    public BaseResponse<Boolean> updateUserProfile(@RequestBody User user) {
        boolean result = profileService.updateUserProfile(user);
        return ResultUtils.success(result);
    }

    @GetMapping("/posts")
    @Operation(summary = "我的帖子")
    public BaseResponse<Page<ForumPost>> getMyPosts(
            @RequestParam long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<ForumPost> page = forumPostService.getUserPosts(userId, pageNum, pageSize);
        return ResultUtils.success(page);
    }

    @GetMapping("/strategies")
    @Operation(summary = "我的攻略")
    public BaseResponse<Page<Strategy>> getMyStrategies(
            @RequestParam long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<Strategy> page = strategyService.getUserStrategies(userId, pageNum, pageSize);
        return ResultUtils.success(page);
    }

    @GetMapping("/history")
    @Operation(summary = "浏览历史")
    public BaseResponse<List<BrowseHistory>> getBrowseHistory(
            @RequestParam long userId,
            @RequestParam(defaultValue = "50") int limit) {
        List<BrowseHistory> history = profileService.getBrowseHistory(userId, limit);
        return ResultUtils.success(history);
    }

    @PostMapping("/follow")
    @Operation(summary = "关注用户")
    public BaseResponse<Boolean> followUser(
            @RequestParam long followerId,
            @RequestParam long followingId) {
        boolean result = profileService.followUser(followerId, followingId);
        return ResultUtils.success(result);
    }

    @PostMapping("/unfollow")
    @Operation(summary = "取消关注")
    public BaseResponse<Boolean> unfollowUser(
            @RequestParam long followerId,
            @RequestParam long followingId) {
        boolean result = profileService.unfollowUser(followerId, followingId);
        return ResultUtils.success(result);
    }

    @GetMapping("/followers")
    @Operation(summary = "粉丝列表")
    public BaseResponse<List<User>> getFollowers(
            @RequestParam long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<User> followers = profileService.getFollowers(userId, pageNum, pageSize);
        return ResultUtils.success(followers);
    }

    @GetMapping("/following")
    @Operation(summary = "关注列表")
    public BaseResponse<List<User>> getFollowing(
            @RequestParam long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<User> following = profileService.getFollowing(userId, pageNum, pageSize);
        return ResultUtils.success(following);
    }

    @GetMapping("/is-following")
    @Operation(summary = "检查是否已关注")
    public BaseResponse<Boolean> isFollowing(
            @RequestParam long followerId,
            @RequestParam long followingId) {
        boolean result = profileService.isFollowing(followerId, followingId);
        return ResultUtils.success(result);
    }
    @GetMapping("/favorites")
    @Operation(summary = "我的收藏")
    public BaseResponse<List<?>> getFavorites(
            @RequestParam long userId,
            @RequestParam String type) {
        List<?> favorites = profileService.getFavorites(userId, type);
        return ResultUtils.success(favorites);
    }

    @PostMapping("/unfavorite")
    @Operation(summary = "取消收藏")
    public BaseResponse<Boolean> unfavorite(@RequestBody java.util.Map<String, Long> request) {
        Long id = request.get("id");
        boolean result = profileService.unfavorite(id);
        return ResultUtils.success(result);
    }
}
