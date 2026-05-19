package com.zzkkyy.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 论坛帖子
 * @TableName forum_post
 */
@TableName(value = "forum_post")
@Data
public class ForumPost implements Serializable {
    /**
     * 帖子ID
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
     * 作者ID
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 分类：recruit-招聘队员, tech-技术交流, share-经验分享, qa-问答求助
     */
    @TableField("category")
    private String category;

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
     * 评论数
     */
    @TableField("comment_count")
    private Integer commentCount;

    /**
     * 收藏数
     */
    @TableField("favorite_count")
    private Integer favoriteCount;

    /**
     * 转发数
     */
    @TableField("share_count")
    private Integer shareCount;

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
