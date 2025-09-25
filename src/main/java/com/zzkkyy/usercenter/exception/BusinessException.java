package com.zzkkyy.usercenter.exception;

import com.zzkkyy.usercenter.common.ErrorCode;

/**
 * 自定义异常类
 * @author 曾凯阳
 * 无敌！
 */
public class BusinessException extends RuntimeException{

    private final String description;

    private final int code;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.description = description;
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.description = errorCode.getDescription();
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode,String description) {
        super(errorCode.getMessage());
        this.description = description;
        this.code = errorCode.getCode();
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }
}
