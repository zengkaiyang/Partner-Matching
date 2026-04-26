package com.zzkkyy.usercenter.service;


import com.zzkkyy.usercenter.mapper.UserMapper;
import com.zzkkyy.usercenter.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
public class InsertUsersTest {

    @Resource
    private UserService userService;

    /**
     * 批量插入用户
     */
    @Test
    public void dbInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("fake用户");
            user.setUserAccount("fakezky");
            user.setAvatarUrl("https://i1.hdslb.com/bfs/archive/ac7edcb6a0cd83bc3a7a7a0a3730ba69c17f47ab.png");
            user.setTags(null);
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("22222");
            user.setEmail("31313");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("13131");
            userList.add(user);
        }
        userService.saveBatch(userList,1000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 多线程插入用户
     */
    @Test
    public void doConcurrencyInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        // 分十组
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<User> userList = new ArrayList<>();
            while(true){
                j++;
                User user = new User();
                user.setUsername("fake用户");
                user.setUserAccount("fakezky");
                user.setAvatarUrl("https://i1.hdslb.com/bfs/archive/ac7edcb6a0cd83bc3a7a7a0a3730ba69c17f47ab.png");
                user.setTags(null);
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("22222");
                user.setEmail("31313");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setPlanetCode("13131");
                userList.add(user);
                if(j % 100000 == 0){
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                userService.saveBatch(userList, 10000);
            });
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }


}
