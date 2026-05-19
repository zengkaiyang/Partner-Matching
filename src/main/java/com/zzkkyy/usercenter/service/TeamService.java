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

    /**
     * 获取队伍详情（含成员列表）
     * @param id 队伍ID
     * @param loginUser 当前登录用户
     * @return 队伍详情
     */
    TeamUserVO getTeamDetail(long id, User loginUser);

    /**
     * 踢出成员（仅队长可操作）
     * @param teamId 队伍ID
     * @param userId 被踢出的用户ID
     * @param operator 操作者（必须是队长）
     * @return 是否成功
     */
    boolean kickMember(long teamId, long userId, User operator);

    /**
     * 转移队长权限（仅队长可操作）
     * @param teamId 队伍ID
     * @param newLeaderId 新队长的用户ID
     * @param operator 操作者（必须是队长）
     * @return 是否成功
     */
    boolean transferLeadership(long teamId, long newLeaderId, User operator);
}
