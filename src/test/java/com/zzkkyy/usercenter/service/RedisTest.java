package com.zzkkyy.usercenter.service;


import com.zzkkyy.usercenter.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;



    @Test
    void testRedis() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("yupiString", "heheh");
        valueOperations.set("yupiInt", 1);
        valueOperations.set("yupiDouble", 2.0);
        User user = new User();
        user.setUsername("zky2222");
        valueOperations.set("yupiUser", user);

        Object yupi = valueOperations.get("yupiString");
        Assertions.assertTrue("heheh".equals((String) yupi));
        yupi = valueOperations.get("yupiInt");
        Assertions.assertTrue(1 == (Integer) yupi);
        yupi = valueOperations.get("yupiDouble");
        Assertions.assertTrue(2.0 == (Double) yupi);
        System.out.println(valueOperations.get("yupiUser"));
    }




}
