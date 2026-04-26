package com.zzkkyy.usercenter.service;

import com.zzkkyy.usercenter.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 曾凯阳
 * 无敌！
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;


    @Test
    public void testAddUser(){
        User user = new User();
        user.setId(1L);
        user.setUsername("zkydog");
        user.setUserAccount("222");
        user.setAvatarUrl("https://i1.hdslb.com/bfs/archive/ac7edcb6a0cd83bc3a7a7a0a3730ba69c17f47ab.png");
        user.setGender(0);
        user.setUserPassword("456");
        user.setPhone("123");
        user.setEmail("456");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        assertTrue(result);
    }

    @Test
    void userRegister2(){
        String userAccount = "zzkkyyy";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        String planetCode = "12145";
        long result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        userAccount = "dogzky";
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode = "125";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        userAccount = "ppgod";
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode = "1145";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        userAccount = "hhhjjj";
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode = "12315";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
    }

    @Test
    void userRegister() {
        String userAccount = "yupi";
        String userPassword = "";
        String checkPassword = "123456";
        String planetCode = "12345";
        long result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        //断言测试
        Assertions.assertEquals(-1,result);
        userAccount = "yu";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount = "yupi";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount = "yu pi";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount = "dogyupi";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
        userAccount = "zzkkyy";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assertions.assertEquals(-1,result);
    }

    @Test
    void searchUserByUserTags() {
        List<String> tagNameList = Arrays.asList("java","python");
        List<User> userList = userService.searchUsersByTags(tagNameList);
        Assertions.assertNotNull(userList);
    }


}