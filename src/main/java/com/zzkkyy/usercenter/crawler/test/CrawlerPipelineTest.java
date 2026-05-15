package com.zzkkyy.usercenter.crawler.test;

import com.zzkkyy.usercenter.crawler.pipeline.UserDataPipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 爬虫数据管道测试
 * 用于验证 UserDataPipeline 是否正确注入 UserMapper
 */
@Slf4j
@Component
public class CrawlerPipelineTest implements CommandLineRunner {

    @Autowired
    private UserDataPipeline userDataPipeline;

    @Override
    public void run(String... args) throws Exception {
        log.info("========== 开始测试 UserDataPipeline ==========");
        
        if (userDataPipeline == null) {
            log.error("❌ UserDataPipeline 未注入！");
        } else {
            log.info("✅ UserDataPipeline 注入成功");
            
            // 通过反射检查 userMapper 是否为 null
            try {
                java.lang.reflect.Field field = UserDataPipeline.class.getDeclaredField("userMapper");
                field.setAccessible(true);
                Object userMapper = field.get(userDataPipeline);
                
                if (userMapper == null) {
                    log.error("❌ UserMapper 为 null！这会导致数据无法保存到数据库");
                } else {
                    log.info("✅ UserMapper 注入成功，可以正常保存数据");
                }
            } catch (Exception e) {
                log.error("检查 UserMapper 失败: {}", e.getMessage(), e);
            }
        }
        
        log.info("========== UserDataPipeline 测试完成 ==========");
    }
}
