package com.zzkkyy.usercenter.common;

/**
 * 错误码
 * @author 曾凯阳
 * 无敌！
 */
public enum ErrorCode {

    SUCCESS(0,"ok",""),
    PARAMS_ERROR(40000,"请求参数错误",""),
    NULL_ERROR(40001,"请求数据为空",""),
    NO_LOGIN(40100,"无登录",""),
    NO_AUTH(40101,"无权限",""),
    NO_USER(40102,"无用户",""),
    SAVE_ERROR(40103,"保存错误",""),
    BYTE_ERROR(40104,"特殊字符错误",""),
    SYSTEM_ERROR(50000,"系统内部异常","");

    private final int code;
    /**
     * 状态码信息
     */
    private final String message;
    /**
     * 状态码描述
     */
    private final String description;

    ErrorCode(int code,String message ,String description) {
        this.message = message;
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
