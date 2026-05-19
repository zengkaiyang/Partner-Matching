package com.zzkkyy.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 攻略
 * @TableName strategy
 */
@TableName(value = "strategy")
@Data
public class Strategy implements Serializable {
    /**
     * 攻略ID
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 内容
     */
    @TableField("content")
    private String content;

    /**
     * 摘要
     */
    @TableField("summary")
    private String summary;

    /**
     * 封面图片URL
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 作者ID
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 分类：study-学习, work-工作, game-游戏, life-生活
     */
    @TableField("category")
    private String category;

    /**
     * 类型：manual-手写, ai-AI分析
     */
    @TableField("type")
    private String type;

    /**
     * 状态：0-正常, 1-审核中, 2-已删除
     */
    @TableField("status")
    private Integer status;

    /**
     * 浏览量
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 收藏数
     */
    @TableField("favorite_count")
    private Integer favoriteCount;

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
     * 是否删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
