package com.zzkkyy.usercenter.service;


import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void testRedisson() {
        RList<String> rList = redissonClient.getList("test");
        System.out.println(rList.get(0));
        rList.remove(0);

    }


}
