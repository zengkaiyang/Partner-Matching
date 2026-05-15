package com.zzkkyy.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zzkkyy.usercenter.mapper.TagMapper;
import com.zzkkyy.usercenter.mapper.UserMapper;
import com.zzkkyy.usercenter.model.domain.Tag;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Resource
    private TagMapper tagMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncTagsFromUsers() {
        log.info("开始从user表统计标签...");

        // 1. 查询所有有标签的用户
        List<User> users = userMapper.selectList(null);

        // 2. 统计每个标签出现的次数
        Map<String, Integer> tagCountMap = new HashMap<>();
        Gson gson = new Gson();

        for (User user : users) {
            if (StringUtils.isBlank(user.getTags())) {
                continue;
            }

            try {
                // 解析用户的标签JSON数组
                List<String> userTags = gson.fromJson(user.getTags(), new TypeToken<List<String>>() {}.getType());

                if (userTags != null) {
                    for (String tag : userTags) {
                        tagCountMap.merge(tag, 1, Integer::sum);
                    }
                }
            } catch (Exception e) {
                log.error("解析用户ID={}的标签失败: {}", user.getId(), user.getTags());
            }
        }

        log.info("统计到 {} 个不同的标签", tagCountMap.size());

        // 3. 清空tag表旧数据
        this.remove(null);

        // 4. 插入新的统计数据
        for (Map.Entry<String, Integer> entry : tagCountMap.entrySet()) {
            Tag tag = new Tag();
            tag.setTagName(entry.getKey());
            tag.setUserCount(entry.getValue());
            this.save(tag);
        }

        log.info("标签统计同步完成！共同步 {} 个标签", tagCountMap.size());
    }

    @Override
    public List<Tag> getHotTags(int limit) {
        return this.lambdaQuery()
                .orderByDesc(Tag::getUserCount)
                .last("LIMIT " + limit)
                .list();
    }

    // ... existing code ...
    @Override
    public List<Map<String, Object>> getAllTagStats() {
        List<Tag> tags = this.lambdaQuery()
                .select(Tag::getTagName, Tag::getUserCount)
                .orderByDesc(Tag::getUserCount)
                .list();

        return tags.stream()
                .map(tag -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("tag_name", tag.getTagName());
                    map.put("user_count", tag.getUserCount());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }
// ... existing code ...

}
