package com.zzkkyy.usercenter.model.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class TeamUpdateRequest {

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
     * 过期时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expireTime;

    /**
     * 0-公开，1-私有，2-加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 标签列表（JSON数组字符串）
     */
    private String tags;

}
