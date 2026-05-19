package com.zzkkyy.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 绑定第三方账号请求
 */
@Data
public class BindThirdPartyRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 平台类型：wechat-微信，qq-QQ
     */
    private String platform;

    /**
     * 第三方账号
     */
    private String account;

    /**
     * 第三方密码
     */
    private String password;
}
