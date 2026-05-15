package com.zzkkyy.usercenter.controller;

import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.service.DataCleaningService;
import com.zzkkyy.usercenter.service.HiveImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.zzkkyy.usercenter.common.ErrorCode.*;

/**
 * 数据清洗控制器 - 提供大数据处理接口
 */
@Slf4j
@RestController
@RequestMapping("/data-cleaning")
@Tag(name = "数据清洗管理", description = "大数据清洗和分析接口")
public class DataCleaningController {

    @Autowired
    private DataCleaningService dataCleaningService;

    @Autowired
    private HiveImportService hiveImportService;

    /**
     * 执行数据清洗并导入 Hive
     */
    @PostMapping("/execute-and-import")
    @Operation(summary = "执行数据清洗并导入Hive", description = "爬取数据 → 清洗 → 导入Hive")
    public BaseResponse<String> executeCleaningAndImport() {
        try {
            log.info("接收到数据清洗和 Hive 导入请求");
            
            // 1. 执行数据清洗
            log.info("步骤1: 执行数据清洗...");
            dataCleaningService.executeDataCleaning();
            
            // 2. 导入到 Hive
            log.info("步骤2: 导入数据到 Hive...");
            hiveImportService.importToHive();
            
            return ResultUtils.success("数据清洗完成并成功导入 Hive");
        } catch (Exception e) {
            log.error("数据清洗或 Hive 导入失败: {}", e.getMessage(), e);
            return ResultUtils.error(CLEAR_ERROR, "操作失败: " + e.getMessage());
        }
    }

    /**
     * 仅导入数据到 Hive
     */
    @PostMapping("/import-to-hive")
    @Operation(summary = "导入数据到Hive", description = "将已清洗的数据导入 Hive")
    public BaseResponse<String> importToHive() {
        try {
            log.info("接收到 Hive 导入请求");
            hiveImportService.importToHive();
            return ResultUtils.success("数据成功导入 Hive");
        } catch (Exception e) {
            log.error("Hive 导入失败: {}", e.getMessage(), e);
            return ResultUtils.error(CLEAR_ERROR, "Hive 导入失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/popular-tags")
    @Operation(summary = "获取热门标签", description = "获取使用频率最高的标签列表")
    public BaseResponse<List<Map<String, Object>>> getPopularTags(
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<Map<String, Object>> popularTags = dataCleaningService.getPopularTags(limit);
            return ResultUtils.success(popularTags);
        } catch (Exception e) {
            log.error("获取热门标签失败: {}", e.getMessage(), e);
            return ResultUtils.error(GET_TAGS_ERROR, "获取热门标签失败: " + e.getMessage());
        }
    }

    /**
     * 根据标签搜索用户
     */
    @GetMapping("/search-users")
    @Operation(summary = "根据标签搜索用户", description = "查找拥有指定标签的用户")
    public BaseResponse<List<User>> searchUsersByTag(@RequestParam String tag) {
        try {
            List<User> users = dataCleaningService.searchUsersByTag(tag);
            return ResultUtils.success(users);
        } catch (Exception e) {
            log.error("搜索用户失败: {}", e.getMessage(), e);
            return ResultUtils.error(GET_USER_ERROR, "搜索用户失败: " + e.getMessage());
        }
    }

    /**
     * 获取数据清洗统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取数据统计", description = "获取用户和标签的统计信息")
    public BaseResponse<Map<String, Object>> getStatistics() {
        try {
            // 这里可以扩展更详细的统计信息
            Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("message", "请查看日志获取详细统计信息");
            stats.put("status", "success");
            return ResultUtils.success(stats);
        } catch (Exception e) {
            log.error("获取统计信息失败: {}", e.getMessage(), e);
            return ResultUtils.error(CLEAR_ERROR, "获取统计信息失败: " + e.getMessage());
        }
    }
}
