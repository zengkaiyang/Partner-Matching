package com.zzkkyy.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ErrorCode;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.model.domain.ForumPost;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.service.ForumPostService;
import com.zzkkyy.usercenter.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 论坛控制器
 */
@RestController
@RequestMapping("/forum")
@Tag(name = "论坛管理", description = "论坛帖子相关接口")
@Slf4j
public class ForumController {

    @Resource
    private ForumPostService forumPostService;
    
    @Resource
    private UserService userService;

    /**
     * 获取论坛帖子列表（通用）
     */
    @GetMapping("/post/list")
    @Operation(summary = "获取论坛帖子列表")
    public BaseResponse<Map<String, Object>> listPosts(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Long userId) {
        try {
            Page<ForumPost> page = forumPostService.listPosts(pageNum, pageSize, category, sortBy, tag);
            
            // 组装完整数据，包含作者信息和用户点赞/收藏状态
            List<Map<String, Object>> records = page.getRecords().stream().map(post -> {
                Map<String, Object> postMap = new HashMap<>();
                postMap.put("id", post.getId());
                postMap.put("title", post.getTitle());
                postMap.put("summary", post.getSummary() != null ? post.getSummary() : 
                    (post.getContent() != null && post.getContent().length() > 100 ? 
                     post.getContent().substring(0, 100) : post.getContent()));
                postMap.put("category", post.getCategory());
                postMap.put("viewCount", post.getViewCount());
                postMap.put("likeCount", post.getLikeCount());
                postMap.put("commentCount", post.getCommentCount());
                postMap.put("createTime", post.getCreateTime());
                
                // 查询作者信息
                if (post.getAuthorId() != null) {
                    User author = userService.getUserById(post.getAuthorId());
                    if (author != null) {
                        postMap.put("authorId", author.getId());
                        postMap.put("authorName", author.getUsername());
                        postMap.put("authorAvatar", author.getAvatarUrl());
                    }
                }
                
                // 查询当前用户的点赞和收藏状态
                if (userId != null) {
                    // TODO: 查询点赞和收藏状态
                    postMap.put("isLiked", false);
                    postMap.put("isFavorited", false);
                } else {
                    postMap.put("isLiked", false);
                    postMap.put("isFavorited", false);
                }
                
                return postMap;
            }).collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("records", records);
            result.put("total", page.getTotal());
            result.put("pageNum", page.getCurrent());
            result.put("pageSize", page.getSize());
            
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("获取帖子列表失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户发布的帖子
     */
    @GetMapping("/user/posts")
    @Operation(summary = "获取用户发布的帖子")
    public BaseResponse<Map<String, Object>> getUserPosts(
            @RequestParam long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            Page<ForumPost> page = forumPostService.getUserPosts(userId, pageNum, pageSize);
            
            // 组装完整数据
            List<Map<String, Object>> records = page.getRecords().stream().map(post -> {
                Map<String, Object> postMap = new HashMap<>();
                postMap.put("id", post.getId());
                postMap.put("title", post.getTitle());
                postMap.put("summary", post.getSummary() != null ? post.getSummary() : 
                    (post.getContent() != null && post.getContent().length() > 100 ? 
                     post.getContent().substring(0, 100) : post.getContent()));
                postMap.put("category", post.getCategory());
                postMap.put("viewCount", post.getViewCount());
                postMap.put("likeCount", post.getLikeCount());
                postMap.put("commentCount", post.getCommentCount());
                postMap.put("createTime", post.getCreateTime());
                return postMap;
            }).collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("records", records);
            result.put("total", page.getTotal());
            result.put("pageNum", page.getCurrent());
            result.put("pageSize", page.getSize());
            
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("获取用户帖子失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户收藏的帖子
     */
    @GetMapping("/user/favorites")
    @Operation(summary = "获取用户收藏的帖子")
    public BaseResponse<Map<String, Object>> getUserFavoritePosts(
            @RequestParam long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            Page<ForumPost> page = forumPostService.getUserFavorites(userId, pageNum, pageSize);
            
            // 组装完整数据
            List<Map<String, Object>> records = page.getRecords().stream().map(post -> {
                Map<String, Object> postMap = new HashMap<>();
                postMap.put("id", post.getId());
                postMap.put("title", post.getTitle());
                postMap.put("summary", post.getSummary() != null ? post.getSummary() : 
                    (post.getContent() != null && post.getContent().length() > 100 ? 
                     post.getContent().substring(0, 100) : post.getContent()));
                postMap.put("category", post.getCategory());
                postMap.put("viewCount", post.getViewCount());
                postMap.put("likeCount", post.getLikeCount());
                postMap.put("commentCount", post.getCommentCount());
                postMap.put("createTime", post.getCreateTime());
                return postMap;
            }).collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("records", records);
            result.put("total", page.getTotal());
            result.put("pageNum", page.getCurrent());
            result.put("pageSize", page.getSize());
            
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("获取用户收藏帖子失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户点赞的帖子
     */
    @GetMapping("/user/liked")
    @Operation(summary = "获取用户点赞的帖子")
    public BaseResponse<Map<String, Object>> getUserLikedPosts(
            @RequestParam long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            Page<ForumPost> page = forumPostService.getUserLikedPosts(userId, pageNum, pageSize);
            
            // 组装完整数据
            List<Map<String, Object>> records = page.getRecords().stream().map(post -> {
                Map<String, Object> postMap = new HashMap<>();
                postMap.put("id", post.getId());
                postMap.put("title", post.getTitle());
                postMap.put("summary", post.getSummary() != null ? post.getSummary() : 
                    (post.getContent() != null && post.getContent().length() > 100 ? 
                     post.getContent().substring(0, 100) : post.getContent()));
                postMap.put("category", post.getCategory());
                postMap.put("viewCount", post.getViewCount());
                postMap.put("likeCount", post.getLikeCount());
                postMap.put("commentCount", post.getCommentCount());
                postMap.put("createTime", post.getCreateTime());
                return postMap;
            }).collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("records", records);
            result.put("total", page.getTotal());
            result.put("pageNum", page.getCurrent());
            result.put("pageSize", page.getSize());
            
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("获取用户点赞帖子失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/post/detail")
    @Operation(summary = "获取帖子详情")
    public BaseResponse<Map<String, Object>> getPostDetail(
            @RequestParam long postId,
            @RequestParam(required = false) Long userId) {
        try {
            // 增加浏览量
            forumPostService.incrementViewCount(postId);
            
            // 获取帖子详情
            Map<String, Object> detail = forumPostService.getPostDetail(postId, userId);
            return ResultUtils.success(detail);
        } catch (Exception e) {
            log.error("获取帖子详情失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 发布帖子
     */
    @PostMapping("/post/add")
    @Operation(summary = "发布帖子")
    public BaseResponse<Long> addPost(
            @RequestBody ForumPost post,
            @RequestParam(required = false) String tags,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            // 获取当前登录用户
            User loginUser = userService.getLoginUser(request);
            
            if (loginUser == null) {
                return ResultUtils.error(ErrorCode.NO_AUTH, "请先登录");
            }
            
            // 设置作者ID
            post.setAuthorId(loginUser.getId());
            
            // 解析标签 - 支持多种格式
            List<String> tagList = null;
            if (tags != null && !tags.isEmpty()) {
                try {
                    // 尝试作为JSON数组解析
                    if (tags.startsWith("[")) {
                        tagList = new com.fasterxml.jackson.databind.ObjectMapper()
                                .readValue(tags, new com.fasterxml.jackson.core.type.TypeReference<List<String>>(){});
                    } else {
                        // 如果是逗号分隔的字符串，直接分割
                        String[] tagArray = tags.split(",");
                        tagList = java.util.Arrays.stream(tagArray)
                                .map(String::trim)
                                .filter(t -> !t.isEmpty())
                                .collect(java.util.stream.Collectors.toList());
                    }
                } catch (Exception e) {
                    log.warn("标签解析失败，使用空列表: {}", tags, e);
                    tagList = new java.util.ArrayList<>();
                }
            }
            
            long postId = forumPostService.addPost(post, tagList);
            return ResultUtils.success(postId);
        } catch (Exception e) {
            log.error("发布帖子失败", e);
            return ResultUtils.error(ErrorCode.SAVE_ERROR, "发布失败: " + e.getMessage());
        }
    }

    /**
     * 删除帖子（管理员或作者）
     */
    @PostMapping("/post/delete")
    @Operation(summary = "删除帖子")
    public BaseResponse<Boolean> deletePost(@RequestParam long id) {
        try {
            // 管理员删除不需要userId参数，直接传入0
            boolean result = forumPostService.deletePost(id, 0);
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("删除帖子失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "删除失败: " + e.getMessage());
        }
    }

    /**
     * 更新帖子
     */
    @PostMapping("/post/update")
    @Operation(summary = "更新帖子")
    public BaseResponse<Boolean> updatePost(
            @RequestBody ForumPost post,
            @RequestParam(required = false) String tags) {
        try {
            // 解析标签
            List<String> tagList = null;
            if (tags != null && !tags.isEmpty()) {
                try {
                    // 尝试作为JSON数组解析
                    if (tags.startsWith("[")) {
                        tagList = new com.fasterxml.jackson.databind.ObjectMapper()
                                .readValue(tags, new com.fasterxml.jackson.core.type.TypeReference<List<String>>(){});
                    } else {
                        // 如果是逗号分隔的字符串，直接分割
                        String[] tagArray = tags.split(",");
                        tagList = java.util.Arrays.stream(tagArray)
                                .map(String::trim)
                                .filter(t -> !t.isEmpty())
                                .collect(java.util.stream.Collectors.toList());
                    }
                } catch (Exception e) {
                    log.warn("标签解析失败，使用空列表: {}", tags, e);
                    tagList = new java.util.ArrayList<>();
                }
            }
            
            boolean result = forumPostService.updatePost(post, tagList);
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("更新帖子失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "更新失败: " + e.getMessage());
        }
    }
}
