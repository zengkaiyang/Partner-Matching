package com.zzkkyy.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户经验
 * @TableName user_experience
 */
@TableName(value = "user_experience")
@Data
public class UserExperience implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 总积分
     */
    private Integer totalPoints;

    /**
     * 等级V1-V10
     */
    private Integer level;

    /**
     * 发帖数
     */
    private Integer postCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 转发数
     */
    private Integer shareCount;

    /**
     * 获得点赞数
     */
    private Integer likeReceived;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
