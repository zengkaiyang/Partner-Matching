package com.zzkkyy.usercenter.service;

import com.zzkkyy.usercenter.model.domain.HiveTagStats;
import com.zzkkyy.usercenter.model.domain.UserActivityStats;
import com.zzkkyy.usercenter.model.domain.UserCityDistribution;

import java.util.List;
import java.util.Map;

/**
 * Hive数据服务
 */
public interface HiveDataService {
    
    /**
     * 获取标签统计数据（优先Hive，失败则MySQL兜底）
     */
    List<HiveTagStats> getTagStats();
    
    /**
     * 获取城市分布数据
     */
    List<UserCityDistribution> getCityDistribution();
    
    /**
     * 获取活跃度趋势数据
     */
    List<UserActivityStats> getActivityTrend();
    
    /**
     * 获取等级分布数据
     */
    Map<String, Object> getLevelDistribution();
    
    /**
     * 同步Hive数据到MySQL
     */
    void syncHiveDataToMySQL();
}
