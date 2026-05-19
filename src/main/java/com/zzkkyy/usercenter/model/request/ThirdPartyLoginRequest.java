package com.zzkkyy.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 第三方平台模拟登录请求
 */
@Data
public class ThirdPartyLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 平台类型：wechat-微信，qq-QQ
     */
    private String platform;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;
}
