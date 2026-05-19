package com.zzkkyy.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户关注
 * @TableName user_follow
 */
@TableName(value = "user_follow")
@Data
public class UserFollow implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 关注者ID
     */
    @TableField("follower_id")
    private Long followerId;

    /**
     * 被关注者ID
     */
    @TableField("following_id")
    private Long followingId;

    /**
     * 关注时间
     */
    @TableField("create_time")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
