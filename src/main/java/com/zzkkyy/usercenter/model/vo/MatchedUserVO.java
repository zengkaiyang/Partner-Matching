package com.zzkkyy.usercenter.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 匹配用户视图对象（包含匹配度）
 */
@Data
public class MatchedUserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 标签JSON字符串
     */
    private String tags;

    /**
     * 性别 0-男 1-女
     */
    private Integer gender;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 匹配度分数（0-1之间）
     */
    private Double matchScore;

    /**
     * 匹配度百分比（0-100）
     */
    private Integer matchRate;
}
