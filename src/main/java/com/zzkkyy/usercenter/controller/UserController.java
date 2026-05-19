package com.zzkkyy.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzkkyy.usercenter.common.BaseResponse;
import com.zzkkyy.usercenter.common.ErrorCode;
import com.zzkkyy.usercenter.common.ResultUtils;
import com.zzkkyy.usercenter.exception.BusinessException;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.model.request.UserLoginRequest;
import com.zzkkyy.usercenter.model.request.UserRegisterRequest;
import com.zzkkyy.usercenter.model.request.WechatLoginRequest;
import com.zzkkyy.usercenter.model.request.QQLoginRequest;
import com.zzkkyy.usercenter.model.request.ThirdPartyLoginRequest;
import com.zzkkyy.usercenter.model.request.BindThirdPartyRequest;
import com.zzkkyy.usercenter.model.vo.UserVO;
import com.zzkkyy.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.zzkkyy.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 曾凯阳
 * 无敌！
 */
@CrossOrigin(origins = "http://localhost:5173/", allowCredentials = "true")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest, HttpServletRequest request){
        if(userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        String tags = userRegisterRequest.getTags();
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegisterFull(userRegisterRequest, request);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user =  userService.userLogin(userAccount , userPassword , request);
        return ResultUtils.success(user);
    }

    /**
     * 微信登录
     */
    @PostMapping("/login/wechat")
    public BaseResponse<User> wechatLogin(@RequestBody WechatLoginRequest wechatLoginRequest, HttpServletRequest request){
        if(wechatLoginRequest == null || StringUtils.isBlank(wechatLoginRequest.getCode())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 这里需要调用微信OAuth2.0接口获取用户信息
        // 由于微信OAuth2.0需要配置appid和secret，这里先返回一个示例实现
        User user = userService.wechatLogin(wechatLoginRequest.getCode(), request);
        return ResultUtils.success(user);
    }

    /**
     * QQ登录
     */
    @PostMapping("/login/qq")
    public BaseResponse<User> qqLogin(@RequestBody QQLoginRequest qqLoginRequest, HttpServletRequest request){
        if(qqLoginRequest == null || StringUtils.isBlank(qqLoginRequest.getCode())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 这里需要调用QQ OAuth2.0接口获取用户信息
        // 由于QQ OAuth2.0需要配置appid和secret，这里先返回一个示例实现
        User user = userService.qqLogin(qqLoginRequest.getCode(), request);
        return ResultUtils.success(user);
    }

    /**
     * 第三方平台模拟登录（微信/QQ）
     */
    @PostMapping("/login/third-party")
    public BaseResponse<User> thirdPartyLogin(@RequestBody ThirdPartyLoginRequest loginRequest, HttpServletRequest request){
        if(loginRequest == null){
            log.error("第三方登录请求为空");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        
        log.info("收到第三方登录请求 - platform: {}, account: {}, password: {}", 
                loginRequest.getPlatform(), loginRequest.getAccount(), 
                loginRequest.getPassword() != null ? "***" : "null");
        
        if(StringUtils.isAnyBlank(loginRequest.getPlatform(), loginRequest.getAccount(), loginRequest.getPassword())){
            log.error("第三方登录参数不完整 - platform: {}, account: {}, password: {}", 
                    loginRequest.getPlatform(), loginRequest.getAccount(), 
                    loginRequest.getPassword() != null ? "***" : "null");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "平台、账号和密码不能为空");
        }
        
        User user = userService.thirdPartyLogin(loginRequest, request);
        return ResultUtils.success(user);
    }

    /**
     * 绑定第三方账号
     */
    @PostMapping("/bind-third-party")
    public BaseResponse<Boolean> bindThirdParty(@RequestBody BindThirdPartyRequest bindRequest, HttpServletRequest request){
        if(bindRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        boolean result = userService.bindThirdPartyAccount(
            loginUser.getId(), 
            bindRequest.getPlatform(), 
            bindRequest.getAccount(), 
            bindRequest.getPassword()
        );
        
        return ResultUtils.success(result);
    }

    /**
     * 解绑第三方账号
     */
    @PostMapping("/unbind-third-party")
    public BaseResponse<Boolean> unbindThirdParty(@RequestBody BindThirdPartyRequest bindRequest, HttpServletRequest request){
        if(bindRequest == null || StringUtils.isBlank(bindRequest.getPlatform())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不完整");
        }
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        boolean result = userService.unbindThirdPartyAccount(loginUser.getId(), bindRequest.getPlatform());
        
        return ResultUtils.success(result);
    }

    /**
     * 查询用户的第三方账号绑定情况
     */
    @GetMapping("/third-party-accounts")
    public BaseResponse<java.util.List<com.zzkkyy.usercenter.model.domain.ThirdPartyAccount>> getThirdPartyAccounts(HttpServletRequest request){
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        java.util.List<com.zzkkyy.usercenter.model.domain.ThirdPartyAccount> accounts = 
            userService.getUserThirdPartyAccounts(loginUser.getId());
        
        return ResultUtils.success(accounts);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }


    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        long userId = currentUser.getId();
        // Todo 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username,HttpServletRequest request){
        // 鉴权仅管理员可查询
        if (!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList){
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(userList);
    }

    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize,long pageNum,HttpServletRequest request){
        //如果有缓存直接读缓存
        User loginUser = userService.getLoginUser(request);
        String redisKey = String.format("yupao:user:recommend:%s",loginUser.getId());
        ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if(userPage != null){
            return ResultUtils.success(userPage);
        }
        // 无缓存查数据库
        // 鉴权仅管理员可查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum,pageSize),queryWrapper);
        //写缓存
        try {
            valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error",e);
        }
        return ResultUtils.success(userPage);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> DeleteUser(@RequestBody Long id,HttpServletRequest request){
        // 鉴权仅管理员可查询
        if(!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> UpdateUser(@RequestBody  User user,HttpServletRequest request){
        //1.校验参数是否为空
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        int result = userService.updateUser(user,loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取最匹配的用户
     * @param num
     * @param request
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num, HttpServletRequest request){
        if(num <= 0 || num > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(num,user));
    }

    /**
     * 获取最匹配的用户（带匹配度）
     * @param num 返回用户数量
     * @param request HTTP请求
     * @return 匹配的用户列表（包含匹配度）
     */
    @GetMapping("/match/score")
    public BaseResponse<List<com.zzkkyy.usercenter.model.vo.MatchedUserVO>> matchUsersWithScore(
            long num, HttpServletRequest request){
        if(num <= 0 || num > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsersWithScore(num, user));
    }

    /**
     * 获取热点用户列表（从 Redis 缓存读取）
     * @param request HTTP请求
     * @return 热点用户列表
     */
    @GetMapping("/hot")
    public BaseResponse<List<User>> getHotUsers(HttpServletRequest request){
        // 尝试从缓存获取
        String redisKey = "yupao:user:hot:list";
        ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
        List<User> hotUsers = (List<User>) valueOperations.get(redisKey);
        
        if(hotUsers != null && !hotUsers.isEmpty()){
            log.info("从缓存获取热点用户列表，数量: {}", hotUsers.size());
            // 为每个用户注入头像
            List<User> usersWithAvatar = hotUsers.stream()
                    .map(user -> {
                        if(StringUtils.isBlank(user.getAvatarUrl())) {
                            user.setAvatarUrl(com.zzkkyy.usercenter.utils.AvatarUtils.getAvatarByUserId(user.getId()));
                        }
                        return user;
                    })
                    .collect(Collectors.toList());
            return ResultUtils.success(usersWithAvatar);
        }
        
        // 缓存未命中，直接查询数据库
        log.info("缓存未命中，从数据库查询热点用户");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("tags");
        queryWrapper.ne("tags", "");
        queryWrapper.orderByDesc("createTime");
        queryWrapper.last("LIMIT 20");
        
        List<User> userList = userService.list(queryWrapper);
        List<User> safetyUsers = userList.stream()
                .map(userService::getSafetyUser)
                .map(user -> {
                    // 注入头像
                    if(StringUtils.isBlank(user.getAvatarUrl())) {
                        user.setAvatarUrl(com.zzkkyy.usercenter.utils.AvatarUtils.getAvatarByUserId(user.getId()));
                    }
                    return user;
                })
                .collect(Collectors.toList());
        
        // 写入缓存（5分钟）
        try {
            valueOperations.set(redisKey, safetyUsers, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("缓存热点用户失败", e);
        }
        
        return ResultUtils.success(safetyUsers);
    }


    /**
     * 获取随机标签列表
     * @param num 标签数量
     * @return 随机标签列表
     */
    @GetMapping("/tags/random")
    public BaseResponse<List<String>> getRandomTags(@RequestParam(defaultValue = "10") int num){
        if(num <= 0 || num > 50){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<String> tags = userService.getRandomTags(num);
        return ResultUtils.success(tags);
    }

    /**
     * 根据标签相似度搜索用户
     * @param tags 搜索标签
     * @param request HTTP请求
     * @return 匹配的用户列表
     */
    @PostMapping("/search/tags/match")
    public BaseResponse<List<User>> searchUsersByTagsMatch(@RequestBody List<String> tags, HttpServletRequest request){
        if(CollectionUtils.isEmpty(tags)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        List<User> userList = userService.searchUsersByTagsMatch(tags, loginUser);
        return ResultUtils.success(userList);
    }




}
