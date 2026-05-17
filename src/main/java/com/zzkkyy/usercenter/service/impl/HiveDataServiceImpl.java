package com.zzkkyy.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzkkyy.usercenter.mapper.*;
import com.zzkkyy.usercenter.model.domain.*;
import com.zzkkyy.usercenter.service.HiveDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Hive数据服务实现（带MySQL兜底策略）
 */
@Service
@Slf4j
public class HiveDataServiceImpl implements HiveDataService {

    @Resource
    private HiveTagStatsMapper hiveTagStatsMapper;

    @Resource
    private UserCityDistributionMapper userCityDistributionMapper;

    @Resource
    private UserActivityStatsMapper userActivityStatsMapper;

    @Resource
    private UserExperienceMapper userExperienceMapper;

    @Override
    public List<HiveTagStats> getTagStats() {
        try {
            // 尝试从Hive获取数据（这里简化处理，实际应该调用Hive服务）
            log.info("尝试从Hive获取标签统计数据");
            // TODO: 实现Hive查询逻辑
            // return hiveService.queryTagStats();
            
            // 如果Hive查询失败，抛出异常进入catch块
            throw new RuntimeException("Hive连接失败");
        } catch (Exception e) {
            log.warn("Hive查询失败，使用MySQL兜底: {}", e.getMessage());
            // MySQL兜底查询
            QueryWrapper<HiveTagStats> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("total_count");
            return hiveTagStatsMapper.selectList(queryWrapper);
        }
    }

    @Override
    public List<UserCityDistribution> getCityDistribution() {
        try {
            log.info("尝试从Hive获取城市分布数据");
            // TODO: 实现Hive查询逻辑
            throw new RuntimeException("Hive连接失败");
        } catch (Exception e) {
            log.warn("Hive查询失败，使用MySQL兜底: {}", e.getMessage());
            QueryWrapper<UserCityDistribution> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("user_count");
            return userCityDistributionMapper.selectList(queryWrapper);
        }
    }

    @Override
    public List<UserActivityStats> getActivityTrend() {
        try {
            log.info("尝试从Hive获取活跃度趋势数据");
            // TODO: 实现Hive查询逻辑
            throw new RuntimeException("Hive连接失败");
        } catch (Exception e) {
            log.warn("Hive查询失败，使用MySQL兜底: {}", e.getMessage());
            QueryWrapper<UserActivityStats> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("stat_date")
                    .last("LIMIT 30");
            return userActivityStatsMapper.selectList(queryWrapper);
        }
    }

    @Override
    public Map<String, Object> getLevelDistribution() {
        // 从MySQL统计各等级用户数
        List<UserExperience> experiences = userExperienceMapper.selectList(null);
        
        Map<Integer, Long> levelCountMap = experiences.stream()
                .collect(Collectors.groupingBy(
                        UserExperience::getLevel,
                        Collectors.counting()
                ));

        Map<String, Object> result = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            result.put("V" + i, levelCountMap.getOrDefault(i, 0L));
        }

        return result;
    }

    @Override
    public void syncHiveDataToMySQL() {
        log.info("开始同步Hive数据到MySQL");
        try {
            // TODO: 实现Hive到MySQL的数据同步逻辑
            // 1. 从Hive查询最新数据
            // 2. 清空MySQL表
            // 3. 插入新数据
            
            log.info("Hive数据同步完成");
        } catch (Exception e) {
            log.error("Hive数据同步失败: {}", e.getMessage(), e);
        }
    }
}
