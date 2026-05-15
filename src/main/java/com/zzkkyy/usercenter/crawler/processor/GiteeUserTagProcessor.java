package com.zzkkyy.usercenter.crawler.processor;

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
 * Gitee用户标签爬虫处理器
 * 用于爬取Gitee（码云）用户的技能标签数据
 * 国内访问速度快，稳定性好
 */
@Slf4j
public class GiteeUserTagProcessor implements PageProcessor {

    private final Site site = CrawlerNetworkConfig.createOptimizedSite()
            .addHeader("Referer", "https://gitee.com/");

    /**
     * 技术栈关键词列表（用于提取标签）
     */
    private static final List<String> TECH_KEYWORDS = Arrays.asList(
            "java", "python", "javascript", "typescript", "go", "rust", "cpp", "c",
            "spring", "springboot", "vue", "react", "angular", "nodejs",
            "mysql", "postgresql", "mongodb", "redis", "elasticsearch",
            "docker", "kubernetes", "linux", "nginx",
            "machine-learning", "deep-learning", "ai", "data-science",
            "microservices", "devops", "git", "maven"
    );

    @Override
    public void process(Page page) {
        log.info("========== 开始处理页面: {} ==========", page.getUrl());

        // 如果是用户列表页，提取用户链接
        if (page.getUrl().regex("https://gitee\\.com/explore/.*").match()) {
            log.info("检测到探索页面，开始提取用户链接...");
            extractUserLinks(page);
        } 
        // 如果是用户详情页，提取用户信息和标签
        else if (page.getUrl().regex("https://gitee\\.com/[a-zA-Z0-9_-]+$").match()) {
            log.info("检测到用户详情页面，开始提取用户数据...");
            extractUserData(page);
        } else {
            log.warn("未知页面类型: {}", page.getUrl());
        }
    }

    /**
     * 从探索页面提取用户链接
     */
    private void extractUserLinks(Page page) {
        try {
            // 提取所有用户主页链接
            List<String> userLinks = page.getHtml()
                    .xpath("//div[@class='user-list']//a[contains(@href, '/u/') or contains(@href, '/')]/@href")
                    .all();

            if (userLinks != null && !userLinks.isEmpty()) {
                log.info("✅ 发现 {} 个用户链接", userLinks.size());
                log.info("用户链接列表: {}", userLinks.subList(0, Math.min(5, userLinks.size())));
                
                int addedCount = 0;
                for (String link : userLinks) {
                    // 过滤并构建完整的用户URL
                    if (link.startsWith("/")) {
                        String fullUrl = "https://gitee.com" + link;
                        // 避免重复添加
                        if (!page.getTargetRequests().stream()
                                .anyMatch(r -> r.toString().equals(fullUrl))) {
                            page.addTargetRequest(fullUrl);
                            addedCount++;
                        }
                    }
                }
                log.info("✅ 成功添加 {} 个新任务到爬取队列", addedCount);
            } else {
                log.warn("⚠️ 未找到任何用户链接，请检查 XPath 是否正确");
                log.debug("页面HTML片段: {}", page.getHtml().toString().substring(0, Math.min(500, page.getHtml().toString().length())));
            }
        } catch (Exception e) {
            log.error("❌ 提取用户链接失败: {}", e.getMessage(), e);
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
                    .xpath("//div[@class='user-info']//h2/text() | //div[@class='name']/text()")
                    .toString();
            
            if (StringUtils.isBlank(username)) {
                // 从URL中提取用户名
                username = page.getUrl().toString()
                        .replaceAll("https://gitee.com/", "")
                        .replaceAll("/", "");
            }
            
            userData.setUsername(username);
            userData.setUserId(username);
            userData.setPlatform("Gitee");
            userData.setProfileUrl(page.getUrl().toString());

            // 提取头像
            String avatarUrl = page.getHtml()
                    .xpath("//img[@class='ui image avatar' or contains(@class, 'avatar')]/@src")
                    .toString();
            
            if (StringUtils.isBlank(avatarUrl)) {
                // 如果没找到头像，使用默认头像
                avatarUrl = "https://gitee.com/assets/no_portrait.png";
            }
            userData.setAvatarUrl(avatarUrl);

            // 提取简介
            String bio = page.getHtml()
                    .xpath("//div[@class='bio']/text() | //p[@class='desc']/text()")
                    .toString();
            userData.setBio(bio);

            // 提取关注数、粉丝数等（Gitee的HTML结构可能不同，这里做容错处理）
            try {
                String followersText = page.getHtml()
                        .xpath("//a[contains(text(), '粉丝')]/span/text() | //span[contains(text(), '粉丝')]/preceding-sibling::span/text()")
                        .toString();
                userData.setFollowers(parseNumber(followersText));

                String followingText = page.getHtml()
                        .xpath("//a[contains(text(), '关注')]/span/text() | //span[contains(text(), '关注')]/preceding-sibling::span/text()")
                        .toString();
                userData.setFollowing(parseNumber(followingText));
            } catch (Exception e) {
                log.warn("提取统计数据失败: {}", e.getMessage());
                userData.setFollowers(0);
                userData.setFollowing(0);
            }

            // 提取技术标签（核心功能 - 必须包含tags）
            List<String> tags = extractTechTags(page);
            userData.setTags(tags);

            log.info("✅ 成功提取用户数据: {}");
            log.info("   - 用户名: {}", username);
            log.info("   - 平台: {}", userData.getPlatform());
            log.info("   - 头像: {}", userData.getAvatarUrl() != null ? "有" : "无");
            log.info("   - 简介: {}", userData.getBio() != null ? userData.getBio().substring(0, Math.min(50, userData.getBio().length())) : "无");
            log.info("   - 标签数量: {}", tags.size());
            log.info("   - 标签列表: {}", tags);
            log.info("   - 粉丝数: {}", userData.getFollowers());
            log.info("   - 关注数: {}", userData.getFollowing());

            // 将结果传递给Pipeline
            page.putField("userData", userData);
            log.info("✅ 用户数据已放入 Pipeline，等待保存...");
        } catch (Exception e) {
            log.error("提取用户数据失败: {}, URL: {}", e.getMessage(), page.getUrl());
            // 即使失败也设置一个默认标签，避免数据丢失
            CrawledUserData userData = new CrawledUserData();
            String username = page.getUrl().toString()
                    .replaceAll("https://gitee.com/", "")
                    .replaceAll("/", "");
            userData.setUsername(StringUtils.isBlank(username) ? "unknown" : username);
            userData.setTags(Arrays.asList("developer"));
            userData.setPlatform("Gitee");
            page.putField("userData", userData);
        }
    }

