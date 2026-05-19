package com.zzkkyy.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzkkyy.usercenter.model.domain.Team;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.model.dto.TeamQuery;
import com.zzkkyy.usercenter.model.request.TeamJoinRequest;
import com.zzkkyy.usercenter.model.request.TeamQuitRequest;
import com.zzkkyy.usercenter.model.request.TeamUpdateRequest;
import com.zzkkyy.usercenter.model.vo.TeamUserVO;

import java.util.List;

/**
* @author Administrator
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2025-11-26 16:49:51
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     * @param team
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     * @param teamQuery
     * @param isAdmin
     * @param loginUser 当前登录用户（用于判断是否已加入）
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin, User loginUser);

    /**
     * 获取用户加入的队伍列表
     * @param userId 用户ID
     * @param teamQuery 查询条件
     * @param isAdmin 是否管理员
     * @param loginUser 当前登录用户
     * @return
     */
    List<TeamUserVO> listJoinTeams(Long userId, TeamQuery teamQuery, boolean isAdmin, User loginUser);

    /**
     * 跟新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 解散队伍
     * @param id
     * @return
     */
    boolean deleteTeam(long id,User loginUser);
}
