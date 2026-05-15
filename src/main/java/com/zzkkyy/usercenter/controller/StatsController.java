package com.zzkkyy.usercenter.controller;

import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.service.StatsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 统计分析接口
 */
@CrossOrigin(origins = "http://localhost:5173/", allowCredentials = "true")
@RestController
@RequestMapping("/stats")
public class StatsController {

    @Resource
    private StatsService statsService;

    /**
     * 获取标签统计分析数据
     */
    @GetMapping("/tags/analysis")
    public BaseResponse<Map<String, Object>> getTagsAnalysis() {
        Map<String, Object> analysisData = statsService.getTagsAnalysis();
        return ResultUtils.success(analysisData);
    }
}
