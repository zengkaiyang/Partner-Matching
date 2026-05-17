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
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 内容类型：post-帖子, strategy-攻略
     */
    private String contentType;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 浏览时间
     */
    private Date browseTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
