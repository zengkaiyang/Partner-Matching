package com.zzkkyy.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Hive标签统计表（MySQL兜底）
 * @TableName hive_tag_stats
 */
@TableName(value = "hive_tag_stats")
@Data
public class HiveTagStats implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 标签名称
     */
    @TableField("tag_name")
    private String tagName;

    /**
     * 标签分类
     */
    @TableField("category")
    private String category;

    /**
     * 使用次数
     */
    @TableField("total_count")
    private Integer totalCount;

    /**
     * 使用用户数
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
