package com.zzkkyy.usercenter.model.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class HallMessageRequest implements Serializable {
    private String content;
    private static final long serialVersionUID = 1L;
}
