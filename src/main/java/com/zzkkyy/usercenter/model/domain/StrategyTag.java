package com.zzkkyy.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 攻略标签关联
 * @TableName strategy_tag
 */
@TableName(value = "strategy_tag")
@Data
public class StrategyTag implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 攻略ID
     */
    private Long strategyId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
