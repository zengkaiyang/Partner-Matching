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
    private Long id;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签分类
     */
    private String category;

    /**
     * 使用次数
     */
    private Integer totalCount;

    /**
     * 使用用户数
     */
    private Integer userCount;

    /**
     * 同步时间
     */
    private Date syncTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
