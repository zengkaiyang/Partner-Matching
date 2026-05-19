package com.zzkkyy.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ErrorCode;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.exception.BusinessException;
import com.zzkkyy.usercenter.model.domain.Team;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.model.dto.TeamQuery;
import com.zzkkyy.usercenter.model.request.TeamJoinRequest;
import com.zzkkyy.usercenter.model.request.TeamQuitRequest;
import com.zzkkyy.usercenter.model.request.TeamUpdateRequest;
import com.zzkkyy.usercenter.model.vo.TeamUserVO;
import com.zzkkyy.usercenter.service.TeamService;
import com.zzkkyy.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 队伍接口
 */
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    /**
     * 创建队伍
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody Team team, HttpServletRequest request) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        long id = teamService.addTeam(team, loginUser);
        return ResultUtils.success(id);
    }

    /**
     * 删除队伍
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(id, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 更新队伍
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 根据id获取队伍
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NO_USER);
        }
        
        return ResultUtils.success(team);
    }

    /**
     * 获取队伍列表（分页）
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            teamQuery = new TeamQuery();
        }
        
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = userService.isAdmin(loginUser);
        
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin, loginUser);
        return ResultUtils.success(teamList);
    }

    /**
     * 获取我加入的队伍列表
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQuery teamQuery, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        boolean isAdmin = userService.isAdmin(loginUser);
        
        List<TeamUserVO> teamList = teamService.listJoinTeams(loginUser.getId(), teamQuery, isAdmin, loginUser);
        return ResultUtils.success(teamList);
    }

    /**
     * 获取我创建的队伍列表
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            teamQuery = new TeamQuery();
        }
        
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        
        boolean isAdmin = userService.isAdmin(loginUser);
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin, loginUser);
        return ResultUtils.success(teamList);
    }

    /**
     * 加入队伍
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 退出队伍
     */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }
}
