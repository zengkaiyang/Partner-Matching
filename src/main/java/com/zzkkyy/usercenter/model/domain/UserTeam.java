package com.zzkkyy.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 用户队伍关系
 * @TableName user_team
 */
@TableName(value ="user_team")
@Data
public class UserTeam {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 用户id
     */
    @TableField("userId")
    private Long userId;

    /**
     * 队伍id
     */
    @TableField("teamId")
    private Long teamId;

    /**
     * 加入时间
     */
    @TableField("joinTime")
    private Date joinTime;

    /**
     * 创建时间
     */
    @TableField("createTime")
    private Date createTime;

    /**
     * 
     */
    @TableField("updateTime")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("isDelete")
    private Integer isDelete;
}