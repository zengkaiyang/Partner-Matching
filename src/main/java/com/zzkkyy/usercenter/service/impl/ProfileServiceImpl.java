package com.zzkkyy.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzkkyy.usercenter.mapper.*;
import com.zzkkyy.usercenter.model.domain.*;
import com.zzkkyy.usercenter.service.ProfileService;
import com.zzkkyy.usercenter.service.UserExperienceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 个人信息服务实现
 */
@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserExperienceMapper userExperienceMapper;

    @Resource
    private BrowseHistoryMapper browseHistoryMapper;

    @Resource
    private UserFollowMapper userFollowMapper;

    @Resource
    private UserExperienceService userExperienceService;

    @Resource
    private PostFavoriteMapper postFavoriteMapper;

    @Resource
    private StrategyFavoriteMapper strategyFavoriteMapper;

    @Resource
    private ForumPostMapper forumPostMapper;

    @Resource
    private StrategyMapper strategyMapper;

    @Override
    public User getUserProfile(long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 获取用户经验信息
        UserExperience experience = userExperienceService.getUserExperience(userId);
        if (experience != null) {
            user.setPoints(experience.getTotalPoints());
            user.setLevel(experience.getLevel());
        } else {
            user.setPoints(0);
            user.setLevel(1);
        }

        // 计算获赞数、关注数、粉丝数
        // 这里简化处理，实际应该从相关表中统计
        user.setLikes(0);
        user.setFollowing(getFollowingCount(userId));
        user.setFollowers(getFollowerCount(userId));

        // 格式化注册时间
        if (user.getCreateTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            user.setRegisterTime(sdf.format(user.getCreateTime()));
        }

        // 清除密码字段
        user.setUserPassword(null);

        return user;
    }

    @Override
    @Transactional
    public boolean updateUserProfile(User user) {
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 只允许更新部分字段
        existingUser.setUsername(user.getUsername());
        existingUser.setAvatarUrl(user.getAvatarUrl());
        existingUser.setBio(user.getBio());
        existingUser.setCity(user.getCity());
        existingUser.setBirthday(user.getBirthday());
        existingUser.setPhone(user.getPhone());
        existingUser.setEmail(user.getEmail());
        existingUser.setGender(user.getGender());
        existingUser.setUpdateTime(new Date());

        userMapper.updateById(existingUser);
        log.info("更新用户信息成功，userId: {}", user.getId());
        return true;
    }

    @Override
    @Transactional
    public void addBrowseHistory(long userId, String contentType, long contentId) {
        BrowseHistory history = new BrowseHistory();
        history.setUserId(userId);
        history.setContentType(contentType);
        history.setContentId(contentId);
        history.setBrowseTime(new Date());
        browseHistoryMapper.insert(history);

        log.info("添加浏览历史，userId: {}, contentType: {}, contentId: {}", userId, contentType, contentId);
    }

    @Override
    public List<BrowseHistory> getBrowseHistory(long userId, String type, int limit) {
        QueryWrapper<BrowseHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        // 按类型筛选
        if ("forum".equals(type)) {
            queryWrapper.eq("content_type", "forum");
        } else if ("strategy".equals(type)) {
            queryWrapper.eq("content_type", "strategy");
        }
        
        queryWrapper.orderByDesc("browse_time")
                .last("LIMIT " + limit);

        return browseHistoryMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public boolean followUser(long followerId, long followingId) {
        if (followerId == followingId) {
            throw new RuntimeException("不能关注自己");
        }

        // 检查是否已关注
        QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", followerId).eq("following_id", followingId);
        Long count = userFollowMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("已经关注过该用户");
        }

        // 添加关注关系
        UserFollow userFollow = new UserFollow();
        userFollow.setFollowerId(followerId);
        userFollow.setFollowingId(followingId);
        userFollow.setCreateTime(new Date());
        userFollowMapper.insert(userFollow);

        log.info("关注用户成功，followerId: {}, followingId: {}", followerId, followingId);
        return true;
    }

    @Override
    @Transactional
    public boolean unfollowUser(long followerId, long followingId) {
        QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", followerId).eq("following_id", followingId);
        userFollowMapper.delete(queryWrapper);

        log.info("取消关注成功，followerId: {}, followingId: {}", followerId, followingId);
        return true;
    }

    @Override
    public List<User> getFollowers(long userId, int pageNum, int pageSize) {
        // 先查询粉丝的用户ID列表
        QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("following_id", userId);
        List<UserFollow> follows = userFollowMapper.selectList(queryWrapper);
        List<Long> followerIds = follows.stream()
                .map(UserFollow::getFollowerId)
                .collect(Collectors.toList());

        if (followerIds.isEmpty()) {
            return List.of();
        }

        // 分页查询用户信息
        Page<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> userQuery = new QueryWrapper<>();
        userQuery.in("id", followerIds);

        Page<User> userPage = userMapper.selectPage(page, userQuery);
        
        // 清除密码
        userPage.getRecords().forEach(u -> u.setUserPassword(null));
        
        return userPage.getRecords();
    }

    @Override
    public List<User> getFollowing(long userId, int pageNum, int pageSize) {
        // 先查询关注的用户ID列表
        QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", userId);
        List<UserFollow> follows = userFollowMapper.selectList(queryWrapper);
        List<Long> followingIds = follows.stream()
                .map(UserFollow::getFollowingId)
                .collect(Collectors.toList());

        if (followingIds.isEmpty()) {
            return List.of();
        }

        // 分页查询用户信息
        Page<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> userQuery = new QueryWrapper<>();
        userQuery.in("id", followingIds);

        Page<User> userPage = userMapper.selectPage(page, userQuery);
        
        // 清除密码
        userPage.getRecords().forEach(u -> u.setUserPassword(null));
        
        return userPage.getRecords();
    }

    @Override
    public boolean isFollowing(long followerId, long followingId) {
        QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", followerId).eq("following_id", followingId);
        Long count = userFollowMapper.selectCount(queryWrapper);
        return count > 0;
    }

    /**
     * 获取关注数
     */
    private int getFollowingCount(long userId) {
        QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", userId);
        return Math.toIntExact(userFollowMapper.selectCount(queryWrapper));
    }

    /**
     * 获取粉丝数
     */
    private int getFollowerCount(long userId) {
        QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("following_id", userId);
        return Math.toIntExact(userFollowMapper.selectCount(queryWrapper));
    }

    @Override
    public List<?> getFavorites(long userId, String type) {
        if ("strategy".equals(type)) {
            // 获取攻略收藏
            QueryWrapper<StrategyFavorite> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .orderByDesc("create_time");
            List<StrategyFavorite> favorites = strategyFavoriteMapper.selectList(queryWrapper);

            // 关联查询攻略详情
            return favorites.stream().map(fav -> {
                Strategy strategy = strategyMapper.selectById(fav.getStrategyId());
                return strategy;
            }).filter(s -> s != null).collect(Collectors.toList());
        } else if ("forum".equals(type)) {
            // 获取论坛帖子收藏
            QueryWrapper<PostFavorite> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .orderByDesc("create_time");
            List<PostFavorite> favorites = postFavoriteMapper.selectList(queryWrapper);

            // 关联查询帖子详情
            return favorites.stream().map(fav -> {
                ForumPost post = forumPostMapper.selectById(fav.getPostId());
                return post;
            }).filter(p -> p != null).collect(Collectors.toList());
        }
        return List.of();
    }


    @Override
    @Transactional
    public boolean unfavorite(long favoriteId) {
        // 尝试从攻略收藏中删除
        QueryWrapper<StrategyFavorite> strategyQuery = new QueryWrapper<>();
        strategyQuery.eq("id", favoriteId);
        int strategyDeleted = strategyFavoriteMapper.delete(strategyQuery);

        if (strategyDeleted > 0) {
            log.info("取消攻略收藏成功，favoriteId: {}", favoriteId);
            return true;
        }

        // 尝试从帖子收藏中删除
        QueryWrapper<PostFavorite> postQuery = new QueryWrapper<>();
        postQuery.eq("id", favoriteId);
        int postDeleted = postFavoriteMapper.delete(postQuery);

        if (postDeleted > 0) {
            log.info("取消帖子收藏成功，favoriteId: {}", favoriteId);
            return true;
        }

        log.warn("未找到收藏记录，favoriteId: {}", favoriteId);
        return false;
    }

}
