package com.zzkkyy.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信登录请求体
 */
@Data
public class WechatLoginRequest implements Serializable {

    private static final long serialVersionUID = -2604125149084866110L;

    /**
     * 微信授权码
     */
    private String code;

    /**
     * 前端传递的state参数（用于防止CSRF攻击）
     */
    private String state;
}