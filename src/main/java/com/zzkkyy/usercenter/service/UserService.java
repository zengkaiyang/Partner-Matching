package com.zzkkyy.usercenter.service;

import com.zzkkyy.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 16642
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-09-18 17:13:23
*/
public interface UserService extends IService<User> {



    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     *
     * @return 新用户id
     */
    Long userRegister(String userAccount, String userPassword,String checkPassword,String plantCode);

    /**
     * 用户登录
     * @param userAccount 用户账户
     * @param userPassword  用户密码
     * @return 返回用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getSafetyUser(User user);

    /**
     * 请求用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);
}