    /**
     * 从用户页面提取技术标签
     * 通过分析用户的项目、贡献等信息提取技术栈
     */
    private List<String> extractTechTags(Page page) {
        List<String> tags = new ArrayList<>();

        try {
            // 方法1: 从用户项目语言中提取
            List<String> languages = page.getHtml()
                    .xpath("//span[@class='language-color']/following-sibling::span/text() | //span[contains(@class, 'lang')]/text()")
                    .all();

            if (languages != null && !languages.isEmpty()) {
                for (String lang : languages) {
                    String normalizedLang = normalizeTagName(lang);
                    if (!tags.contains(normalizedLang) && !normalizedLang.isEmpty()) {
                        tags.add(normalizedLang);
                    }
                }
            }

            // 方法2: 从个人简介中匹配技术关键词
            String bio = page.getHtml()
                    .xpath("//div[@class='bio']/text() | //p[@class='desc']/text()")
                    .toString();
            
            String about = page.getHtml()
                    .xpath("//div[@class='about']/text() | //div[contains(@class, 'profile')]/text()")
                    .toString();

            String combinedText = (bio != null ? bio : "") + " " + (about != null ? about : "");
            
            for (String keyword : TECH_KEYWORDS) {
                if (combinedText.toLowerCase().contains(keyword.toLowerCase())) {
                    String tag = normalizeTagName(keyword);
                    if (!tags.contains(tag)) {
                        tags.add(tag);
                    }
                }
            }

            // 方法3: 从用户置顶项目中提取
            List<String> projectNames = page.getHtml()
                    .xpath("//div[@class='project-list']//a[@class='title']/text()")
                    .all();

            if (projectNames != null) {
                for (String projectName : projectNames) {
                    for (String keyword : TECH_KEYWORDS) {
                        if (projectName.toLowerCase().contains(keyword.toLowerCase())) {
                            String tag = normalizeTagName(keyword);
                            if (!tags.contains(tag)) {
                                tags.add(tag);
                            }
                        }
                    }
                }
            }

            // 方法4: 从用户贡献的项目描述中提取
            List<String> descriptions = page.getHtml()
                    .xpath("//p[@class='description']/text() | //div[@class='desc']/text()")
                    .all();

            if (descriptions != null) {
                for (String desc : descriptions) {
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
            } else if (numberStr.toLowerCase().endsWith("w")) {
                // 中文"万"
                return (int) (Double.parseDouble(numberStr.replace("w", "")) * 10000);
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
        GiteeUserTagProcessor processor = new GiteeUserTagProcessor();
        
        Spider.create(processor)
                .addUrl("https://gitee.com/explore/all")
                .addPipeline(new UserDataPipeline())
                .thread(3)
                .run();
    }
}
