package com.zzkkyy.usercenter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ErrorCode;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.exception.BusinessException;
import com.zzkkyy.usercenter.model.domain.SystemConfig;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 后台管理控制器
 * 所有接口仅限管理员访问
 */
@RestController
@RequestMapping("/admin")
@Tag(name = "后台管理", description = "系统管理接口")
@Slf4j
public class AdminController {

    @Resource
    private UserService userService;

    /**
     * 管理员权限验证
     */
    private void checkAdminPermission(HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权访问，仅管理员可操作");
        }
    }

    @GetMapping("/users")
    @Operation(summary = "用户列表")
    public BaseResponse<Page<User>> getUserList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer userRole,
            HttpServletRequest request) {
        // 验证管理员权限
        checkAdminPermission(request);
        
        Page<User> page = userService.getUserList(pageNum, pageSize, keyword, userRole);
        // 清除密码
        page.getRecords().forEach(u -> u.setUserPassword(null));
        return ResultUtils.success(page);
    }

    @PostMapping("/user/update")
    @Operation(summary = "更新用户信息")
    public BaseResponse<Boolean> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 验证管理员权限
        checkAdminPermission(request);
        
        boolean result = userService.updateUser(user);
        return ResultUtils.success(result);
    }

    @PostMapping("/user/disable")
    @Operation(summary = "禁用用户")
    public BaseResponse<Boolean> disableUser(@RequestParam long userId, HttpServletRequest request) {
        // 验证管理员权限
        checkAdminPermission(request);
        
        boolean result = userService.updateUserStatus(userId, 1);
        return ResultUtils.success(result);
    }

    @PostMapping("/user/enable")
    @Operation(summary = "启用用户")
    public BaseResponse<Boolean> enableUser(@RequestParam long userId, HttpServletRequest request) {
        // 验证管理员权限
        checkAdminPermission(request);
        
        boolean result = userService.updateUserStatus(userId, 0);
        return ResultUtils.success(result);
    }

    @PostMapping("/user/delete")
    @Operation(summary = "删除用户")
    public BaseResponse<Boolean> deleteUser(@RequestParam long userId, HttpServletRequest request) {
        // 验证管理员权限
        checkAdminPermission(request);
        
        boolean result = userService.deleteUser(userId);
        return ResultUtils.success(result);
    }

    @GetMapping("/settings")
    @Operation(summary = "获取系统设置")
    public BaseResponse<Map<String, String>> getSettings(HttpServletRequest request) {
        // 验证管理员权限
        checkAdminPermission(request);
        
        Map<String, String> settings = userService.getAllSettings();
        return ResultUtils.success(settings);
    }

    @PostMapping("/settings/update")
    @Operation(summary = "更新系统设置")
    public BaseResponse<Boolean> updateSettings(@RequestBody List<SystemConfig> configs, HttpServletRequest request) {
        // 验证管理员权限
        checkAdminPermission(request);
        
        boolean result = userService.updateSettings(configs);
        return ResultUtils.success(result);
    }

    @GetMapping("/stats")
    @Operation(summary = "系统统计信息")
    public BaseResponse<Map<String, Object>> getStats(HttpServletRequest request) {
        // 验证管理员权限
        checkAdminPermission(request);
        
        Map<String, Object> stats = userService.getSystemStats();
        return ResultUtils.success(stats);
    }
}
