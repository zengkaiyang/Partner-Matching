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
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 分类：recruit-招聘队员, tech-技术交流, share-经验分享, qa-问答求助
     */
    private String category;

    /**
     * 状态：0-正常, 1-审核中, 2-已删除
     */
    private Integer status;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 转发数
     */
    private Integer shareCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
