package com.zzkkyy.usercenter.crawler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 爬虫配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "crawler")
public class CrawlerConfig {
    
    /**
     * 爬取的目标URL列表
     */
    private String targetUrls = "https://github.com/trending/java,https://github.com/trending/python";
    
    /**
     * 线程数
     */
    private int threadCount = 5;
    
    /**
     * 重试次数
     */
    private int retryTimes = 3;
    
    /**
     * 爬取间隔时间（毫秒）
     */
    private long sleepTime = 1000;
    
    /**
     * 是否启用爬虫
     */
    private boolean enabled = true;
}
