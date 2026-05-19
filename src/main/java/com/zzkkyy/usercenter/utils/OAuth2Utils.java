package com.zzkkyy.usercenter.utils;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 第三方登录OAuth2.0工具类
 */
@Slf4j
public class OAuth2Utils {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final Gson gson = new Gson();

    /**
     * 获取微信访问令牌和OpenID
     */
    public static Map<String, Object> getWechatAccessTokenAndOpenId(String appId, String appSecret, String code) {
        try {
            String url = String.format(
                "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                appId, appSecret, code
            );
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            Map<String, Object> result = gson.fromJson(response.getBody(), Map.class);
            
            if (result.containsKey("access_token") && result.containsKey("openid")) {
                return result;
            } else {
                log.error("获取微信access_token失败: {}", result.get("errmsg"));
                return null;
            }
        } catch (Exception e) {
            log.error("获取微信access_token异常", e);
            return null;
        }
    }

    /**
     * 获取微信访问令牌
     */
    public static String getWechatAccessToken(String appId, String appSecret, String code) {
        try {
            String url = String.format(
                "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                appId, appSecret, code
            );
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            Map<String, Object> result = gson.fromJson(response.getBody(), Map.class);
            
            if (result.containsKey("access_token")) {
                return (String) result.get("access_token");
            } else {
                log.error("获取微信access_token失败: {}", result.get("errmsg"));
                return null;
            }
        } catch (Exception e) {
            log.error("获取微信access_token异常", e);
            return null;
        }
    }

    /**
     * 获取微信用户信息
     */
    public static Map<String, Object> getWechatUserInfo(String accessToken, String openId) {
        try {
            String url = String.format(
                "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s",
                accessToken, openId
            );
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return gson.fromJson(response.getBody(), Map.class);
        } catch (Exception e) {
            log.error("获取微信用户信息异常", e);
            return null;
        }
    }

    /**
     * 获取QQ访问令牌
     */
    public static String getQQAccessToken(String appId, String appKey, String code, String redirectUri) {
        try {
            String url = String.format(
                "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s&redirect_uri=%s",
                appId, appKey, code, redirectUri
            );
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            // QQ返回的是URL编码格式，需要解析
            String body = response.getBody();
            if (body != null && body.contains("access_token=")) {
                String[] params = body.split("&");
                for (String param : params) {
                    if (param.startsWith("access_token=")) {
                        return param.substring("access_token=".length());
                    }
                }
            }
            
            log.error("获取QQ access_token失败: {}", body);
            return null;
        } catch (Exception e) {
            log.error("获取QQ access_token异常", e);
            return null;
        }
    }

    /**
     * 获取QQ用户OpenID
     */
    public static String getQQOpenId(String accessToken) {
        try {
            String url = String.format(
                "https://graph.qq.com/oauth2.0/me?access_token=%s",
                accessToken
            );
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String body = response.getBody();
            if (body != null && body.contains("\"openid\"")) {
                // 解析JSONP响应
                int start = body.indexOf("{");
                int end = body.lastIndexOf("}") + 1;
                if (start >= 0 && end > start) {
                    String jsonStr = body.substring(start, end);
                    Map<String, Object> result = gson.fromJson(jsonStr, Map.class);
                    return (String) result.get("openid");
                }
            }
            
            log.error("获取QQ openid失败: {}", body);
            return null;
        } catch (Exception e) {
            log.error("获取QQ openid异常", e);
            return null;
        }
    }

    /**
     * 获取QQ用户信息
     */
    public static Map<String, Object> getQQUserInfo(String appId, String accessToken, String openId) {
        try {
            String url = String.format(
                "https://graph.qq.com/user/get_user_info?access_token=%s&oauth_consumer_key=%s&openid=%s",
                accessToken, appId, openId
            );
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return gson.fromJson(response.getBody(), Map.class);
        } catch (Exception e) {
            log.error("获取QQ用户信息异常", e);
            return null;
        }
    }
}