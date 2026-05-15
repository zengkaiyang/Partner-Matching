package com.zzkkyy.usercenter.crawler.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 爬取的用户数据模型
 */
@Data
public class CrawledUserData implements Serializable {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户头像URL
     */
    private String avatarUrl;
    
    /**
     * 用户标签列表（必须包含tags）
     */
    private List<String> tags;
    
    /**
     * 用户简介
     */
    private String bio;
    
    /**
     * 来源平台
     */
    private String platform;
    
    /**
     * 个人主页URL
     */
    private String profileUrl;
    
    /**
     * 粉丝数
     */
    private Integer followers;
    
    /**
     * 关注数
     */
    private Integer following;
    
    /**
     * 项目数
     */
    private Integer repositories;
}
