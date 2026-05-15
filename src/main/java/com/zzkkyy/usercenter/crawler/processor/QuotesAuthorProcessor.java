package com.zzkkyy.usercenter.crawler.processor;

import com.zzkkyy.usercenter.crawler.config.CrawlerNetworkConfig;
import com.zzkkyy.usercenter.crawler.model.CrawledUserData;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Quotes 网站用户爬虫
 * 爬取 http://quotes.toscrape.com 网站的作者信息
 * 这是一个专门用于爬虫练习的网站，无防爬机制
 */
@Slf4j
public class QuotesAuthorProcessor implements PageProcessor {

    private final Site site = CrawlerNetworkConfig.createOptimizedSite()
            .setCharset("UTF-8");

    /**
     * 已爬取的作者 URL 集合（用于去重）
     */
    private static final Set<String> crawledUrls = ConcurrentHashMap.newKeySet();

    @Override
    public void process(Page page) {
        log.info("========== 开始处理页面: {} ==========", page.getUrl());

        try {
            // 先判断是否是作者详情页（优先级更高）
            if (page.getUrl().regex("http://quotes\\.toscrape\\.com/author/.+").match()) {
                String authorUrl = page.getUrl().toString();
                
                // 检查是否已经爬取过
                if (crawledUrls.contains(authorUrl)) {
                    log.info("⏭️ 跳过已爬取的作者: {}", authorUrl);
                    return;
                }
                
                // 标记为已爬取
                crawledUrls.add(authorUrl);
                log.info("🆕 新作者页面，开始提取数据...");
                
                extractAuthorDetails(page);
            }
            // 再判断是否是首页或列表页
            else if (page.getUrl().regex("http://quotes\\.toscrape\\.com/.*").match()) {
                extractAuthors(page);
            }
            
        } catch (Exception e) {
            log.error("❌ 处理页面失败: {}", e.getMessage(), e);
        }
        
        log.info("========== 页面处理完成 ==========");
    }

    /**
     * 从首页提取作者链接
     */
    private void extractAuthors(Page page) {
        log.info("检测到首页，开始提取作者链接...");
        
        int addedCount = 0;
        
        // 备用方案：直接提取所有包含 /author/ 的链接（更可靠）
        List<String> allLinks = page.getHtml()
                .links()
                .all();
        
        if (allLinks != null && !allLinks.isEmpty()) {
            log.info("✅ 页面共有 {} 个链接", allLinks.size());
            
            for (String link : allLinks) {
                // 只提取作者详情页链接
                if (link.contains("/author/") && !link.endsWith("/author/")) {
                    // 避免重复添加
                    if (!page.getTargetRequests().stream()
                            .anyMatch(r -> r.toString().equals(link))) {
                        page.addTargetRequest(link);
                        addedCount++;
                        
                        // 限制最多添加 20 个作者
                        if (addedCount >= 20) {
                            break;
                        }
                    }
                }
            }
            
            log.info("✅ 成功添加 {} 个作者链接到爬取队列", addedCount);
        } else {
            log.warn("⚠️ 未找到任何链接");
        }
        
        // 如果有下一页，继续爬取
        String nextUrl = page.getHtml()
                .xpath("//li[@class='next']/a/@href")
                .toString();
        
        if (nextUrl != null && !nextUrl.isEmpty()) {
            String fullNextUrl = "http://quotes.toscrape.com" + nextUrl;
            page.addTargetRequest(fullNextUrl);
            log.info("✅ 发现下一页: {}", fullNextUrl);
        }
    }

    /**
     * 提取作者详细信息
     */
    private void extractAuthorDetails(Page page) {
        log.info("检测到作者详情页，开始提取作者数据...");
        
        try {
            CrawledUserData userData = new CrawledUserData();
            
            // 提取作者名字
            String username = page.getHtml()
                    .xpath("//h3[@class='author-title']/text()")
                    .toString();
            
            if (username == null || username.isEmpty()) {
                log.warn("⚠️ 未找到作者名字");
                return;
            }
            
            userData.setUsername(username.trim());
            userData.setUserId(username.trim());
            userData.setPlatform("Quotes");
            
            // 提取作者简介
            String bio = page.getHtml()
                    .xpath("//div[@class='author-description']/p/text()")
                    .toString();
            userData.setBio(bio != null ? bio.trim() : "暂无简介");
            
            // 提取出生日期
            String birthDate = page.getHtml()
                    .xpath("//span[@class='author-born-date']/text()")
                    .toString();
            
            // 提取出生地
            String birthPlace = page.getHtml()
                    .xpath("//span[@class='author-born-location']/text()")
                    .toString();
            
            if (birthDate != null && birthPlace != null) {
                userData.setBio(userData.getBio() + " | 出生于: " + birthDate.trim() + ", " + birthPlace.trim());
            }
            
            // 提取作者的所有标签（从引言中）
            List<String> tags = page.getHtml()
                    .xpath("//div[@class='tags']/a[@class='tag']/text()")
                    .all();
            
            if (tags == null || tags.isEmpty()) {
                tags = Arrays.asList("writer", "author");
            } else {
                // 去重并转换为小写
                Set<String> uniqueTags = new HashSet<>();
                for (String tag : tags) {
                    if (tag != null && !tag.isEmpty()) {
                        uniqueTags.add(tag.trim().toLowerCase().replace(" ", "-"));
                    }
                }
                tags = new ArrayList<>(uniqueTags);
            }
            
            userData.setTags(tags);
            
            // 设置头像（使用随机头像）
            userData.setAvatarUrl(com.zzkkyy.usercenter.utils.AvatarUtils.getRandomAvatar());
            
            // 设置统计数据（模拟）
            Random random = new Random();
            userData.setFollowers(random.nextInt(1000) + 100);
            userData.setFollowing(random.nextInt(100) + 10);
            userData.setRepositories(random.nextInt(20) + 1);
            
            log.info("✅ 成功提取作者数据:");
            log.info("   - 用户名: {}", userData.getUsername());
            log.info("   - 平台: {}", userData.getPlatform());
            log.info("   - 简介: {}", userData.getBio().substring(0, Math.min(50, userData.getBio().length())));
            log.info("   - 标签数量: {}", tags.size());
            log.info("   - 标签列表: {}", tags);
            
            // 将结果传递给 Pipeline
            page.putField("userData", userData);
            log.info("✅ 作者数据已放入 Pipeline，等待保存...");
            
            // 输出爬取统计
            log.info("📊 当前已爬取作者数量: {}", crawledUrls.size());
            
        } catch (Exception e) {
            log.error("❌ 提取作者详情失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    /**
     * 启动爬虫
     */
    public static void main(String[] args) {
        QuotesAuthorProcessor processor = new QuotesAuthorProcessor();
        
        String startUrl = "http://quotes.toscrape.com/";
        
        log.info("开始爬取 Quotes 网站: {}", startUrl);
        
        Spider.create(processor)
                .addUrl(startUrl)
                .thread(2)
                .run();
    }
}
