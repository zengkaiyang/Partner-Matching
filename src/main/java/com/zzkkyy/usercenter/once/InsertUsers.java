package com.zzkkyy.usercenter.once;
import java.util.Date;

import com.zzkkyy.usercenter.mapper.UserMapper;
import com.zzkkyy.usercenter.model.domain.User;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public class InsertUsers {

    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户
     */
//    @Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE)
    public void dbInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("fake用户");
            user.setUserAccount("fakezky");
            user.setAvatarUrl("https://i1.hdslb.com/bfs/archive/ac7edcb6a0cd83bc3a7a7a0a3730ba69c17f47ab.png");
            user.setTags("21312");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("22222");
            user.setEmail("31313");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("13131");
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }


}
