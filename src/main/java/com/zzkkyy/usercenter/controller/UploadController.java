package com.zzkkyy.usercenter.controller;

import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ErrorCode;
import com.zzkkyy.usercenter.common.ResultUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
@Tag(name = "文件上传", description = "文件上传相关接口")
@Slf4j
public class UploadController {

    // 上传文件存储目录
    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping
    @Operation(summary = "上传文件")
    public BaseResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            log.info("开始上传文件: {}", file.getOriginalFilename());
            
            if (file.isEmpty()) {
                return ResultUtils.error(ErrorCode.NULL_ERROR, "文件不能为空");
            }

            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResultUtils.error(ErrorCode.PARAMS_ERROR, "只能上传图片文件");
            }

            // 检查文件大小（5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResultUtils.error(ErrorCode.PARAMS_ERROR, "文件大小不能超过5MB");
            }

            // 使用绝对路径
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                log.info("创建上传目录: {}, 结果: {}", uploadDir, created);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            File dest = new File(uploadDir + File.separator + filename);
            log.info("保存文件到: {}", dest.getAbsolutePath());
            file.transferTo(dest);

            log.info("文件上传成功: {}", filename);

            // 返回文件访问路径（相对路径）
            String fileUrl = "http://localhost:8080/api/uploads/" + filename;
            return ResultUtils.success(fileUrl);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return ResultUtils.error(ErrorCode.SAVE_ERROR, "文件上传失败: " + e.getMessage());
        }
    }
}
