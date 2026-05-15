package com.zzkkyy.usercenter.service;

import java.util.Map;

public interface StatsService {

    /**
     * 获取标签统计分析数据
     * @return 统计数据
     */
    Map<String, Object> getTagsAnalysis();
}
