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
    @TableField("id")
    private long id;

    /**
     * 昵称
     */
    @TableField("username")
    private String username;

    /**
     * 登录账号
     */
    @TableField("userAccount")
    private String userAccount;

    /**
     * 头像
     */
    @TableField("avatarUrl")
    private String avatarUrl;

    /**
     * 标签列表json
     */
    @TableField("tags")
    private String tags;

    /**
     * 性别
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 密码
     */
    @TableField("userPassword")
    private String userPassword;

    /**
     * 电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 用户状态
     */
    @TableField("userStatus")
    private Integer userStatus;

    /**
     * 创建时间
     */
    @TableField("createTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("updateTime")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("isDelete")
    private Integer isDelete;

    /**
     * 用户角色
     */
    @TableField("userRole")
    private Integer userRole;

    /**
     * 星球编号
     */
    @TableField("planetCode")
    private String planetCode;

    /**
     * 个人简介
     */
    @TableField("bio")
    private String bio;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    /**
     * 生日
     */
    @TableField("birthday")
    private Date birthday;

    /**
     * 年龄
     */
    @TableField("age")
    private Integer age;

    /**
     * 积分（用于排行榜）
     */
    @TableField(exist = false)
    private Integer points;

    /**
     * 等级（V1-V10）
     */
    @TableField("level")
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

    /**
     * 微信OpenID
     */
    @TableField("wechatOpenId")
    private String wechatOpenId;

    /**
     * QQ OpenID
     */
    @TableField("qqOpenId")
    private String qqOpenId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}