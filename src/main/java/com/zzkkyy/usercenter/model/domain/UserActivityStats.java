package com.zzkkyy.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户活跃度统计表
 * @TableName user_activity_stats
 */
@TableName(value = "user_activity_stats")
@Data
public class UserActivityStats implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 统计日期
     */
    @TableField("stat_date")
    private Date statDate;

    /**
     * 活跃用户数
     */
    @TableField("active_users")
    private Integer activeUsers;

    /**
     * 新发帖数
     */
    @TableField("new_posts")
    private Integer newPosts;

    /**
     * 新评论数
     */
    @TableField("new_comments")
    private Integer newComments;

    /**
     * 同步时间
     */
    @TableField("sync_time")
    private Date syncTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
