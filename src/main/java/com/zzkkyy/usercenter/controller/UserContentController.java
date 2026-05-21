package com.zzkkyy.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ErrorCode;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.exception.BusinessException;
import com.zzkkyy.usercenter.model.domain.ForumPost;
import com.zzkkyy.usercenter.model.domain.Strategy;
import com.zzkkyy.usercenter.model.domain.Team;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.service.ForumPostService;
import com.zzkkyy.usercenter.service.StrategyService;
import com.zzkkyy.usercenter.service.TeamService;
import com.zzkkyy.usercenter.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户个人内容管理控制器
 * 普通用户可以管理自己的队伍、帖子、攻略等内容
 */
@RestController
@RequestMapping("/user/content")
@Tag(name = "用户内容管理", description = "用户管理自己的内容")
@Slf4j
public class UserContentController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private ForumPostService forumPostService;

    @Resource
    private StrategyService strategyService;

    /**
     * 获取当前登录用户
     */
    private User getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH, "请先登录");
        }
        return loginUser;
    }

    // ========== 队伍管理 ==========

    @GetMapping("/teams")
    @Operation(summary = "获取我创建的队伍列表")
    public BaseResponse<Map<String, Object>> getMyTeams(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        
        // 查询用户创建的队伍
        Page<Team> page = new Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Team> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.orderByDesc("createTime");
        
        Page<Team> teamPage = teamService.page(page, queryWrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", teamPage.getRecords());
        result.put("total", teamPage.getTotal());
        result.put("pageNum", teamPage.getCurrent());
        result.put("pageSize", teamPage.getSize());
        
        return ResultUtils.success(result);
    }

    @PostMapping("/team/delete")
    @Operation(summary = "删除我创建的队伍")
    public BaseResponse<Boolean> deleteMyTeam(@RequestParam long teamId, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        
        // 验证队伍是否存在且属于当前用户
        Team team = teamService.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        if (!team.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权操作该队伍");
        }
        
        boolean result = teamService.deleteTeam(teamId, loginUser);
        return ResultUtils.success(result);
    }

    // ========== 帖子管理 ==========

    @GetMapping("/posts")
    @Operation(summary = "获取我发布的帖子列表")
    public BaseResponse<Map<String, Object>> getMyPosts(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        
        Page<ForumPost> page = forumPostService.getUserPosts(loginUser.getId(), pageNum, pageSize);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        result.put("pageNum", page.getCurrent());
        result.put("pageSize", page.getSize());
        
        return ResultUtils.success(result);
    }

    @PostMapping("/post/delete")
    @Operation(summary = "删除我发布的帖子")
    public BaseResponse<Boolean> deleteMyPost(@RequestParam long postId, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        
        // 验证帖子是否存在且属于当前用户
        ForumPost post = forumPostService.getPostById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "帖子不存在");
        }
        if (!post.getAuthorId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权操作该帖子");
        }
        
        boolean result = forumPostService.deletePost(postId, loginUser.getId());
        return ResultUtils.success(result);
    }

    // ========== 攻略管理 ==========

    @GetMapping("/strategies")
    @Operation(summary = "获取我发布的攻略列表")
    public BaseResponse<Map<String, Object>> getMyStrategies(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        
        Page<Strategy> page = strategyService.getUserStrategies(loginUser.getId(), pageNum, pageSize);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", page.getRecords());
        result.put("total", page.getTotal());
        result.put("pageNum", page.getCurrent());
        result.put("pageSize", page.getSize());
        
        return ResultUtils.success(result);
    }

    @PostMapping("/strategy/delete")
    @Operation(summary = "删除我发布的攻略")
    public BaseResponse<Boolean> deleteMyStrategy(@RequestParam long strategyId, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        
        // 验证攻略是否存在且属于当前用户
        Strategy strategy = strategyService.getStrategyById(strategyId);
        if (strategy == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "攻略不存在");
        }
        if (!strategy.getAuthorId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权操作该攻略");
        }
        
        boolean result = strategyService.deleteStrategy(strategyId, loginUser.getId());
        return ResultUtils.success(result);
    }

    // ========== 统计信息 ==========

    @GetMapping("/stats")
    @Operation(summary = "获取我的内容统计信息")
    public BaseResponse<Map<String, Object>> getMyStats(HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        Long userId = loginUser.getId();
        
        Map<String, Object> stats = new HashMap<>();
        
        // 统计创建的队伍数
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Team> teamQuery = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        teamQuery.eq("userId", userId);
        long teamCount = teamService.count(teamQuery);
        stats.put("teamCount", teamCount);
        
        // 统计发布的帖子数
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ForumPost> postQuery = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        postQuery.eq("author_id", userId);
        long postCount = forumPostService.getPostMapper().selectCount(postQuery);
        stats.put("postCount", postCount);
        
        // 统计发布的攻略数
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Strategy> strategyQuery = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        strategyQuery.eq("author_id", userId);
        long strategyCount = strategyService.getStrategyMapper().selectCount(strategyQuery);
        stats.put("strategyCount", strategyCount);
        
        return ResultUtils.success(stats);
    }
}
