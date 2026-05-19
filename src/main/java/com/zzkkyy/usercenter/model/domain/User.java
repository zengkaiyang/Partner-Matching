package com.zzkkyy.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private long id;

    /**
     * 昵称
     */
    private String username;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 标签列表json
     */
    private String tags;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 用户角色
     */
    private Integer userRole;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 城市
     */
    private String city;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 积分（用于排行榜）
     */
    @TableField(exist = false)
    private Integer points;

    /**
     * 等级（V1-V10）
     */
    private Integer level;

    /**
     * 获赞数
     */
    @TableField(exist = false)
    private Integer likes;

    /**
     * 关注数
     */
    @TableField(exist = false)
    private Integer following;

    /**
     * 粉丝数
     */
    @TableField(exist = false)
    private Integer followers;

    /**
     * 注册时间（格式化）
     */
    @TableField(exist = false)
    private String registerTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}