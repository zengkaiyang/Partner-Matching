package com.zzkkyy.usercenter.job;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzkkyy.usercenter.mapper.UserMapper;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    //重点用户
    private List<Long> mainUserList = Arrays.asList(1L);

    @Resource
    private RedissonClient redissonClient;

    /**
     * 加载与缓存推荐用户（针对特定用户）
     */
    @Scheduled(cron = "0 28 10 * * *")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("yupao:precachejob:docache:lock");
        try {
            //只有一个线程能获取锁
            if(lock.tryLock(0,30000,TimeUnit.MILLISECONDS)){
                for(Long userId : mainUserList){
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1,28),queryWrapper);
                    String redisKey = String.format("yupao:user:recommend:%s",userId);
                    ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
                    try {
                        valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error",e);
        } finally {
            //只能释放自己的锁
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

    /**
     * 缓存热点用户列表（主页使用）
     * 每小时执行一次
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void doCacheHotUsers(){
        RLock lock = redissonClient.getLock("yupao:precachejob:hotusers:lock");
        try {
            if(lock.tryLock(0,30000,TimeUnit.MILLISECONDS)){
                log.info("开始缓存热点用户列表...");
                
                // 查询有标签的活跃用户，按创建时间排序
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.isNotNull("tags");
                queryWrapper.ne("tags", "");
                queryWrapper.orderByDesc("create_time");
                queryWrapper.last("LIMIT 20");
                
                List<User> hotUsers = userService.list(queryWrapper);
                
                // 脱敏处理
                List<User> safetyUsers = hotUsers.stream()
                        .map(userService::getSafetyUser)
                        .collect(java.util.stream.Collectors.toList());
                
                // 存入Redis
                String redisKey = "yupao:user:hot:list";
                ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
                try {
                    // 缓存1小时
                    valueOperations.set(redisKey, safetyUsers, 1, TimeUnit.HOURS);
                    log.info("热点用户列表缓存成功，数量: {}", safetyUsers.size());
                } catch (Exception e) {
                    log.error("缓存热点用户列表失败", e);
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheHotUsers error", e);
        } finally {
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }
}
