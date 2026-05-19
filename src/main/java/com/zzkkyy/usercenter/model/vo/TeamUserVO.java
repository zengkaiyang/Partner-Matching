package com.zzkkyy.usercenter.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 队伍和用户信息封装类
 */
@Data
public class TeamUserVO {
    /**
     * id
     */
    private Long id;

    /**
     * 队伍昵称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 标签（JSON数组，用于分类）
     */
    private String tags;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0-公开，1-私有，2-加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 跟新时间
     */
    private Date updateTime;

    private UserVO createUser;

    private Integer hasJoinNum;

    /**
     * 是否已加入队伍
     */
    private boolean hasJoin = false;

    /**
     * 队伍成员列表
     */
    private List<TeamMemberVO> members;

    /**
     * 队伍成员 VO
     */
    @Data
    public static class TeamMemberVO {
        /**
         * 用户ID
         */
        private Long userId;
        
        /**
         * 用户名
         */
        private String username;
        
        /**
         * 头像
         */
        private String avatarUrl;
        
        /**
         * 是否是队长（true-是队长，false-普通成员）
         */
        private boolean isLeader;
        
        /**
         * 加入时间
         */
        private Date joinTime;
    }
}
