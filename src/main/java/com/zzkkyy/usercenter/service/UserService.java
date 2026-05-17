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
    Long userRegister(String userAccount, String userPassword,String checkPassword,String plantCode,String tags);

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

    /**
     * 获取随机标签列表
     * @param num 标签数量
     * @return 随机标签列表
     */
    List<String> getRandomTags(int num);

    /**
     * 根据标签相似度搜索用户（使用编辑距离算法）
     * @param tags 搜索标签
     * @param loginUser 登录用户
     * @return 匹配的用户列表
     */
    List<User> searchUsersByTagsMatch(List<String> tags, User loginUser);

    /**
     * 获取用户列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键词
     * @param userRole 用户角色筛选
     * @return 用户分页列表
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> getUserList(int pageNum, int pageSize, String keyword, Integer userRole);

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUser(User user);

    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 状态（0-正常，1-禁用）
     * @return 是否成功
     */
    boolean updateUserStatus(long userId, int status);

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUser(long userId);

    /**
     * 获取所有系统设置
     * @return 系统设置Map
     */
    java.util.Map<String, String> getAllSettings();

    /**
     * 更新系统设置
     * @param configs 配置列表
     * @return 是否成功
     */
    boolean updateSettings(java.util.List<com.zzkkyy.usercenter.model.domain.SystemConfig> configs);

    /**
     * 获取系统统计信息
     * @return 统计信息
     */
    java.util.Map<String, Object> getSystemStats();

}
