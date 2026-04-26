package com.zzkkyy.usercenter.model.request;

import lombok.Data;

import java.util.Date;

@Data
public class TeamJoinRequest {

    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;

}
