package com.zzkkyy.usercenter.crawler.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.util.HashMap;
import java.util.Map;

/**
 * 爬虫网络配置
 * 优化HTTP连接，避免超时问题
 */
@Slf4j
@Configuration
public class CrawlerNetworkConfig {

    /**
     * 配置优化的HTTP下载器
     */
    @Bean
    public HttpClientDownloader httpClientDownloader() {
        HttpClientDownloader downloader = new HttpClientDownloader();
        
        // 设置代理池（如果有代理的话）
        // downloader.setProxyProvider(...);
        
        log.info("HttpClientDownloader 配置完成");
        return downloader;
    }

    /**
     * 创建优化的Site配置
     */
    public static Site createOptimizedSite() {
        return Site.me()
                .setRetryTimes(3)              // 重试3次（从5次降低）
                .setSleepTime(500)             // 每次请求间隔500ms（从2000ms降低）
                .setTimeOut(15000)             // 超时时间15秒（从30秒降低）
                .setCycleRetryTimes(2)         // 循环重试2次（从3次降低）
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .addHeader("Accept-Language", "en-US,en;q=0.5")
                .addHeader("Connection", "keep-alive")
                .addHeader("Cache-Control", "max-age=0")
                .setCharset("UTF-8");          // 设置字符编码
    }
}
