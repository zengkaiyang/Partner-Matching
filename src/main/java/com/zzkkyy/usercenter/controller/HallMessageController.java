package com.zzkkyy.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ErrorCode;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.exception.BusinessException;
import com.zzkkyy.usercenter.model.domain.HallMessage;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.model.dto.HallMessageRequest;
import com.zzkkyy.usercenter.service.HallMessageService;
import com.zzkkyy.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/hall/message")
@Slf4j
public class HallMessageController {

    @Resource
    private HallMessageService hallMessageService;

    @Resource
    private UserService userService;

    /**
     * 进入聊天室
     */
    @PostMapping("/enter")
    public BaseResponse<Boolean> enterChat(HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }

        hallMessageService.userEnterChat(loginUser.getId());
        return ResultUtils.success(true);
    }

    /**
     * 离开聊天室
     */
    @PostMapping("/leave")
    public BaseResponse<Boolean> leaveChat(HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }

        hallMessageService.userLeaveChat(loginUser.getId());
        return ResultUtils.success(true);
    }

    /**
     * 发送消息
     */
    @PostMapping("/send")
    public BaseResponse<Boolean> sendMessage(@RequestBody HallMessageRequest request, HttpServletRequest httpRequest) {
        if (request == null || request.getContent() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String content = request.getContent().trim();
        if (content.isEmpty() || content.length() > 500) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息内容不合法");
        }

        User loginUser = userService.getLoginUser(httpRequest);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }

        boolean result = hallMessageService.sendMessage(loginUser, content);
        return ResultUtils.success(result);
    }

    /**
     * 获取消息列表
     */
    @GetMapping("/list")
    public BaseResponse<List<HallMessage>> listMessages(HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }

        // 获取消息时标记用户在线
        hallMessageService.userEnterChat(loginUser.getId());

        List<HallMessage> messages = hallMessageService.listMessages();
        return ResultUtils.success(messages);
    }

    /**
     * 清空聊天消息（离开聊天室时调用）
     */
    @PostMapping("/clear")
    public BaseResponse<Boolean> clearMessages(HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }

        // 用户离开聊天室
        hallMessageService.userLeaveChat(loginUser.getId());

        // 不再直接清空消息，由 userLeaveChat 方法判断是否所有用户都离开后再清空
        return ResultUtils.success(true);
    }
}
