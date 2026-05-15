package com.zzkkyy.usercenter.service;

import com.alibaba.fastjson.JSON;
import com.zzkkyy.usercenter.mapper.UserMapper;
import com.zzkkyy.usercenter.model.domain.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据清洗服务 - 使用大数据技术处理用户标签数据
 */
@Slf4j
@Service
public class DataCleaningService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 标签标准化映射表
     */
    private static final Map<String, String> TAG_NORMALIZATION = new HashMap<>();

    static {
        // Java 生态
        TAG_NORMALIZATION.put("springboot", "spring-boot");
        TAG_NORMALIZATION.put("spring boot", "spring-boot");
        TAG_NORMALIZATION.put("springmvc", "spring-mvc");
        
        // JavaScript 生态
        TAG_NORMALIZATION.put("nodejs", "node.js");
        TAG_NORMALIZATION.put("node.js", "node.js");
        TAG_NORMALIZATION.put("reactjs", "react");
        TAG_NORMALIZATION.put("vuejs", "vue");
        
        // 数据库
        TAG_NORMALIZATION.put("postgresql", "postgres");
        TAG_NORMALIZATION.put("mongo", "mongodb");
        TAG_NORMALIZATION.put("mysql", "mysql");
        
        // 容器化
        TAG_NORMALIZATION.put("k8s", "kubernetes");
        TAG_NORMALIZATION.put("docker", "docker");
        
        // 其他
        TAG_NORMALIZATION.put("c++", "cpp");
        TAG_NORMALIZATION.put("c#", "csharp");
    }

    /**
     * 热门标签列表（用于推荐）
     */
    private static final List<String> POPULAR_TAGS = Arrays.asList(
            "java", "python", "javascript", "spring-boot", "vue", "react",
            "mysql", "redis", "docker", "linux", "git", "microservices"
    );

    /**
     * 执行完整的数据清洗流程
     */
    public void executeDataCleaning() {
        log.info("========== 开始执行数据清洗 ==========");
        
        // 1. 获取所有带标签的用户
        List<User> usersWithTags = getUsersWithTags();
        log.info("📊 获取到 {} 个带标签的用户", usersWithTags.size());
        
        if (usersWithTags.isEmpty()) {
            log.warn("⚠️ 没有需要清洗的数据");
            return;
        }
        
        // 2. 标签标准化
        int standardizedCount = standardizeTags(usersWithTags);
        log.info("✅ 标准化了 {} 个用户的标签", standardizedCount);
        
        // 3. 去除重复标签
        int deduplicatedCount = removeDuplicateTags(usersWithTags);
        log.info("✅ 去重了 {} 个用户的标签", deduplicatedCount);
        
        // 4. 过滤无效标签
        int filteredCount = filterInvalidTags(usersWithTags);
        log.info("✅ 过滤了 {} 个用户的无效标签", filteredCount);
        
        // 5. 保存清洗后的数据
        saveCleanedData(usersWithTags);
        log.info("✅ 保存了 {} 个用户的清洗后数据", usersWithTags.size());
        
        // 6. 生成统计信息
        generateStatistics(usersWithTags);
        
        log.info("========== 数据清洗完成 ==========");
    }

    /**
     * 获取所有带标签的用户
     */
    private List<User> getUsersWithTags() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(User::getTags)
               .ne(User::getTags, "")
               .ne(User::getTags, "[]");
        
        return userMapper.selectList(wrapper);
    }

    /**
     * 标签标准化
     */
    private int standardizeTags(List<User> users) {
        int count = 0;
        
        for (User user : users) {
            try {
                List<String> tags = JSON.parseArray(user.getTags(), String.class);
                if (tags == null || tags.isEmpty()) {
                    continue;
                }
                
                boolean changed = false;
                List<String> standardizedTags = new ArrayList<>();
                
                for (String tag : tags) {
                    String normalized = tag.toLowerCase().trim();
                    
                    // 应用标准化映射
                    if (TAG_NORMALIZATION.containsKey(normalized)) {
                        String standardTag = TAG_NORMALIZATION.get(normalized);
                        standardizedTags.add(standardTag);
                        changed = true;
                        log.debug("标准化标签: {} -> {}", tag, standardTag);
                    } else {
                        standardizedTags.add(normalized);
                    }
                }
                
                if (changed) {
                    user.setTags(JSON.toJSONString(standardizedTags));
                    count++;
                }
                
            } catch (Exception e) {
                log.error("用户 {} 标签标准化失败: {}", user.getId(), e.getMessage());
            }
        }
        
        return count;
    }

    /**
     * 去除重复标签
     */
    private int removeDuplicateTags(List<User> users) {
        int count = 0;
        
        for (User user : users) {
            try {
                List<String> tags = JSON.parseArray(user.getTags(), String.class);
                if (tags == null || tags.isEmpty()) {
                    continue;
                }
                
                // 使用 LinkedHashSet 去重并保持顺序
                Set<String> uniqueTags = new LinkedHashSet<>(tags);
                
                if (uniqueTags.size() < tags.size()) {
                    user.setTags(JSON.toJSONString(new ArrayList<>(uniqueTags)));
                    count++;
                    log.debug("用户 {} 去重: {} -> {}", user.getId(), tags.size(), uniqueTags.size());
                }
                
            } catch (Exception e) {
                log.error("用户 {} 标签去重失败: {}", user.getId(), e.getMessage());
            }
        }
        
        return count;
    }

    /**
     * 过滤无效标签
     */
    private int filterInvalidTags(List<User> users) {
        int count = 0;
        
        for (User user : users) {
            try {
                List<String> tags = JSON.parseArray(user.getTags(), String.class);
                if (tags == null || tags.isEmpty()) {
                    continue;
                }
                
                List<String> validTags = tags.stream()
                        .filter(tag -> tag != null && !tag.trim().isEmpty())
                        .filter(tag -> tag.length() >= 2 && tag.length() <= 30)
                        .map(String::trim)
                        .collect(Collectors.toList());
                
                if (validTags.size() < tags.size()) {
                    user.setTags(JSON.toJSONString(validTags));
                    count++;
                    log.debug("用户 {} 过滤无效标签: {} -> {}", user.getId(), tags.size(), validTags.size());
                }
                
            } catch (Exception e) {
                log.error("用户 {} 标签过滤失败: {}", user.getId(), e.getMessage());
            }
        }
        
        return count;
    }

    /**
     * 保存清洗后的数据
     */
    private void saveCleanedData(List<User> users) {
        for (User user : users) {
            try {
                userMapper.updateById(user);
            } catch (Exception e) {
                log.error("保存用户 {} 清洗后数据失败: {}", user.getId(), e.getMessage());
            }
        }
    }

    /**
     * 生成统计信息
     */
    private void generateStatistics(List<User> users) {
        log.info("========== 数据统计信息 ==========");
        
        // 1. 总用户数
        log.info("📊 总用户数: {}", users.size());
        
        // 2. 标签分布统计
        Map<String, Long> tagDistribution = new HashMap<>();
        
        for (User user : users) {
            try {
                List<String> tags = JSON.parseArray(user.getTags(), String.class);
                if (tags != null) {
                    for (String tag : tags) {
                        tagDistribution.merge(tag.toLowerCase(), 1L, Long::sum);
                    }
                }
            } catch (Exception e) {
                log.error("统计用户 {} 标签失败: {}", user.getId(), e.getMessage());
            }
        }
        
        // 3. 按使用频率排序
        List<Map.Entry<String, Long>> sortedTags = tagDistribution.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(20)
                .collect(Collectors.toList());
        
        log.info("🏆 Top 20 热门标签:");
        for (int i = 0; i < sortedTags.size(); i++) {
            Map.Entry<String, Long> entry = sortedTags.get(i);
            log.info("   {}. {} - {} 次", i + 1, entry.getKey(), entry.getValue());
        }
        
        // 4. 平均标签数
        double avgTags = users.stream()
                .mapToInt(user -> {
                    try {
                        List<String> tags = JSON.parseArray(user.getTags(), String.class);
                        return tags != null ? tags.size() : 0;
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .average()
                .orElse(0);
        
        log.info("📈 平均每用户标签数: {:.2f}", avgTags);
        
        // 5. 技术栈分类统计
        Map<String, Integer> categoryStats = categorizeTags(tagDistribution);
        log.info("📂 技术栈分类统计:");
        categoryStats.forEach((category, count) -> 
                log.info("   - {}: {} 个标签", category, count));
        
        log.info("====================================");
    }

    /**
     * 将标签分类
     */
    private Map<String, Integer> categorizeTags(Map<String, Long> tagDistribution) {
        Map<String, Integer> categories = new HashMap<>();
        
        Map<String, List<String>> categoryMap = new HashMap<>();
        categoryMap.put("后端开发", Arrays.asList("java", "spring", "spring-boot", "mybatis", "hibernate"));
        categoryMap.put("前端开发", Arrays.asList("javascript", "typescript", "vue", "react", "angular", "html", "css"));
        categoryMap.put("数据库", Arrays.asList("mysql", "postgres", "mongodb", "redis", "elasticsearch"));
        categoryMap.put("DevOps", Arrays.asList("docker", "kubernetes", "jenkins", "git", "linux", "nginx"));
        categoryMap.put("编程语言", Arrays.asList("python", "go", "rust", "cpp", "csharp", "php", "node.js"));
        
        for (Map.Entry<String, List<String>> entry : categoryMap.entrySet()) {
            String category = entry.getKey();
            int count = 0;
            
            for (String tech : entry.getValue()) {
                if (tagDistribution.containsKey(tech)) {
                    count += tagDistribution.get(tech).intValue();
                }
            }
            
            if (count > 0) {
                categories.put(category, count);
            }
        }
        
        return categories;
    }

    /**
     * 获取热门标签（供前端使用）
     */
    public List<Map<String, Object>> getPopularTags(int limit) {
        List<User> users = getUsersWithTags();
        Map<String, Long> tagDistribution = new HashMap<>();
        
        for (User user : users) {
            try {
                List<String> tags = JSON.parseArray(user.getTags(), String.class);
                if (tags != null) {
                    for (String tag : tags) {
                        tagDistribution.merge(tag.toLowerCase(), 1L, Long::sum);
                    }
                }
            } catch (Exception e) {
                log.error("统计标签失败: {}", e.getMessage());
            }
        }
        
        return tagDistribution.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> tagInfo = new HashMap<>();
                    tagInfo.put("name", entry.getKey());
                    tagInfo.put("count", entry.getValue());
                    return tagInfo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 根据标签搜索用户
     */
    public List<User> searchUsersByTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String searchTag = tag.toLowerCase().trim();
        List<User> allUsers = getUsersWithTags();
        
        return allUsers.stream()
                .filter(user -> {
                    try {
                        List<String> tags = JSON.parseArray(user.getTags(), String.class);
                        return tags != null && tags.stream()
                                .anyMatch(t -> t.toLowerCase().contains(searchTag));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }
}
