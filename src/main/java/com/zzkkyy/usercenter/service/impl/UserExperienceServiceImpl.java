package com.zzkkyy.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzkkyy.usercenter.mapper.UserActionLogMapper;
import com.zzkkyy.usercenter.mapper.UserExperienceMapper;
import com.zzkkyy.usercenter.model.domain.UserActionLog;
import com.zzkkyy.usercenter.model.domain.UserExperience;
import com.zzkkyy.usercenter.service.UserExperienceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 用户经验服务实现
 */
@Service
@Slf4j
public class UserExperienceServiceImpl implements UserExperienceService {

    @Resource
    private UserExperienceMapper userExperienceMapper;

    @Resource
    private UserActionLogMapper userActionLogMapper;

    @Override
    @Transactional
    public void addExperience(Long userId, String actionType, Long targetId, int points) {
        // 查询或创建用户经验记录
        QueryWrapper<UserExperience> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        UserExperience experience = userExperienceMapper.selectOne(queryWrapper);

        if (experience == null) {
            experience = new UserExperience();
            experience.setUserId(userId);
            experience.setTotalPoints(0);
            experience.setLevel(1);
            experience.setPostCount(0);
            experience.setCommentCount(0);
            experience.setShareCount(0);
            experience.setLikeReceived(0);
            experience.setCreateTime(new Date());
            experience.setUpdateTime(new Date());
            userExperienceMapper.insert(experience);
        }

        // 更新总积分
        experience.setTotalPoints(experience.getTotalPoints() + points);

        // 更新等级
        int newLevel = calculateLevel(experience.getTotalPoints());
        experience.setLevel(newLevel);

        // 更新统计数据
        switch (actionType) {
            case "post":
                experience.setPostCount(experience.getPostCount() + 1);
                break;
            case "comment":
                experience.setCommentCount(experience.getCommentCount() + 1);
                break;
            case "share":
                experience.setShareCount(experience.getShareCount() + 1);
                break;
            case "like_received":
                experience.setLikeReceived(experience.getLikeReceived() + 1);
                break;
        }

        experience.setUpdateTime(new Date());
        userExperienceMapper.updateById(experience);

        // 记录行为日志
        UserActionLog actionLog = new UserActionLog();
        actionLog.setUserId(userId);
        actionLog.setActionType(actionType);
        actionLog.setTargetId(targetId);
        actionLog.setPointsEarned(points);
        actionLog.setCreateTime(new Date());
        userActionLogMapper.insert(actionLog);

        log.info("用户{}获得{}积分，行为类型: {}，当前总积分: {}，等级: V{}", 
                userId, points, actionType, experience.getTotalPoints(), newLevel);
    }

    @Override
    public UserExperience getUserExperience(Long userId) {
        QueryWrapper<UserExperience> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return userExperienceMapper.selectOne(queryWrapper);
    }

    @Override
    public int calculateLevel(int totalPoints) {
        if (totalPoints <= 100) return 1;
        if (totalPoints <= 300) return 2;
        if (totalPoints <= 600) return 3;
        if (totalPoints <= 1000) return 4;
        if (totalPoints <= 1500) return 5;
        if (totalPoints <= 2200) return 6;
        if (totalPoints <= 3100) return 7;
        if (totalPoints <= 4200) return 8;
        if (totalPoints <= 5500) return 9;
        return 10;
    }

    @Override
    public List<UserExperience> getTopAuthors(int limit, String period) {
        QueryWrapper<UserExperience> queryWrapper = new QueryWrapper<>();
        
        // 如果是近30天，需要关联user_action_log表筛选时间
        if ("30days".equals(period)) {
            // 简化处理：直接按总积分排序
            // 实际应该根据action_log的create_time筛选近30天的数据
        }
        
        queryWrapper.orderByDesc("total_points")
                .last("LIMIT " + limit);

        return userExperienceMapper.selectList(queryWrapper);
    }
}
