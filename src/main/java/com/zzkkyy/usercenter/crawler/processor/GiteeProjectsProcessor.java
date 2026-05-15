package com.zzkkyy.usercenter.crawler.processor;

import com.zzkkyy.usercenter.crawler.config.CrawlerNetworkConfig;
import com.zzkkyy.usercenter.crawler.model.CrawledUserData;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Gitee 热门项目爬虫
 * 通过爬取 Gitee 热门项目页面，获取项目作者信息
 */
@Slf4j
public class GiteeProjectsProcessor implements PageProcessor {

    private final Site site = CrawlerNetworkConfig.createOptimizedSite()
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
        log.info("========== 开始处理页面: {} ==========", page.getUrl());

        try {
            String html = page.getHtml().toString();
            
            // 尝试解析 HTML
            Document doc = Jsoup.parse(html);
            
            // 查找项目卡片
            Elements projectCards = doc.select(".project-item, .repo-item, [class*='project'], [class*='repo']");
            
            if (projectCards.isEmpty()) {
                log.warn("⚠️ 未找到项目卡片，尝试其他选择器...");
                
                // 尝试更通用的选择器
                projectCards = doc.select("a[href*='/'][href*='-']");
                log.info("使用通用选择器找到 {} 个链接", projectCards.size());
            }
            
            log.info("✅ 找到 {} 个项目卡片", projectCards.size());
            
            int userCount = 0;
            for (Element card : projectCards) {
                try {
                    CrawledUserData userData = extractUserFromProject(card);
                    
                    if (userData != null && userData.getUsername() != null) {
                        userCount++;
                        log.info("✅ 提取用户 [{}/{}]: {}", userCount, projectCards.size(), userData.getUsername());
                        log.info("   - 用户名: {}", userData.getUsername());
                        log.info("   - 平台: {}", userData.getPlatform());
                        log.info("   - 标签数量: {}", userData.getTags() != null ? userData.getTags().size() : 0);
                        log.info("   - 标签列表: {}", userData.getTags());
                        
                        // 将结果传递给 Pipeline
                        page.putField("user_" + userCount, userData);
                    }
                } catch (Exception e) {
                    log.debug("处理项目卡片失败: {}", e.getMessage());
                }
            }
            
            log.info("✅ 总共提取 {} 个用户", userCount);
            
        } catch (Exception e) {
            log.error("❌ 处理页面失败: {}", e.getMessage(), e);
        }
        
        log.info("========== 页面处理完成 ==========");
    }

    /**
     * 从项目卡片中提取用户信息
     */
    private CrawledUserData extractUserFromProject(Element card) {
        try {
            CrawledUserData userData = new CrawledUserData();
            
            // 尝试提取用户名（从链接中）
            Element authorLink = card.selectFirst("a[href^='/'][href*='/']");
            if (authorLink != null) {
                String href = authorLink.attr("href");
                // 从 URL 中提取用户名，例如 /username/project -> username
                String[] parts = href.split("/");
                if (parts.length >= 2) {
                    String username = parts[1];
                    if (!username.isEmpty() && !username.equals("explore") && !username.equals("organizations")) {
                        userData.setUsername(username);
                        userData.setUserId(username);
                        userData.setProfileUrl("https://gitee.com" + href);
                    }
                }
            }
            
            // 如果没有用户名，跳过
            if (userData.getUsername() == null || userData.getUsername().isEmpty()) {
                return null;
            }
            
            userData.setPlatform("Gitee");
            
            // 提取项目名称作为参考
            Element titleElement = card.selectFirst("h3, h4, .title, [class*='title']");
            if (titleElement != null) {
                String projectName = titleElement.text();
                userData.setBio("项目: " + projectName);
            }
            
            // 生成技术标签
            List<String> tags = generateTagsFromElement(card);
            userData.setTags(tags);
            
            return userData;
            
        } catch (Exception e) {
            log.debug("提取用户信息失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 HTML 元素中生成技术标签
     */
    private List<String> generateTagsFromElement(Element element) {
        List<String> tags = new ArrayList<>();
        
        String text = element.text().toLowerCase();
        String html = element.html().toLowerCase();
        String combined = text + " " + html;
        
        // 从文本和 HTML 中匹配技术关键词
        for (String keyword : TECH_KEYWORDS) {
            if (combined.contains(keyword)) {
                tags.add(keyword);
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
     * 启动爬虫 - 爬取 Gitee 热门项目
     */
    public static void main(String[] args) {
        GiteeProjectsProcessor processor = new GiteeProjectsProcessor();
        
        // 爬取 Gitee 探索页面
        String exploreUrl = "https://gitee.com/explore/all";
        
        log.info("开始爬取 Gitee 探索页面: {}", exploreUrl);
        
        Spider.create(processor)
                .addUrl(exploreUrl)
                .thread(1)
                .run();
    }
}
