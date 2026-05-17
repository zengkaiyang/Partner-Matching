package com.zzkkyy.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.model.domain.ForumPost;
import com.zzkkyy.usercenter.service.ForumPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 论坛控制器
 */
@RestController
@RequestMapping("/api/forum")
@Tag(name = "论坛管理", description = "论坛帖子相关接口")
@Slf4j
public class ForumController {

    @Resource
    private ForumPostService forumPostService;

    @PostMapping("/post/add")
    @Operation(summary = "发布帖子")
    public BaseResponse<Long> addPost(@RequestBody ForumPost post, @RequestParam List<String> tags) {
        long postId = forumPostService.addPost(post, tags);
        return ResultUtils.success(postId);
    }

    @PostMapping("/post/delete")
    @Operation(summary = "删除帖子")
    public BaseResponse<Boolean> deletePost(@RequestParam long postId, @RequestParam long userId) {
        boolean result = forumPostService.deletePost(postId, userId);
        return ResultUtils.success(result);
    }

    @PostMapping("/post/update")
    @Operation(summary = "更新帖子")
    public BaseResponse<Boolean> updatePost(@RequestBody ForumPost post, @RequestParam(required = false) List<String> tags) {
        boolean result = forumPostService.updatePost(post, tags);
        return ResultUtils.success(result);
    }

    @GetMapping("/post/detail")
    @Operation(summary = "获取帖子详情")
    public BaseResponse<ForumPost> getPostDetail(@RequestParam long postId) {
        // 增加浏览量
        forumPostService.incrementViewCount(postId);
        ForumPost post = forumPostService.getPostById(postId);
        return ResultUtils.success(post);
    }

    @GetMapping("/post/list")
    @Operation(summary = "帖子列表")
    public BaseResponse<Page<ForumPost>> listPosts(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "latest") String sortBy) {
        Page<ForumPost> page = forumPostService.listPosts(pageNum, pageSize, category, sortBy);
        return ResultUtils.success(page);
    }

    @GetMapping("/post/search")
    @Operation(summary = "搜索帖子")
    public BaseResponse<Page<ForumPost>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<ForumPost> page = forumPostService.searchPosts(keyword, pageNum, pageSize);
        return ResultUtils.success(page);
    }

    @PostMapping("/post/like")
    @Operation(summary = "点赞帖子")
    public BaseResponse<Boolean> likePost(@RequestParam long postId, @RequestParam long userId) {
        boolean result = forumPostService.likePost(postId, userId);
        return ResultUtils.success(result);
    }

    @PostMapping("/post/unlike")
    @Operation(summary = "取消点赞")
    public BaseResponse<Boolean> unlikePost(@RequestParam long postId, @RequestParam long userId) {
        boolean result = forumPostService.unlikePost(postId, userId);
        return ResultUtils.success(result);
    }

    @PostMapping("/post/favorite")
    @Operation(summary = "收藏帖子")
    public BaseResponse<Boolean> favoritePost(@RequestParam long postId, @RequestParam long userId) {
        boolean result = forumPostService.favoritePost(postId, userId);
        return ResultUtils.success(result);
    }

    @PostMapping("/post/unfavorite")
    @Operation(summary = "取消收藏")
    public BaseResponse<Boolean> unfavoritePost(@RequestParam long postId, @RequestParam long userId) {
        boolean result = forumPostService.unfavoritePost(postId, userId);
        return ResultUtils.success(result);
    }

    @GetMapping("/post/hot")
    @Operation(summary = "热门帖子")
    public BaseResponse<List<ForumPost>> getHotPosts(@RequestParam(defaultValue = "10") int limit) {
        List<ForumPost> posts = forumPostService.getHotPosts(limit);
        return ResultUtils.success(posts);
    }

    @GetMapping("/tags/hot")
    @Operation(summary = "热门标签")
    public BaseResponse<List<Map<String, Object>>> getHotTags(@RequestParam(defaultValue = "20") int limit) {
        List<Map<String, Object>> tags = forumPostService.getHotTags(limit);
        return ResultUtils.success(tags);
    }

    @GetMapping("/post/user")
    @Operation(summary = "用户发布的帖子")
    public BaseResponse<Page<ForumPost>> getUserPosts(
            @RequestParam long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<ForumPost> page = forumPostService.getUserPosts(userId, pageNum, pageSize);
        return ResultUtils.success(page);
    }

    @GetMapping("/post/favorites")
    @Operation(summary = "用户收藏的帖子")
    public BaseResponse<Page<ForumPost>> getUserFavorites(
            @RequestParam long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<ForumPost> page = forumPostService.getUserFavorites(userId, pageNum, pageSize);
        return ResultUtils.success(page);
    }
}
