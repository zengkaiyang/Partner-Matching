package com.zzkkyy.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzkkyy.usercenter.mapper.TeamTagMapper;
import com.zzkkyy.usercenter.model.domain.TeamTag;
import com.zzkkyy.usercenter.service.TeamTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【team_tag(队伍标签关联表)】的数据库操作Service实现
 * @createDate 2026-05-21
 */
@Service
@Slf4j
public class TeamTagServiceImpl extends ServiceImpl<TeamTagMapper, TeamTag> implements TeamTagService {

    @Override
    public List<String> getTagsByTeamId(Long teamId) {
        if (teamId == null || teamId <= 0) {
            return new ArrayList<>();
        }
        
        QueryWrapper<TeamTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("team_id", teamId);
        queryWrapper.select("tag_name");
        
        List<TeamTag> teamTags = this.list(queryWrapper);
        return teamTags.stream()
                .map(TeamTag::getTagName)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTeamTags(Long teamId, List<String> tags) {
        if (teamId == null || teamId <= 0) {
            return;
        }
        
        log.info("更新队伍标签 - teamId: {}, tags: {}", teamId, tags);
        
        // 1. 删除旧的标签
        QueryWrapper<TeamTag> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("team_id", teamId);
        this.remove(deleteWrapper);
        
        // 2. 插入新的标签
        if (!CollectionUtils.isEmpty(tags)) {
            List<TeamTag> teamTagList = tags.stream()
                    .filter(tag -> tag != null && !tag.trim().isEmpty())
                    .map(tag -> {
                        TeamTag teamTag = new TeamTag();
                        teamTag.setTeamId(teamId);
                        teamTag.setTagName(tag.trim());
                        return teamTag;
                    })
                    .collect(Collectors.toList());
            
            if (!teamTagList.isEmpty()) {
                this.saveBatch(teamTagList);
                log.info("成功保存 {} 个标签", teamTagList.size());
            }
        }
    }
}
