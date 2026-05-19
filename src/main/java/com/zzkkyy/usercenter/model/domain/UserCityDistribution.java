package com.zzkkyy.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户城市分布表
 * @TableName user_city_distribution
 */
@TableName(value = "user_city_distribution")
@Data
public class UserCityDistribution implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 城市名称
     */
    @TableField("city")
    private String city;

    /**
     * 用户数量
     */
    @TableField("user_count")
    private Integer userCount;

    /**
     * 同步时间
     */
    @TableField("sync_time")
    private Date syncTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
