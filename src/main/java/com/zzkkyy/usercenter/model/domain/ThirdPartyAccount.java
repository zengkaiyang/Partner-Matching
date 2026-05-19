package com.zzkkyy.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 第三方账号绑定表
 */
@TableName(value = "third_party_account")
@Data
public class ThirdPartyAccount implements Serializable {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 关联的用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 平台类型：wechat-微信，qq-QQ
     */
    @TableField("platform")
    private String platform;

    /**
     * 第三方平台账号
     */
    @TableField("account")
    private String account;

    /**
     * 第三方平台密码（加密存储）
     */
    @TableField("password")
    private String password;

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

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
