package com.zzkkyy.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzkkyy.usercenter.model.domain.TeamTag;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【team_tag(队伍标签关联表)】的数据库操作Service
 * @createDate 2026-05-21
 */
public interface TeamTagService extends IService<TeamTag> {
    
    /**
     * 获取队伍的标签列表
     * @param teamId 队伍ID
     * @return 标签名称列表
     */
    List<String> getTagsByTeamId(Long teamId);
    
    /**
     * 更新队伍的标签
     * @param teamId 队伍ID
     * @param tags 标签列表
     */
    void updateTeamTags(Long teamId, List<String> tags);
}
