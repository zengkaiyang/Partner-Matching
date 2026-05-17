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
    private Long id;

    /**
     * 统计日期
     */
    private Date statDate;

    /**
     * 活跃用户数
     */
    private Integer activeUsers;

    /**
     * 新发帖数
     */
    private Integer newPosts;

    /**
     * 新评论数
     */
    private Integer newComments;

    /**
     * 同步时间
     */
    private Date syncTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
