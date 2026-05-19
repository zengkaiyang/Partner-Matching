package com.zzkkyy.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户注册请求体
 * @author 曾凯阳
 * 无敌！
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -2604125149084866109L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String planetCode;

    private String tags;

    private String username;

    private Integer gender;

    private String email;

    private Date birthday;

}
