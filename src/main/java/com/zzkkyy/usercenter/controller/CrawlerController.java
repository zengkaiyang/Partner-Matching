package com.zzkkyy.usercenter.controller;

import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ErrorCode;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.crawler.service.CrawlerService;
import com.zzkkyy.usercenter.job.DataCrawlJob;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 爬虫管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/crawler")
@Tag(name = "爬虫管理", description = "用户标签数据爬取相关接口")
public class CrawlerController {

    @Autowired
    private CrawlerService crawlerService;

    @Autowired
    private DataCrawlJob dataCrawlJob;

    /**
     * 启动爬虫
     */
    @PostMapping("/start")
    @Operation(summary = "启动爬虫", description = "异步启动用户标签爬虫")
    public BaseResponse<String> startCrawler() {
        try {
            crawlerService.startCrawler();
            return ResultUtils.success("爬虫已启动");
        } catch (Exception e) {
            log.error("启动爬虫失败: {}", e.getMessage(), e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "启动爬虫失败: " + e.getMessage());
        }
    }

    /**
     * 同步启动爬虫（等待完成）
     */
    @PostMapping("/start-sync")
    @Operation(summary = "同步启动爬虫", description = "同步启动用户标签爬虫，等待完成后返回")
    public BaseResponse<String> startCrawlerSync() {
        try {
            crawlerService.startCrawlerSync();
            return ResultUtils.success("爬虫已完成");
        } catch (Exception e) {
            log.error("启动爬虫失败: {}", e.getMessage(), e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "启动爬虫失败: " + e.getMessage());
        }
    }

    /**
     * 停止爬虫
     */
    @PostMapping("/stop")
    @Operation(summary = "停止爬虫", description = "停止正在运行的爬虫")
    public BaseResponse<String> stopCrawler() {
        try {
            crawlerService.stopCrawler();
            return ResultUtils.success("爬虫已停止");
        } catch (Exception e) {
            log.error("停止爬虫失败: {}", e.getMessage(), e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "停止爬虫失败: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    @Operation(summary = "获取爬虫状态", description = "查询当前爬虫的运行状态")
    public BaseResponse<String> getCrawlerStatus() {
        try {
            String status = crawlerService.getCrawlerStatus();
            return ResultUtils.success(status);
        } catch (Exception e) {
            log.error("获取爬虫状态失败: {}", e.getMessage(), e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取状态失败: " + e.getMessage());
        }
    }

    @PostMapping("/execute-now")
    @Operation(summary = "立即执行爬取", description = "手动触发一次用户标签爬取任务")
    public BaseResponse<String> executeCrawlNow() {
        try {
            dataCrawlJob.executeCrawlNow();
            return ResultUtils.success("爬取任务已执行完成");
        } catch (Exception e) {
            log.error("执行爬取任务失败: {}", e.getMessage(), e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "执行失败: " + e.getMessage());
        }
    }
}
