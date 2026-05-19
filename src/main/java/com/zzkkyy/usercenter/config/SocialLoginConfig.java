package com.zzkkyy.usercenter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 第三方登录配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "social.login")
public class SocialLoginConfig {

    /**
     * 微信配置
     */
    private Wechat wechat = new Wechat();

    /**
     * QQ配置
     */
    private QQ qq = new QQ();

    @Data
    public static class Wechat {
        private String appId;
        private String appSecret;
        private String redirectUri;
    }

    @Data
    public static class QQ {
        private String appId;
        private String appKey;
        private String redirectUri;
    }
}