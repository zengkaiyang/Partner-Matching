package com.zzkkyy.usercenter.crawler.pipeline;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzkkyy.usercenter.crawler.model.CrawledUserData;
import com.zzkkyy.usercenter.mapper.UserMapper;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.utils.AvatarUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Date;
import java.util.List;

/**
 * 用户数据管道 - 将爬取的数据保存到数据库
 */
@Slf4j
@Component
public class UserDataPipeline implements Pipeline {

    @Autowired(required = false)
    private UserMapper userMapper;

    /**
     * 盐值，与 UserServiceImpl 保持一致
     */
    private static final String SALT = "yupi";

    /**
     * 默认密码（爬虫创建的用户）
     */
    private static final String DEFAULT_PASSWORD = "12345678";

    @Override
    public void process(ResultItems resultItems, Task task) {
        log.info("========== UserDataPipeline.process() 被调用 ==========");
        log.info("resultItems 中的所有字段: {}", resultItems.getAll().keySet());
        
        // 尝试获取 userData 字段（单个用户）
        CrawledUserData userData = resultItems.get("userData");
        
        if (userData != null) {
            saveUserData(userData);
        } else {
            // 尝试获取所有 user_* 字段（API 返回的多个用户）
            boolean foundAny = false;
            for (String key : resultItems.getAll().keySet()) {
                if (key.startsWith("user_")) {
                    Object obj = resultItems.get(key);
                    if (obj instanceof CrawledUserData) {
                        foundAny = true;
                        saveUserData((CrawledUserData) obj);
                    }
                }
            }
            
            if (!foundAny && userData == null) {
                log.warn("⚠️ 未找到任何用户数据");
            }
        }
        
        log.info("========== UserDataPipeline.process() 完成 ==========");
    }

    /**
     * 保存单个用户数据
     */
    private void saveUserData(CrawledUserData userData) {
        if (userData == null) {
            log.warn("⚠️ 爬取的数据为空，跳过保存");
            return;
        }

        log.info("📦 接收到用户数据:");
        log.info("   - 用户名: {}", userData.getUsername());
        log.info("   - 平台: {}", userData.getPlatform());
        log.info("   - 标签数量: {}", userData.getTags() != null ? userData.getTags().size() : 0);
        log.info("   - 标签列表: {}", userData.getTags());

        // 检查 UserMapper 是否注入成功
        if (userMapper == null) {
            log.error("❌ UserMapper 未注入，无法保存数据！请确保通过 Spring 容器获取 UserDataPipeline");
            return;
        }
        
        log.info("✅ UserMapper 已注入，开始保存数据...");

        try {
            // 检查用户是否已存在
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserAccount, userData.getUsername());
            User existingUser = userMapper.selectOne(queryWrapper);

            if (existingUser != null) {
                log.info("🔄 用户已存在，更新标签: {}", userData.getUsername());
                // 更新现有用户的标签
                updateUserTags(existingUser, userData);
                log.info("✅ 更新用户标签成功: {}", userData.getUsername());
            } else {
                log.info("➕ 用户不存在，创建新用户: {}", userData.getUsername());
                // 创建新用户
                createNewUser(userData);
                log.info("✅ 创建新用户成功: {}", userData.getUsername());
            }

        } catch (Exception e) {
            log.error("❌ 保存用户数据失败: {}, 错误详情: {}", userData.getUsername(), e.getMessage(), e);
        }
    }

    /**
     * 更新用户标签
     */
    private void updateUserTags(User user, CrawledUserData userData) {
        // 合并标签（去重）
        List<String> newTags = userData.getTags();
        
        if (newTags != null && !newTags.isEmpty()) {
            String existingTagsStr = user.getTags();
            
            if (existingTagsStr != null && !existingTagsStr.isEmpty()) {
                // 解析现有标签
                List<String> existingTags = JSON.parseArray(existingTagsStr, String.class);
                
                // 合并并去重
                for (String tag : newTags) {
                    if (!existingTags.contains(tag)) {
                        existingTags.add(tag);
                    }
                }
                
                user.setTags(JSON.toJSONString(existingTags));
            } else {
                // 直接设置新标签
                user.setTags(JSON.toJSONString(newTags));
            }
            
            user.setUpdateTime(new Date());
            userMapper.updateById(user);
        }
    }

    /**
     * 创建新用户
     */
    private void createNewUser(CrawledUserData userData) {
        User user = new User();
        user.setUsername(userData.getUsername());
        user.setUserAccount(userData.getUsername());
        // 使用 AvatarUtils 生成随机头像（与系统其他用户保持一致）
        user.setAvatarUrl(AvatarUtils.getRandomAvatar());
        user.setTags(JSON.toJSONString(userData.getTags()));
        user.setUserStatus(0);
        user.setUserRole(0);
        user.setIsDelete(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        
        // 使用与 UserServiceImpl 相同的加密方式
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + DEFAULT_PASSWORD).getBytes());
        user.setUserPassword(encryptPassword);
        
        int rows = userMapper.insert(user);
        log.info("插入数据库，影响行数: {}, 用户ID: {}", rows, user.getId());
    }
}
