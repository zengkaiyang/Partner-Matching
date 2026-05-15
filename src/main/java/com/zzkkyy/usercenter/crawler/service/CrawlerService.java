package com.zzkkyy.usercenter.crawler.service;

import com.zzkkyy.usercenter.crawler.config.CrawlerConfig;
import com.zzkkyy.usercenter.crawler.pipeline.UserDataPipeline;
import com.zzkkyy.usercenter.crawler.processor.GiteeApiUserProcessor;
import com.zzkkyy.usercenter.crawler.processor.GiteeUserTagProcessor;
import com.zzkkyy.usercenter.crawler.processor.QuotesAuthorProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import java.util.Arrays;

/**
 * 爬虫服务 - 管理爬虫的启动和停止
 */
@Slf4j
@Service
public class CrawlerService {

    @Autowired
    private CrawlerConfig crawlerConfig;

    @Autowired
    private UserDataPipeline userDataPipeline;

    private Spider spider;

    /**
     * 启动爬虫
     */
    public void startCrawler() {
        if (!crawlerConfig.isEnabled()) {
            log.warn("爬虫功能未启用");
            return;
        }

        log.info("开始启动爬虫...");

        GiteeUserTagProcessor processor = new GiteeUserTagProcessor();

        // 解析目标URL
        String[] urls = crawlerConfig.getTargetUrls().split(",");

        spider = Spider.create(processor)
                .addUrl(urls)
                .addPipeline(userDataPipeline)
                .thread(crawlerConfig.getThreadCount());

        // 异步启动爬虫
        spider.start();

        log.info("爬虫已启动，目标URL: {}", Arrays.toString(urls));
    }

    /**
     * 同步启动爬虫（阻塞直到完成）
     */
    public void startCrawlerSync() {
        if (!crawlerConfig.isEnabled()) {
            log.warn("⚠️ 爬虫功能未启用");
            return;
        }

        log.info("========================================");
        log.info("🚀 开始启动爬虫（同步模式）...");
        log.info("   - 爬取目标: Quotes to Scrape (练习用网站)");
        log.info("   - 说明: 无防爬机制，稳定可靠");
        log.info("   - 线程数: {}", crawlerConfig.getThreadCount());
        log.info("   - 重试次数: {}", crawlerConfig.getRetryTimes());
        log.info("   - 爬取间隔: {}ms", crawlerConfig.getSleepTime());
        log.info("========================================");

        QuotesAuthorProcessor processor = new QuotesAuthorProcessor();

        String startUrl = "http://quotes.toscrape.com/";

        spider = Spider.create(processor)
                .addUrl(startUrl)
                .addPipeline(userDataPipeline)
                .thread(crawlerConfig.getThreadCount());  // 使用配置的线程数

        log.info("✅ Spider 创建成功，开始执行爬取任务...");
        log.info("   - 目标URL: {}", startUrl);
        
        // 同步启动爬虫
        spider.run();

        log.info("========================================");
        log.info("✅ 爬虫已完成");
        log.info("========================================");
    }

    /**
     * 停止爬虫
     */
    public void stopCrawler() {
        if (spider != null) {
            spider.stop();
            log.info("爬虫已停止");
        }
    }

    /**
     * 获取爬虫状态
     */
    public String getCrawlerStatus() {
        if (spider == null) {
            return "未启动";
        }
        return spider.getStatus().toString();
    }
}
