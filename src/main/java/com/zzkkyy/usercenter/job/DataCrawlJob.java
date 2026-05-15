package com.zzkkyy.usercenter.job;

import com.zzkkyy.usercenter.crawler.service.CrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 数据爬取定时任务
 * 每天凌晨2点执行爬虫任务
 */
@Slf4j
@Component
public class DataCrawlJob {

    @Autowired
    private CrawlerService crawlerService;

    /**
     * 定时爬取用户标签数据
     * cron表达式: 秒 分 时 日 月 周
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void crawlUserTags() {
        log.info("========== 开始执行用户标签爬取任务 ==========");
        
        try {
            // 启动爬虫
            crawlerService.startCrawlerSync();
            
            log.info("========== 用户标签爬取任务完成 ==========");
        } catch (Exception e) {
            log.error("用户标签爬取任务失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 测试方法：立即执行一次爬取（用于测试）
     * 可以通过调用此方法手动触发爬取
     */
    public void executeCrawlNow() {
        log.info("========== 手动触发用户标签爬取任务 ==========");
        
        try {
            crawlerService.startCrawlerSync();
            log.info("========== 手动爬取任务完成 ==========");
        } catch (Exception e) {
            log.error("手动爬取任务失败: {}", e.getMessage(), e);
        }
    }
}
