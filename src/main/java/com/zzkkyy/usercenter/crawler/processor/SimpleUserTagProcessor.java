package com.zzkkyy.usercenter.crawler.processor;

import com.zzkkyy.usercenter.crawler.model.CrawledUserData;
import com.zzkkyy.usercenter.crawler.pipeline.UserDataPipeline;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.Arrays;
import java.util.List;

/**
 * 简单用户标签爬虫 - 用于测试和演示
 * 爬取模拟的用户数据，包含tags标签
 */
@Slf4j
public class SimpleUserTagProcessor implements PageProcessor {

    private final Site site = Site.me()
            .setRetryTimes(5)
            .setSleepTime(2000)
            .setTimeOut(30000);

    @Override
    public void process(Page page) {
        log.info("开始处理页面: {}", page.getUrl());

        // 创建模拟的用户数据（实际使用时可以替换为真实爬取逻辑）
        CrawledUserData userData = new CrawledUserData();
        
        // 从URL中提取用户名（模拟）
        String url = page.getUrl().toString();
        String username = extractUsername(url);
        
        userData.setUsername(username);
        userData.setUserId(username);
        userData.setPlatform("Test");
        userData.setProfileUrl(url);
        userData.setAvatarUrl("https://avatars.githubusercontent.com/u/1?v=4");
        userData.setBio("这是一个测试用户的简介");
        userData.setFollowers(100);
        userData.setFollowing(50);
        userData.setRepositories(20);

        // 核心功能：设置用户标签（必须包含tags）
        List<String> tags = generateUserTags(username);
        userData.setTags(tags);

        log.info("成功提取用户数据: {}, 标签: {}", username, tags);

        // 将结果传递给Pipeline
        page.putField("userData", userData);
    }

    /**
     * 从URL提取用户名
     */
    private String extractUsername(String url) {
        if (url.contains("user")) {
            return "test-user-" + (int)(Math.random() * 1000);
        }
        return "unknown-user";
    }

    /**
     * 根据用户名生成技术标签
     */
    private List<String> generateUserTags(String username) {
        // 模拟不同的技术栈
        List<List<String>> techStacks = Arrays.asList(
                Arrays.asList("java", "spring", "mysql", "redis"),
                Arrays.asList("python", "django", "postgresql", "docker"),
                Arrays.asList("javascript", "react", "nodejs", "mongodb"),
                Arrays.asList("go", "microservices", "kubernetes", "aws"),
                Arrays.asList("typescript", "vue", "elasticsearch", "linux")
        );
        
        // 根据用户名的hash值选择一个技术栈
        int index = Math.abs(username.hashCode()) % techStacks.size();
        return techStacks.get(index);
    }

    @Override
    public Site getSite() {
        return site;
    }

    /**
     * 启动测试爬虫
     */
    public static void main(String[] args) {
        SimpleUserTagProcessor processor = new SimpleUserTagProcessor();
        
        Spider.create(processor)
                .addUrl("http://test.com/user1")
                .addUrl("http://test.com/user2")
                .addUrl("http://test.com/user3")
                .addPipeline(new UserDataPipeline())
                .thread(3)
                .run();
    }
}
