package com.zzkkyy.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "hall_message")
public class HallMessage implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")  // 明确指定数据库字段名
    private Long userId;

    @TableField("username")
    private String username;

    @TableField("avatar_url")  // 明确指定数据库字段名
    private String avatarUrl;

    @TableField("content")
    private String content;

    @TableField("create_time")  // 明确指定数据库字段名
    private Date createTime;

    @TableField("update_time")  // 明确指定数据库字段名
    private Date updateTime;

    @TableField("is_delete")  // 明确指定数据库字段名
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
