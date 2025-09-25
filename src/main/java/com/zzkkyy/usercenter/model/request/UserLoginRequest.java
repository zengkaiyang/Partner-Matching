package com.zzkkyy.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 * @author 曾凯阳
 * 无敌！
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -2604125149084866109L;

    private String userAccount;

    private String userPassword;


}
