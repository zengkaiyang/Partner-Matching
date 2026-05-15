package com.zzkkyy.usercenter.crawler.processor;

import com.alibaba.fastjson.JSON;
import com.zzkkyy.usercenter.crawler.config.CrawlerNetworkConfig;
import com.zzkkyy.usercenter.crawler.model.CrawledUserData;
import com.zzkkyy.usercenter.crawler.pipeline.UserDataPipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GitHub用户标签爬虫处理器
 * 用于爬取GitHub用户的技能标签数据
 */
@Slf4j
public class GitHubUserTagProcessor implements PageProcessor {

    private final Site site = CrawlerNetworkConfig.createOptimizedSite();

    /**
     * 技术栈关键词列表（用于提取标签）
     */
    private static final List<String> TECH_KEYWORDS = Arrays.asList(
            "java", "python", "javascript", "typescript", "go", "rust", "cpp", "c",
            "spring", "springboot", "react", "vue", "angular", "nodejs",
            "mysql", "postgresql", "mongodb", "redis", "elasticsearch",
            "docker", "kubernetes", "aws", "azure", "gcp",
            "machine-learning", "deep-learning", "ai", "data-science",
            "blockchain", "microservices", "devops", "linux"
    );

    @Override
    public void process(Page page) {
        log.info("开始处理页面: {}", page.getUrl());

        // 如果是用户列表页，提取用户链接
        if (page.getUrl().regex("https://github\\.com/trending/.*").match()) {
            extractUserLinks(page);
        } 
        // 如果是用户详情页，提取用户信息和标签
        else if (page.getUrl().regex("https://github\\.com/[a-zA-Z0-9_-]+$").match()) {
            extractUserData(page);
        }
    }

    /**
     * 从趋势页面提取用户链接
     */
    private void extractUserLinks(Page page) {
        // 提取所有用户主页链接
        List<String> userLinks = page.getHtml()
                .xpath("//article[@class='Box-row']//h1/a/@href")
                .all();

        if (userLinks != null && !userLinks.isEmpty()) {
            log.info("发现 {} 个用户链接", userLinks.size());
            for (String link : userLinks) {
                String fullUrl = "https://github.com" + link;
                page.addTargetRequest(fullUrl);
            }
        }
    }

    /**
     * 从用户详情页提取用户数据和标签
     */
    private void extractUserData(Page page) {
        try {
            CrawledUserData userData = new CrawledUserData();

            // 提取用户名
            String username = page.getHtml()
                    .xpath("//span[@itemprop='additionalName']/text()")
                    .toString();
            
            if (StringUtils.isBlank(username)) {
                username = page.getUrl().toString().replaceAll("https://github.com/", "");
            }
            userData.setUsername(username);
            userData.setUserId(username);
            userData.setPlatform("GitHub");
            userData.setProfileUrl(page.getUrl().toString());

            // 提取头像
            String avatarUrl = page.getHtml()
                    .xpath("//img[contains(@class,'avatar-user')]/@src")
                    .toString();
            userData.setAvatarUrl(avatarUrl);

            // 提取简介
            String bio = page.getHtml()
                    .xpath("//div[@data-testid='user-profile-bio']/div/p/text()")
                    .toString();
            userData.setBio(bio);

            // 提取粉丝数、关注数、项目数
            try {
                String followersText = page.getHtml()
                        .xpath("//a[@href='/{username}?tab=followers']/span/text()")
                        .toString();
                userData.setFollowers(parseNumber(followersText));

                String followingText = page.getHtml()
                        .xpath("//a[@href='/{username}?tab=following']/span/text()")
                        .toString();
                userData.setFollowing(parseNumber(followingText));

                String repoText = page.getHtml()
                        .xpath("//a[@href='/{username}?tab=repositories']/span/text()")
                        .toString();
                userData.setRepositories(parseNumber(repoText));
            } catch (Exception e) {
                log.warn("提取统计数据失败: {}", e.getMessage());
            }

            // 提取技术标签（核心功能 - 必须包含tags）
            List<String> tags = extractTechTags(page);
            userData.setTags(tags);

            log.info("成功提取用户数据: {}, 标签数量: {}", username, tags.size());

            // 将结果传递给Pipeline
            page.putField("userData", userData);
        } catch (Exception e) {
            log.error("提取用户数据失败: {}, URL: {}", e.getMessage(), page.getUrl());
            // 即使失败也设置一个默认标签，避免数据丢失
            CrawledUserData userData = new CrawledUserData();
            userData.setUsername("unknown");
            userData.setTags(Arrays.asList("developer"));
            page.putField("userData", userData);
        }
    }

