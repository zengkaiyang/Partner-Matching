package com.zzkkyy.usercenter.service;

import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzkkyy.usercenter.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import static com.zzkkyy.usercenter.contant.UserConstant.ADMIN_ROLE;
import static com.zzkkyy.usercenter.contant.UserConstant.USER_LOGIN_STATE;

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

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 跟新用户信息
     * @param user
     * @return
     */
    int updateUser(User user,User loginUser);

    /**
     * 货权当前登录用户信息
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);


    /**
     * 是否为管理员
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);

    /**
     * 匹配用户
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(long num, User loginUser);
}
