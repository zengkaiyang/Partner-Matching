package com.zzkkyy.usercenter.common;


import lombok.Data;

import java.io.Serializable;

/**
 * 通用删除请求参数
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = -5446729412697096160L;

    private long id;


}
