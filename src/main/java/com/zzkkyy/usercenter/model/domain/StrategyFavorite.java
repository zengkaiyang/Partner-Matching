package com.zzkkyy.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 攻略收藏
 * @TableName strategy_favorite
 */
@TableName(value = "strategy_favorite")
@Data
public class StrategyFavorite implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 攻略ID
     */
    @TableField("strategy_id")
    private Long strategyId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 收藏时间
     */
    @TableField("create_time")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
