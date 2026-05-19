package com.zzkkyy.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户行为记录
 * @TableName user_action_log
 */
@TableName(value = "user_action_log")
@Data
public class UserActionLog implements Serializable {
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
     * 行为类型：post-发帖, comment-评论, share-转发, like-点赞
     */
    @TableField("action_type")
    private String actionType;

    /**
     * 目标ID（帖子ID或攻略ID）
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 获得积分
     */
    @TableField("points_earned")
    private Integer pointsEarned;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
