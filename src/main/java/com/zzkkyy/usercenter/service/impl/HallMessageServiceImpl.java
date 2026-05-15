package com.zzkkyy.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzkkyy.usercenter.mapper.HallMessageMapper;
import com.zzkkyy.usercenter.model.domain.HallMessage;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.service.HallMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class HallMessageServiceImpl extends ServiceImpl<HallMessageMapper, HallMessage> implements HallMessageService {

    // 使用 ConcurrentHashMap 存储在线用户ID（线程安全）
    private final Set<Long> onlineUsers = ConcurrentHashMap.newKeySet();

    @Override
    public boolean sendMessage(User user, String content) {
        // 发送消息时标记用户在线
        userEnterChat(user.getId());

        HallMessage message = new HallMessage();
        message.setUserId(user.getId());
        message.setUsername(user.getUsername());
        message.setAvatarUrl(user.getAvatarUrl());
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setUpdateTime(new Date());

        return this.save(message);
    }

    @Override
    public List<HallMessage> listMessages() {
        QueryWrapper<HallMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("create_time");
        queryWrapper.last("LIMIT 100"); // 限制返回最近100条消息
        return this.list(queryWrapper);
    }

    @Override
    public boolean clearMessages() {
        // 逻辑删除所有消息
        QueryWrapper<HallMessage> queryWrapper = new QueryWrapper<>();
        return this.remove(queryWrapper);
    }

    @Override
    public void userEnterChat(Long userId) {
        onlineUsers.add(userId);
        log.info("用户 {} 进入聊天室，当前在线用户数: {}", userId, onlineUsers.size());
    }

    @Override
    public void userLeaveChat(Long userId) {
        onlineUsers.remove(userId);
        log.info("用户 {} 离开聊天室，当前在线用户数: {}", userId, onlineUsers.size());

        // 如果没有用户在线，清理消息
        if (onlineUsers.isEmpty()) {
            log.info("所有用户已离开聊天室，开始清理聊天数据");
            clearMessages();
        }
    }

    @Override
    public void checkAndCleanMessages() {
        if (onlineUsers.isEmpty()) {
            log.info("定时检查：没有用户在线，清理聊天数据");
            clearMessages();
        }
    }
}
