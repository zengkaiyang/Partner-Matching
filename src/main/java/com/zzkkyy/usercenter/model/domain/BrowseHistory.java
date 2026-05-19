package com.zzkkyy.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 浏览历史
 * @TableName browse_history
 */
@TableName(value = "browse_history")
@Data
public class BrowseHistory implements Serializable {
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
     * 内容类型：post-帖子, strategy-攻略
     */
    @TableField("content_type")
    private String contentType;

    /**
     * 内容ID
     */
    @TableField("content_id")
    private Long contentId;

    /**
     * 浏览时间
     */
    @TableField("browse_time")
    private Date browseTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
