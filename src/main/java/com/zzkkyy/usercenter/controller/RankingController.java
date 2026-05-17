package com.zzkkyy.usercenter.controller;

import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.model.domain.ForumPost;
import com.zzkkyy.usercenter.model.domain.Strategy;
import com.zzkkyy.usercenter.model.domain.UserExperience;
import com.zzkkyy.usercenter.service.ForumPostService;
import com.zzkkyy.usercenter.service.StrategyService;
import com.zzkkyy.usercenter.service.UserExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 排行榜控制器
 */
@RestController
@RequestMapping("/api/ranking")
@Tag(name = "排行榜", description = "各类排行榜接口")
@Slf4j
public class RankingController {

    @Resource
    private ForumPostService forumPostService;

    @Resource
    private StrategyService strategyService;

    @Resource
    private UserExperienceService userExperienceService;

    @GetMapping("/strategy")
    @Operation(summary = "热门攻略排行榜")
    public BaseResponse<List<Strategy>> getStrategyRanking(
            @RequestParam(defaultValue = "10") int limit) {
        List<Strategy> strategies = strategyService.getHotStrategies(limit);
        return ResultUtils.success(strategies);
    }

    @GetMapping("/forum")
    @Operation(summary = "热门论坛帖子排行榜")
    public BaseResponse<List<ForumPost>> getForumRanking(
            @RequestParam(defaultValue = "10") int limit) {
        List<ForumPost> posts = forumPostService.getHotPosts(limit);
        return ResultUtils.success(posts);
    }

    @GetMapping("/authors")
    @Operation(summary = "优质作者排行榜")
    public BaseResponse<List<UserExperience>> getAuthorRanking(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "all") String period) {
        List<UserExperience> authors = userExperienceService.getTopAuthors(limit, period);
        return ResultUtils.success(authors);
    }
}
