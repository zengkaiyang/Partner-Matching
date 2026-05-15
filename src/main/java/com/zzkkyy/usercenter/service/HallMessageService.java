package com.zzkkyy.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzkkyy.usercenter.model.domain.HallMessage;
import com.zzkkyy.usercenter.model.domain.User;

import java.util.List;

public interface HallMessageService extends IService<HallMessage> {

    /**
     * 发送消息
     */
    boolean sendMessage(User user, String content);

    /**
     * 获取消息列表
     */
    List<HallMessage> listMessages();

    /**
     * 清空消息（逻辑删除）
     */
    boolean clearMessages();

    /**
     * 用户进入聊天室
     */
    void userEnterChat(Long userId);

    /**
     * 用户离开聊天室
     */
    void userLeaveChat(Long userId);

    /**
     * 检查并清理消息（当没有用户在线时）
     */
    void checkAndCleanMessages();
}
