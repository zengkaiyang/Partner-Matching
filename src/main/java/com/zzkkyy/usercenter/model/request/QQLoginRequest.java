package com.zzkkyy.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * QQ登录请求体
 */
@Data
public class QQLoginRequest implements Serializable {

    private static final long serialVersionUID = -2604125149084866111L;

    /**
     * QQ授权码
     */
    private String code;

    /**
     * 前端传递的state参数（用于防止CSRF攻击）
     */
    private String state;
}