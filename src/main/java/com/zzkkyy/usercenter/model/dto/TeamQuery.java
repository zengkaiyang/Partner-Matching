package com.zzkkyy.usercenter.model.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.zzkkyy.usercenter.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 队伍查询封装类
 * @TableName team
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeamQuery extends PageRequest {

    /**
     * id
     */
    private Long id;


    /**
     * id 列表
     */
    private List<Long> idList;

    /**
     * 搜索关键词同时对队伍名称和描述搜索
     */
    private String searchText;

    /**
     * 队伍昵称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;


    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0-公开，1-私有，2-加密
     */
    private Integer status;


}