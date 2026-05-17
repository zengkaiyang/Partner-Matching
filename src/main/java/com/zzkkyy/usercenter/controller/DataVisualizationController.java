package com.zzkkyy.usercenter.controller;

import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.model.domain.HiveTagStats;
import com.zzkkyy.usercenter.model.domain.UserActivityStats;
import com.zzkkyy.usercenter.model.domain.UserCityDistribution;
import com.zzkkyy.usercenter.service.DataCleaningService;
import com.zzkkyy.usercenter.service.HiveDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 数据可视化控制器
 */
@RestController
@RequestMapping("/api/data")
@Tag(name = "数据可视化", description = "数据统计和可视化接口")
@Slf4j
public class DataVisualizationController {

    @Resource
    private HiveDataService hiveDataService;

    @Resource
    private DataCleaningService dataCleaningService;

    @GetMapping("/tag-stats")
    @Operation(summary = "标签统计数据")
    public BaseResponse<List<HiveTagStats>> getTagStats() {
        List<HiveTagStats> stats = hiveDataService.getTagStats();
        return ResultUtils.success(stats);
    }

    @GetMapping("/city-distribution")
    @Operation(summary = "城市分布数据")
    public BaseResponse<List<UserCityDistribution>> getCityDistribution() {
        List<UserCityDistribution> distribution = hiveDataService.getCityDistribution();
        return ResultUtils.success(distribution);
    }

    @GetMapping("/activity-trend")
    @Operation(summary = "活跃度趋势数据")
    public BaseResponse<List<UserActivityStats>> getActivityTrend() {
        List<UserActivityStats> trend = hiveDataService.getActivityTrend();
        return ResultUtils.success(trend);
    }

    @GetMapping("/level-distribution")
    @Operation(summary = "等级分布数据")
    public BaseResponse<Map<String, Object>> getLevelDistribution() {
        Map<String, Object> distribution = hiveDataService.getLevelDistribution();
        return ResultUtils.success(distribution);
    }

    @GetMapping("/popular-tags")
    @Operation(summary = "热门标签（兼容旧接口）")
    public BaseResponse<List<Map<String, Object>>> getPopularTags(
            @RequestParam(defaultValue = "20") int limit) {
        List<Map<String, Object>> tags = dataCleaningService.getPopularTags(limit);
        return ResultUtils.success(tags);
    }

    @PostMapping("/sync-hive")
    @Operation(summary = "同步Hive数据到MySQL")
    public BaseResponse<Boolean> syncHiveData() {
        hiveDataService.syncHiveDataToMySQL();
        return ResultUtils.success(true);
    }
}
