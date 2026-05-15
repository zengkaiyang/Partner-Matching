package com.zzkkyy.usercenter.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zzkkyy.usercenter.mapper.UserMapper;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.service.StatsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatsServiceImpl implements StatsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String STATS_CACHE_KEY = "yupao:stats:tags:analysis";
    private static final long CACHE_EXPIRE_TIME = 30; // 缓存30分钟

    @Override
    public Map<String, Object> getTagsAnalysis() {
        // 尝试从缓存获取
        try {
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            Map<String, Object> cachedData = (Map<String, Object>) valueOperations.get(STATS_CACHE_KEY);
            if (cachedData != null) {
                log.info("命中统计数据缓存");
                return cachedData;
            }
        } catch (Exception e) {
            log.error("redis get key error", e);
        }

        // 查询所有用户的标签
        List<User> userList = userMapper.selectList(null);

        // 解析所有标签
        Gson gson = new Gson();
        Map<String, Integer> tagCountMap = new HashMap<>();
        Map<String, Map<String, Integer>> tagCorrelationMap = new HashMap<>();

        for (User user : userList) {
            String tagsStr = user.getTags();
            if (tagsStr == null || tagsStr.trim().isEmpty()) {
                continue;
            }

            try {
                List<String> userTags = gson.fromJson(tagsStr, new TypeToken<List<String>>() {}.getType());
                if (userTags == null || userTags.isEmpty()) {
                    continue;
                }

                // 统计每个标签的出现次数
                for (String tag : userTags) {
                    tagCountMap.merge(tag, 1, Integer::sum);
                }

                // 统计标签关联度
                for (int i = 0; i < userTags.size(); i++) {
                    for (int j = i + 1; j < userTags.size(); j++) {
                        String tag1 = userTags.get(i);
                        String tag2 = userTags.get(j);

                        tagCorrelationMap.computeIfAbsent(tag1, k -> new HashMap<>())
                                .merge(tag2, 1, Integer::sum);
                        tagCorrelationMap.computeIfAbsent(tag2, k -> new HashMap<>())
                                .merge(tag1, 1, Integer::sum);
                    }
                }
            } catch (Exception e) {
                log.error("解析用户标签失败: {}", tagsStr, e);
            }
        }

        // 构建返回数据
        Map<String, Object> result = new HashMap<>();

        // 总用户数和标签数
        result.put("totalUsers", userList.size());
        result.put("totalTags", tagCountMap.size());

        // 标签分布（饼图数据）
        List<Map<String, Object>> tagDistribution = tagCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10) // Top 10
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("tag", entry.getKey());
                    item.put("count", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
        result.put("tagDistribution", tagDistribution);

        // Top标签排行（柱状图数据）
        List<Map<String, Object>> topTags = tagDistribution;
        result.put("topTags", topTags);

        // 标签关联度（热力图数据）
        List<Map<String, Object>> tagCorrelation = new ArrayList<>();
        tagCorrelationMap.forEach((tag1, relatedMap) -> {
            relatedMap.forEach((tag2, count) -> {
                Map<String, Object> item = new HashMap<>();
                item.put("tag1", tag1);
                item.put("tag2", tag2);
                // 计算关联度百分比
                int tag1Total = tagCountMap.getOrDefault(tag1, 0);
                int correlation = tag1Total > 0 ? (count * 100 / tag1Total) : 0;
                item.put("correlation", correlation);
                tagCorrelation.add(item);
            });
        });
        // 按关联度排序，取Top 50
        tagCorrelation.sort((a, b) -> (int) b.get("correlation") - (int) a.get("correlation"));
        result.put("tagCorrelation", tagCorrelation.stream().limit(50).collect(Collectors.toList()));

        // Top关联标签详情
        List<Map<String, Object>> topCorrelations = tagCorrelationMap.entrySet().stream()
                .limit(10)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("tag", entry.getKey());

                    List<Map<String, Object>> relatedTags = entry.getValue().entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                            .limit(5)
                            .map(rel -> {
                                Map<String, Object> relItem = new HashMap<>();
                                relItem.put("tag", rel.getKey());
                                int total = tagCountMap.getOrDefault(entry.getKey(), 0);
                                relItem.put("percentage", total > 0 ? (rel.getValue() * 100 / total) : 0);
                                return relItem;
                            })
                            .collect(Collectors.toList());
                    item.put("relatedTags", relatedTags);

                    // 计算总体关联度
                    int maxCorrelation = relatedTags.isEmpty() ? 0 :
                            (int) relatedTags.get(0).get("percentage");
                    item.put("correlation", maxCorrelation);

                    return item;
                })
                .collect(Collectors.toList());
        result.put("topCorrelations", topCorrelations);

        // 用户群体画像（雷达图数据）
        Map<String, Object> userProfile = buildUserProfile(tagCountMap, userList.size());
        result.put("userProfile", userProfile);

        // 写入缓存
        try {
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(STATS_CACHE_KEY, result, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }

        return result;
    }

    /**
     * 构建用户画像数据
     */
    private Map<String, Object> buildUserProfile(Map<String, Integer> tagCountMap, int totalUsers) {
        Map<String, Object> profile = new HashMap<>();

        // 定义几个维度（可以根据实际标签调整）
        List<Map<String, Object>> indicators = new ArrayList<>();
        indicators.add(createIndicator("技术", 100));
        indicators.add(createIndicator("艺术", 100));
        indicators.add(createIndicator("运动", 100));
        indicators.add(createIndicator("音乐", 100));
        indicators.add(createIndicator("学习", 100));

        profile.put("indicators", indicators);

        // 这里需要根据实际标签分类来计算
        // 示例数据，实际应该根据标签内容分类统计
        profile.put("currentUser", Arrays.asList(60, 40, 70, 50, 80));
        profile.put("platformAvg", Arrays.asList(50, 50, 50, 50, 50));

        return profile;
    }

    private Map<String, Object> createIndicator(String name, int max) {
        Map<String, Object> indicator = new HashMap<>();
        indicator.put("name", name);
        indicator.put("max", max);
        return indicator;
    }
}
