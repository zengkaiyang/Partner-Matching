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
    @TableField("id")
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 总积分
     */
    @TableField("total_points")
    private Integer totalPoints;

    /**
     * 等级V1-V10
     */
    @TableField("level")
    private Integer level;

    /**
     * 发帖数
     */
    @TableField("post_count")
    private Integer postCount;

    /**
     * 评论数
     */
    @TableField("comment_count")
    private Integer commentCount;

    /**
     * 转发数
     */
    @TableField("share_count")
    private Integer shareCount;

    /**
     * 获得点赞数
     */
    @TableField("like_received")
    private Integer likeReceived;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
