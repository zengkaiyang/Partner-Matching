package com.zzkkyy.usercenter.controller;

import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ErrorCode;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 标签控制器
 */
@RestController
@RequestMapping("/tag")
@Tag(name = "标签管理", description = "标签相关接口")
@Slf4j
public class TagController {

    @Resource
    private TagService tagService;

    /**
     * 从用户表同步标签统计数据
     */
    @PostMapping("/sync-from-users")
    @Operation(summary = "同步标签统计", description = "从user表的tags字段统计并更新tag表")
    public BaseResponse<String> syncTagsFromUsers() {
        try {
            tagService.syncTagsFromUsers();
            return ResultUtils.success("标签同步成功");
        } catch (Exception e) {
            log.error("同步标签失败: {}", e.getMessage(), e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "同步失败: " + e.getMessage());
        }
    }
}
