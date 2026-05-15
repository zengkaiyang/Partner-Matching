package com.zzkkyy.usercenter.crawler.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zzkkyy.usercenter.crawler.config.CrawlerNetworkConfig;
import com.zzkkyy.usercenter.crawler.model.CrawledUserData;
import com.zzkkyy.usercenter.crawler.pipeline.UserDataPipeline;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Gitee API 用户爬虫
 * 使用 Gitee 公开 API 获取用户数据，避免 HTML 解析问题
 */
@Slf4j
public class GiteeApiUserProcessor implements PageProcessor {

    private final Site site = CrawlerNetworkConfig.createOptimizedSite()
            .addHeader("Accept", "application/json")
            .setCharset("UTF-8");

    /**
     * 技术栈关键词列表
     */
    private static final List<String> TECH_KEYWORDS = Arrays.asList(
            "java", "python", "javascript", "typescript", "go", "rust",
            "spring", "vue", "react", "nodejs",
            "mysql", "postgresql", "mongodb", "redis",
            "docker", "kubernetes", "linux"
    );

    @Override
    public void process(Page page) {
        log.info("========== 开始处理 API 页面: {} ==========", page.getUrl());

        try {
            String rawText = page.getRawText();
            log.info("API 返回数据长度: {}", rawText != null ? rawText.length() : 0);
            
            if (rawText == null || rawText.isEmpty()) {
                log.error("❌ API 返回数据为空");
                return;
            }

            // 尝试解析为 JSON 数组
            JSONArray usersArray;
            try {
                usersArray = JSON.parseArray(rawText);
            } catch (Exception e) {
                log.error("❌ 解析 JSON 失败: {}", e.getMessage());
                log.debug("原始数据: {}", rawText.substring(0, Math.min(200, rawText.length())));
                return;
            }

            if (usersArray == null || usersArray.isEmpty()) {
                log.warn("⚠️ API 返回的用户列表为空");
                return;
            }

            log.info("✅ 成功解析到 {} 个用户", usersArray.size());

            // 遍历每个用户
            for (int i = 0; i < usersArray.size(); i++) {
                JSONObject userJson = usersArray.getJSONObject(i);
                
                try {
                    CrawledUserData userData = extractUserFromJson(userJson);
                    
                    if (userData != null && userData.getUsername() != null) {
                        log.info("✅ 提取用户 [{}/{}]: {}", i + 1, usersArray.size(), userData.getUsername());
                        log.info("   - 用户名: {}", userData.getUsername());
                        log.info("   - 平台: {}", userData.getPlatform());
                        log.info("   - 标签数量: {}", userData.getTags() != null ? userData.getTags().size() : 0);
                        log.info("   - 标签列表: {}", userData.getTags());
                        
                        // 将结果传递给 Pipeline
                        page.putField("user_" + i, userData);
                    }
                } catch (Exception e) {
                    log.error("❌ 处理第 {} 个用户失败: {}", i + 1, e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            log.error("❌ 处理页面失败: {}", e.getMessage(), e);
        }
        
        log.info("========== API 页面处理完成 ==========");
    }

    /**
     * 从 JSON 对象中提取用户数据
     */
    private CrawledUserData extractUserFromJson(JSONObject userJson) {
        if (userJson == null) {
            return null;
        }

        CrawledUserData userData = new CrawledUserData();
        
        // 提取基本信息
        String username = userJson.getString("login");
        String name = userJson.getString("name");
        String avatarUrl = userJson.getString("avatar_url");
        String blog = userJson.getString("blog");
        String bio = userJson.getString("bio");
        
        userData.setUsername(username != null ? username : name);
        userData.setUserId(username);
        userData.setPlatform("Gitee");
        userData.setProfileUrl(userJson.getString("html_url"));
        userData.setAvatarUrl(avatarUrl);
        userData.setBio(bio);
        
        // 提取统计数据
        userData.setFollowers(userJson.getInteger("followers"));
        userData.setFollowing(userJson.getInteger("following"));
        userData.setRepositories(userJson.getInteger("public_repos"));
        
        // 生成技术标签
        List<String> tags = generateTagsFromUserInfo(userJson);
        userData.setTags(tags);
        
        return userData;
    }

    /**
     * 根据用户信息生成技术标签
     */
    private List<String> generateTagsFromUserInfo(JSONObject userJson) {
        List<String> tags = new ArrayList<>();
        
        // 从简介中提取标签
        String bio = userJson.getString("bio");
        if (bio != null && !bio.isEmpty()) {
            String bioLower = bio.toLowerCase();
            for (String keyword : TECH_KEYWORDS) {
                if (bioLower.contains(keyword)) {
                    tags.add(keyword);
                }
            }
        }
        
        // 从博客URL中提取标签
        String blog = userJson.getString("blog");
        if (blog != null && !blog.isEmpty()) {
            String blogLower = blog.toLowerCase();
            for (String keyword : TECH_KEYWORDS) {
                if (blogLower.contains(keyword)) {
                    if (!tags.contains(keyword)) {
                        tags.add(keyword);
                    }
                }
            }
        }
        
        // 如果没有提取到标签，添加默认标签
        if (tags.isEmpty()) {
            tags.add("developer");
        }
        
        return tags;
    }

    @Override
    public Site getSite() {
        return site;
    }

    /**
     * 启动爬虫 - 使用 Gitee API
     */
    public static void main(String[] args) {
        GiteeApiUserProcessor processor = new GiteeApiUserProcessor();
        
        // 使用 Gitee API 获取用户列表
        // API 文档: https://gitee.com/api/v5/swagger#/getV5Users
        String apiUrl = "https://gitee.com/api/v5/users?page=1&per_page=10";
        
        log.info("开始爬取 Gitee API: {}", apiUrl);
        
        Spider.create(processor)
                .addUrl(apiUrl)
                .addPipeline(new UserDataPipeline())
                .thread(1)
                .run();
    }
}