    /**
     * 从用户页面提取技术标签
     * 通过分析用户的仓库、贡献等信息提取技术栈
     */
    private List<String> extractTechTags(Page page) {
        List<String> tags = new ArrayList<>();

        try {
            // 方法1: 从用户仓库语言中提取
            List<String> languages = page.getHtml()
                    .xpath("//span[@itemprop='programmingLanguage']/text()")
                    .all();

            if (languages != null) {
                for (String lang : languages) {
                    String normalizedLang = normalizeTagName(lang);
                    if (!tags.contains(normalizedLang)) {
                        tags.add(normalizedLang);
                    }
                }
            }

            // 方法2: 从README或简介中匹配技术关键词
            String bio = page.getHtml()
                    .xpath("//div[@data-testid='user-profile-bio']//text()")
                    .toString();
            
            String readme = page.getHtml()
                    .xpath("//div[@id='readme']//text()")
                    .toString();

            String combinedText = (bio != null ? bio : "") + " " + (readme != null ? readme : "");
            
            for (String keyword : TECH_KEYWORDS) {
                if (combinedText.toLowerCase().contains(keyword.toLowerCase())) {
                    String tag = normalizeTagName(keyword);
                    if (!tags.contains(tag)) {
                        tags.add(tag);
                    }
                }
            }

            // 方法3: 从用户 pinned repositories 提取
            List<String> repoDescriptions = page.getHtml()
                    .xpath("//p[@itemprop='description']/text()")
                    .all();

            if (repoDescriptions != null) {
                for (String desc : repoDescriptions) {
                    for (String keyword : TECH_KEYWORDS) {
                        if (desc.toLowerCase().contains(keyword.toLowerCase())) {
                            String tag = normalizeTagName(keyword);
                            if (!tags.contains(tag)) {
                                tags.add(tag);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("提取标签失败: {}", e.getMessage(), e);
        }

        // 如果没有提取到标签，添加默认标签
        if (tags.isEmpty()) {
            tags.add("developer");
        }

        return tags;
    }

    /**
     * 标准化标签名称
     */
    private String normalizeTagName(String tag) {
        if (tag == null) return "";
        return tag.trim().toLowerCase().replace(" ", "-");
    }

    /**
     * 解析数字（处理 k, m 等单位）
     */
    private Integer parseNumber(String numberStr) {
        if (StringUtils.isBlank(numberStr)) {
            return 0;
        }
        
        try {
            numberStr = numberStr.trim().replace(",", "");
            
            if (numberStr.toLowerCase().endsWith("k")) {
                return (int) (Double.parseDouble(numberStr.replace("k", "")) * 1000);
            } else if (numberStr.toLowerCase().endsWith("m")) {
                return (int) (Double.parseDouble(numberStr.replace("m", "")) * 1000000);
            } else {
                return Integer.parseInt(numberStr);
            }
        } catch (Exception e) {
            return 0;
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
        GitHubUserTagProcessor processor = new GitHubUserTagProcessor();
        
        Spider.create(processor)
                .addUrl("https://github.com/trending/java")
                .addPipeline(new UserDataPipeline())
                .thread(5)
                .run();
    }
}
