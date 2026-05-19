package com.zzkkyy.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.zzkkyy.usercenter.common.ErrorCode;
import com.zzkkyy.usercenter.exception.BusinessException;
import com.zzkkyy.usercenter.mapper.UserMapper;
import com.zzkkyy.usercenter.mapper.ThirdPartyAccountMapper;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.model.request.UserRegisterRequest;
import com.zzkkyy.usercenter.model.vo.UserVO;
import com.zzkkyy.usercenter.service.UserService;
import com.zzkkyy.usercenter.config.SocialLoginConfig;
import com.zzkkyy.usercenter.model.domain.ThirdPartyAccount;
import com.zzkkyy.usercenter.utils.AlgorithmUtils;
import com.zzkkyy.usercenter.utils.AvatarUtils;
import com.zzkkyy.usercenter.utils.OAuth2Utils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.zzkkyy.usercenter.contant.UserConstant.ADMIN_ROLE;
import static com.zzkkyy.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
* @author zzkkyy
* @description 用户注册实现类
* @createDate 2025-09-18 17:13:23
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;
    
    @Resource
    private ThirdPartyAccountMapper thirdPartyAccountMapper;
    
    @Resource
    private SocialLoginConfig socialLoginConfig;
    
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";

    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword,String plantCode,String tags) {
        // 创建 UserRegisterRequest 对象
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUserAccount(userAccount);
        request.setUserPassword(userPassword);
        request.setCheckPassword(checkPassword);
        request.setPlanetCode(plantCode);
        request.setTags(tags);
        
        return userRegisterFull(request, null);
    }

    @Override
    public Long userRegisterFull(UserRegisterRequest userRegisterRequest, HttpServletRequest request) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String plantCode = userRegisterRequest.getPlanetCode();
        String tags = userRegisterRequest.getTags();
        
        //1.利用isAnyBlank校验是否为空
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,plantCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号小于4位");
        }
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码不得小于8位");
        }
        if(plantCode.length() > 6){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号不得大于6位");
        }
        //账户不能包含特殊字符
        String vaildPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(vaildPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //校验二次密码
        if(!userPassword.equals(checkPassword)){
            return -1L;
        }
        //用户不重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复");
        }
        //星球编号不重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", plantCode);
        count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"编号重复");
        }
        //2.对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(plantCode);
        user.setTags(tags);
        
        // 设置用户名
        if (StringUtils.isNotBlank(userRegisterRequest.getUsername())) {
            user.setUsername(userRegisterRequest.getUsername());
        }
        
        // 设置性别
        if (userRegisterRequest.getGender() != null) {
            user.setGender(userRegisterRequest.getGender());
        }
        
        // 设置邮箱
        if (StringUtils.isNotBlank(userRegisterRequest.getEmail())) {
            user.setEmail(userRegisterRequest.getEmail());
        }
        
        // 设置生日并计算年龄
        if (userRegisterRequest.getBirthday() != null) {
            user.setBirthday(userRegisterRequest.getBirthday());
            
            // 计算年龄
            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(userRegisterRequest.getBirthday());
            Calendar nowCal = Calendar.getInstance();
            int age = nowCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
            // 如果还没过生日，年龄减1
            if (nowCal.get(Calendar.MONTH) < birthCal.get(Calendar.MONTH) ||
                (nowCal.get(Calendar.MONTH) == birthCal.get(Calendar.MONTH) && 
                 nowCal.get(Calendar.DAY_OF_MONTH) < birthCal.get(Calendar.DAY_OF_MONTH))) {
                age--;
            }
            user.setAge(age);
        }
        
        // 为新用户分配随机头像
        user.setAvatarUrl(AvatarUtils.getRandomAvatar());
        
        // 设置初始等级为1
        user.setLevel(1);
        
        // 城市由用户注册时手动填写或由用户后续在个人信息页面修改
        // 如果请求中带有城市信息（如注册接口传入），则使用该城市
        if (StringUtils.isNotBlank(user.getCity())) {
            log.info("使用用户手动填写的城市: {}", user.getCity());
        } else {
            log.info("用户未设置城市，可在个人信息页面补充");
        }
        
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.SAVE_ERROR);
        }
        return user.getId();
    }


    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.利用isAnyBlank校验是否为空
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度小于4位");
        }
        // 密码长度至少6位
        if(userPassword.length() < 6){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不能少于6位");
        }
        //账户不能包含特殊字符
        String vaildPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(vaildPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.BYTE_ERROR,"账号包含特殊字符");
        }
        //2.对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在
        if(user == null){
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.NO_USER,"用户不存在");
        }
        User safetyUser = getSafetyUser(user);
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 微信登录
     */
    @Override
    public User wechatLogin(String code, HttpServletRequest request) {
        // 调用微信OAuth2.0接口获取用户信息
        String appId = socialLoginConfig.getWechat().getAppId();
        String appSecret = socialLoginConfig.getWechat().getAppSecret();
        
        if (appId == null || appSecret == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "微信登录未配置，请联系管理员");
        }
        
        // 获取access_token和openid
        Map<String, Object> tokenInfo = OAuth2Utils.getWechatAccessTokenAndOpenId(appId, appSecret, code);
        if (tokenInfo == null || !tokenInfo.containsKey("openid")) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "微信登录失败，无法获取用户信息");
        }
        
        String openId = (String) tokenInfo.get("openid");
        String accessToken = (String) tokenInfo.get("access_token");
        
        // 获取用户详细信息
        Map<String, Object> userInfo = OAuth2Utils.getWechatUserInfo(accessToken, openId);
        if (userInfo == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "微信登录失败，无法获取用户详细信息");
        }
        
        String nickname = (String) userInfo.getOrDefault("nickname", "微信用户");
        String avatarUrl = (String) userInfo.getOrDefault("headimgurl", AvatarUtils.getRandomAvatar());
        
        // 检查是否已有该微信用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("wechatOpenId", openId);
        User existingUser = userMapper.selectOne(queryWrapper);
        
        if (existingUser != null) {
            // 用户已存在，直接登录
            User safetyUser = getSafetyUser(existingUser);
            request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
            return safetyUser;
        } else {
            // 创建新用户
            User newUser = new User();
            newUser.setUserAccount("wechat_" + System.currentTimeMillis());
            newUser.setUsername(nickname);
            newUser.setAvatarUrl(avatarUrl);
            newUser.setWechatOpenId(openId);
            newUser.setUserPassword(DigestUtils.md5DigestAsHex((SALT + "default_password").getBytes())); // 设置默认密码
            newUser.setLevel(1);
            // 微信登录用户不自动设置城市，留空后由用户在个人信息页面手动设置
            
            boolean saveResult = this.save(newUser);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SAVE_ERROR);
            }
            
            User safetyUser = getSafetyUser(newUser);
            request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
            return safetyUser;
        }
    }

    /**
     * QQ登录
     */
    @Override
    public User qqLogin(String code, HttpServletRequest request) {
        // 调用QQ OAuth2.0接口获取用户信息
        String appId = socialLoginConfig.getQq().getAppId();
        String appKey = socialLoginConfig.getQq().getAppKey();
        String redirectUri = socialLoginConfig.getQq().getRedirectUri();
        
        if (appId == null || appKey == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "QQ登录未配置，请联系管理员");
        }
        
        // 获取access_token
        String accessToken = OAuth2Utils.getQQAccessToken(appId, appKey, code, redirectUri);
        if (accessToken == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "QQ登录失败，无法获取访问令牌");
        }
        
        // 获取openid
        String openId = OAuth2Utils.getQQOpenId(accessToken);
        if (openId == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "QQ登录失败，无法获取用户ID");
        }
        
        // 获取用户信息
        Map<String, Object> userInfo = OAuth2Utils.getQQUserInfo(appId, accessToken, openId);
        if (userInfo == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "QQ登录失败，无法获取用户信息");
        }
        
        String nickname = (String) userInfo.getOrDefault("nickname", "QQ用户");
        String avatarUrl = (String) userInfo.getOrDefault("figureurl_qq_2", AvatarUtils.getRandomAvatar());
        
        // 检查是否已有该QQ用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("qqOpenId", openId);
        User existingUser = userMapper.selectOne(queryWrapper);
        
        if (existingUser != null) {
            // 用户已存在，直接登录
            User safetyUser = getSafetyUser(existingUser);
            request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
            return safetyUser;
        } else {
            // 创建新用户
            User newUser = new User();
            newUser.setUserAccount("qq_" + System.currentTimeMillis());
            newUser.setUsername(nickname);
            newUser.setAvatarUrl(avatarUrl);
            newUser.setQqOpenId(openId);
            newUser.setUserPassword(DigestUtils.md5DigestAsHex((SALT + "default_password").getBytes())); // 设置默认密码
            newUser.setLevel(1);
            // QQ登录用户不自动设置城市，留空后由用户在个人信息页面手动设置
            
            boolean saveResult = this.save(newUser);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SAVE_ERROR);
            }
            
            User safetyUser = getSafetyUser(newUser);
            request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
            return safetyUser;
        }
    }

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    @Override
    public User getSafetyUser(User user){
        if(user == null){
            throw new BusinessException(ErrorCode.NO_USER,"用户不存在");
        }
        //3.用户脱敏
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setIsDelete(user.getIsDelete());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setPlanetCode(user.getPlanetCode());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setTags(user.getTags());
        safetyUser.setBirthday(user.getBirthday());
        safetyUser.setAge(user.getAge());
        safetyUser.setCity(user.getCity());

        // 如果用户没有头像，自动分配一个随机头像
        if (StringUtils.isBlank(safetyUser.getAvatarUrl())) {
            safetyUser.setAvatarUrl(AvatarUtils.getAvatarByUserId(user.getId()));
        }

        return safetyUser;
    }


    /**
     * 用户注销
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 根据标签搜索用户内存过滤
     *
     * @param tagNameList 用户拥有的标签
     * @return
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1. 先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 2. 在内存中判断是否包含要求的标签
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public int updateUser(User user,User loginUser) {
        long userId = user.getId();
        if(userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //如果是管理员，允许跟新其他的任意用户
        //若不是管理员，只允许跟新自己信息
        if(!isAdmin(loginUser) && userId != loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if(oldUser == null){
            throw new  BusinessException(ErrorCode.NULL_ERROR);
        }
        log.info("接收到的用户对象: {}", user);
        return userMapper.updateById(user);
        //判断权限,仅管理员和自己可修改
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if(request == null){
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userObj == null){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request){
        // 鉴权仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public boolean isAdmin(User loginUser){
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list();
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        List<Pair<User,Long>> list = new ArrayList<>();
        //用户列表下标 =》 相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            //无标签或为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            long dist = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user,dist));
        }
        // 按编辑距离由小到大排序
        List<Pair<User,Long>> topUserList = list.stream()
                .sorted((a,b) -> (int) (a.getValue() -b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        //原本顺序的userId列表
        List<Long> userIdList = topUserList.stream().map(Pair -> Pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.in("id",userIdList);
        //1 3 2
        //user1 user2 user3
        //1->user1 2->user2 3->user3
        Map<Long,List<User>> userIdUserListMap = this.list(wrapper)
                .stream()
                .map(user -> getSafetyUser(user))
                .map(user -> {
                    // 注入头像
                    if(StringUtils.isBlank(user.getAvatarUrl())) {
                        user.setAvatarUrl(AvatarUtils.getAvatarByUserId(user.getId()));
                    }
                    return user;
                })
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;

    }

    @Override
    public List<com.zzkkyy.usercenter.model.vo.MatchedUserVO> matchUsersWithScore(long num, User loginUser) {
        // 1. 获取当前用户的标签
        String tags = loginUser.getTags();
        if (StringUtils.isBlank(tags)) {
            return new ArrayList<>();
        }
        
        Gson gson = new Gson();
        List<String> currentUserTags = gson.fromJson(tags, new TypeToken<List<String>>() {}.getType());
        
        // 2. 查询所有有标签的用户（排除自己）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "username", "avatarUrl", "tags", "gender", "planetCode");
        queryWrapper.isNotNull("tags");
        queryWrapper.ne("tags", "");
        queryWrapper.ne("id", loginUser.getId());
        
        List<User> userList = this.list(queryWrapper);
        
        // 3. 计算每个用户的编辑距离和匹配度
        List<Pair<User, Long>> userDistanceList = userList.stream()
                .map(user -> {
                    String userTagsStr = user.getTags();
                    if (StringUtils.isBlank(userTagsStr)) {
                        return null;
                    }
                    
                    try {
                        List<String> userTags = gson.fromJson(userTagsStr, new TypeToken<List<String>>() {}.getType());
                        if (userTags == null || userTags.isEmpty()) {
                            return null;
                        }
                        
                        // 计算编辑距离
                        long distance = AlgorithmUtils.minDistance(currentUserTags, userTags);
                        return new Pair<>(user, distance);
                    } catch (Exception e) {
                        log.error("解析用户标签失败: {}", userTagsStr, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        // 4. 按编辑距离从小到大排序，取前 num 个
        List<Pair<User, Long>> topUserList = userDistanceList.stream()
                .sorted(Comparator.comparingLong(Pair::getValue))
                .limit(num)
                .collect(Collectors.toList());
        
        // 5. 转换为 VO 对象，计算匹配度并注入头像
        return topUserList.stream()
                .map(pair -> {
                    User user = pair.getKey();
                    long distance = pair.getValue();
                    
                    // 计算匹配度
                    List<String> userTags = gson.fromJson(user.getTags(), new TypeToken<List<String>>() {}.getType());
                    double similarity = AlgorithmUtils.calculateSimilarityScore(currentUserTags, userTags);
                    int matchRate = (int) Math.round(similarity * 100);
                    
                    // 创建 VO 对象
                    com.zzkkyy.usercenter.model.vo.MatchedUserVO vo = 
                            new com.zzkkyy.usercenter.model.vo.MatchedUserVO();
                    vo.setId(user.getId());
                    vo.setUsername(user.getUsername());
                    
                    // 注入头像：如果用户没有头像，则根据用户ID生成固定头像
                    String avatarUrl = user.getAvatarUrl();
                    if (StringUtils.isBlank(avatarUrl)) {
                        avatarUrl = com.zzkkyy.usercenter.utils.AvatarUtils.getAvatarByUserId(user.getId());
                    }
                    vo.setAvatarUrl(avatarUrl);
                    
                    vo.setTags(user.getTags());
                    vo.setGender(user.getGender());
                    vo.setPlanetCode(user.getPlanetCode());
                    vo.setMatchScore(similarity);
                    vo.setMatchRate(matchRate);
                    
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getRandomTags(int num) {
        // 查询所有用户的标签
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);

        // 收集所有标签
        Set<String> allTagsSet = new HashSet<>();
        Gson gson = new Gson();

        for (User user : userList) {
            String tagsStr = user.getTags();
            if (StringUtils.isNotBlank(tagsStr)) {
                try {
                    List<String> userTags = gson.fromJson(tagsStr, new TypeToken<List<String>>() {}.getType());
                    if (userTags != null) {
                        allTagsSet.addAll(userTags);
                    }
                } catch (Exception e) {
                    log.error("解析标签失败: {}", tagsStr, e);
                }
            }
        }

        // 转换为列表并随机选择
        List<String> allTagsList = new ArrayList<>(allTagsSet);
        Collections.shuffle(allTagsList);

        // 返回指定数量的标签
        int resultSize = Math.min(num, allTagsList.size());
        return allTagsList.subList(0, resultSize);
    }

    @Override
    public List<User> searchUsersByTagsMatch(List<String> searchTags, User loginUser) {
        if (CollectionUtils.isEmpty(searchTags)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    
        // 优化1: 只查询必要的字段，减少数据传输
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "username", "avatarUrl", "tags", "gender", "planetCode");
        queryWrapper.isNotNull("tags");
        // 排除当前用户
        if (loginUser != null) {
            queryWrapper.ne("id", loginUser.getId());
        }
    
        List<User> userList = this.list(queryWrapper);
    
        Gson gson = new Gson();
    
        // 将搜索标签转换为大写，用于比较
        Set<String> searchTagsUpper = searchTags.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
    
        // 使用标签重叠度算法（改进版 Jaccard 相似度）
        List<Pair<User, Double>> userScoreList = userList.parallelStream()
                .map(user -> {
                    String tagsStr = user.getTags();
                    if (StringUtils.isBlank(tagsStr)) {
                        return null;
                    }
    
                    try {
                        List<String> userTags = gson.fromJson(tagsStr, new TypeToken<List<String>>() {}.getType());
                        if (userTags == null || userTags.isEmpty()) {
                            return null;
                        }
    
                        // 将用户标签转换为大写
                        Set<String> userTagsUpper = userTags.stream()
                                .map(String::toUpperCase)
                                .collect(Collectors.toSet());
    
                        // 计算交集
                        Set<String> intersection = new HashSet<>(searchTagsUpper);
                        intersection.retainAll(userTagsUpper);
    
                        // 计算并集
                        Set<String> union = new HashSet<>(searchTagsUpper);
                        union.addAll(userTagsUpper);
    
                        // Jaccard 相似度 = 交集 / 并集
                        double similarity = union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    
                        return new Pair<>(user, similarity);
                    } catch (Exception e) {
                        log.error("解析用户标签失败: {}", tagsStr, e);
                        return null;
                    }
                })
                .filter(pair -> pair != null && pair.getValue() > 0) // 只保留有匹配的用户
                .collect(Collectors.toList());
    
        // 按匹配度从高到低排序
        List<Pair<User, Double>> sortedList = userScoreList.stream()
                .sorted((pair1, pair2) -> Double.compare(pair2.getValue(), pair1.getValue()))
                .limit(50)  // 先取前50个最匹配的
                .collect(Collectors.toList());
    
        // 批量查询完整用户信息并进行脱敏处理
        List<Long> userIds = sortedList.stream()
                .map(Pair::getKey)
                .map(User::getId)
                .collect(Collectors.toList());
    
        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }
    
        // 通过 ID 批量查询，确保数据完整性
        Map<Long, User> userMap = this.listByIds(userIds).stream()
                .map(this::getSafetyUser)
                .map(user -> {
                    // 注入头像
                    if(StringUtils.isBlank(user.getAvatarUrl())) {
                        user.setAvatarUrl(AvatarUtils.getAvatarByUserId(user.getId()));
                    }
                    return user;
                })
                .collect(Collectors.toMap(User::getId, u -> u));
    
        // 保持排序顺序返回结果
        return sortedList.stream()
                .map(Pair::getKey)
                .map(User::getId)
                .map(userMap::get)
                .filter(Objects::nonNull)
                .limit(30)  // 最终只返回前30个
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> searchUsersByTagsMatchWithScore(List<String> searchTags, User loginUser) {
        if (CollectionUtils.isEmpty(searchTags)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    
        // 查询所有有标签的用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "username", "avatarUrl", "tags", "gender", "planetCode");
        queryWrapper.isNotNull("tags");
        if (loginUser != null) {
            queryWrapper.ne("id", loginUser.getId());
        }
    
        List<User> userList = this.list(queryWrapper);
        Gson gson = new Gson();
    
        // 将搜索标签转换为大写
        Set<String> searchTagsUpper = searchTags.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
    
        // 计算每个用户的 Jaccard 相似度
        List<Map<String, Object>> resultWithScore = userList.parallelStream()
                .map(user -> {
                    String tagsStr = user.getTags();
                    if (StringUtils.isBlank(tagsStr)) {
                        return null;
                    }
    
                    try {
                        List<String> userTags = gson.fromJson(tagsStr, new TypeToken<List<String>>() {}.getType());
                        if (userTags == null || userTags.isEmpty()) {
                            return null;
                        }
    
                        // 转换为大写集合
                        Set<String> userTagsUpper = userTags.stream()
                                .map(String::toUpperCase)
                                .collect(Collectors.toSet());
    
                        // 计算 Jaccard 相似度
                        Set<String> intersection = new HashSet<>(searchTagsUpper);
                        intersection.retainAll(userTagsUpper);
                        
                        Set<String> union = new HashSet<>(searchTagsUpper);
                        union.addAll(userTagsUpper);
                        
                        double similarity = union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    
                        if (similarity <= 0) {
                            return null; // 过滤掉没有匹配的用户
                        }
    
                        // 构建结果 Map
                        Map<String, Object> result = new HashMap<>();
                        result.put("user", getSafetyUser(user));
                        result.put("matchRate", (int)(similarity * 100)); // 转为百分比
                        result.put("intersectionTags", intersection); // 共同标签
                        result.put("totalSearchTags", searchTagsUpper.size());
                        result.put("totalUserTags", userTagsUpper.size());
                        
                        // 注入头像
                        if(StringUtils.isBlank(user.getAvatarUrl())) {
                            result.put("avatarUrl", AvatarUtils.getAvatarByUserId(user.getId()));
                        }
                        
                        return result;
                    } catch (Exception e) {
                        log.error("解析用户标签失败: {}", tagsStr, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    
        // 按匹配度从高到低排序
        resultWithScore.sort((r1, r2) -> {
            int rate1 = (Integer) r1.get("matchRate");
            int rate2 = (Integer) r2.get("matchRate");
            return Integer.compare(rate2, rate1); // 降序
        });
    
        // 取前 30 个
        return resultWithScore.subList(0, Math.min(30, resultWithScore.size()));
    }


    /**
     * 根据标签搜索用户sql查询
     * @param tagNameList
     * @return
     */
    @Deprecated
    private List<User> searchUsersByTagsBYSQL(List<String> tagNameList){
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //拼接and查询
        for(String tagName : tagNameList){
            queryWrapper = queryWrapper.like("tags",tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> getUserList(int pageNum, int pageSize, String keyword, Integer userRole) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like("username", keyword)
                    .or()
                    .like("userAccount", keyword)
                    .or()
                    .like("email", keyword)
            );
        }
        
        // 角色筛选
        if (userRole != null) {
            queryWrapper.eq("userRole", userRole);
        }
        
        queryWrapper.orderByDesc("createTime");
        return userMapper.selectPage(page, queryWrapper);
    }

    @Override
    public boolean updateUser(User user) {
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        
        // 只允许更新部分字段
        existingUser.setUsername(user.getUsername());
        existingUser.setAvatarUrl(user.getAvatarUrl());
        existingUser.setPhone(user.getPhone());
        existingUser.setEmail(user.getEmail());
        existingUser.setGender(user.getGender());
        existingUser.setUserRole(user.getUserRole());
        existingUser.setUserStatus(user.getUserStatus());
        existingUser.setUpdateTime(new Date());
        
        return userMapper.updateById(existingUser) > 0;
    }

    @Override
    public boolean updateUserStatus(long userId, int status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        
        user.setUserStatus(status);
        user.setUpdateTime(new Date());
        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean deleteUser(long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        
        // 逻辑删除
        user.setIsDelete(1);
        user.setUpdateTime(new Date());
        return userMapper.updateById(user) > 0;
    }

    @Override
    public Map<String, String> getAllSettings() {
        // TODO: 从system_config表读取配置
        Map<String, String> settings = new HashMap<>();
        settings.put("siteName", "Partner Matching");
        settings.put("allowRegister", "true");
        settings.put("pageSize", "10");
        return settings;
    }

    @Override
    public boolean updateSettings(List<com.zzkkyy.usercenter.model.domain.SystemConfig> configs) {
        // TODO: 实现系统设置更新逻辑
        log.info("更新系统设置，配置数量: {}", configs.size());
        return true;
    }

    @Override
    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总用户数
        long totalUsers = userMapper.selectCount(null);
        stats.put("totalUsers", totalUsers);
        
        // 今日新增用户（简化处理）
        stats.put("todayNewUsers", 0);
        
        // 活跃用户数
        stats.put("activeUsers", 0);
        
        return stats;
    }

    /**
     * 获取真实IP地址
     * @param request HTTP请求
     * @return IP地址
     */
    private String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 第三方平台模拟登录（微信/QQ）
     * 逻辑：
     * 1. 查询 third_party_account 表，验证账号密码
     * 2. 获取绑定的 user_id
     * 3. 登录该用户
     */
    @Override
    public User thirdPartyLogin(com.zzkkyy.usercenter.model.request.ThirdPartyLoginRequest loginRequest, HttpServletRequest request) {
        String platform = loginRequest.getPlatform();
        String account = loginRequest.getAccount();
        String password = loginRequest.getPassword();
        
        // 验证平台类型
        if (!"wechat".equals(platform) && !"qq".equals(platform)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的平台类型");
        }
        
        // 查询第三方账号
        QueryWrapper<ThirdPartyAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("platform", platform);
        queryWrapper.eq("account", account);
        ThirdPartyAccount thirdPartyAccount = thirdPartyAccountMapper.selectOne(queryWrapper);
        
        if (thirdPartyAccount == null) {
            throw new BusinessException(ErrorCode.NO_USER, "该账号未绑定，请先注册或绑定账号");
        }
        
        // 验证密码（第三方账号表使用纯MD5加密，不加盐）
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!encryptedPassword.equals(thirdPartyAccount.getPassword())) {
            log.error("密码验证失败 - 账号: {}, 平台: {}", account, platform);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        
        // 获取绑定的用户ID
        Long userId = thirdPartyAccount.getUserId();
        User user = userMapper.selectById(userId);
        
        if (user == null) {
            throw new BusinessException(ErrorCode.NO_USER, "绑定的用户不存在");
        }
        
        // 登录成功，设置session
        User safetyUser = getSafetyUser(user);
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        
        log.info("用户通过{}平台登录成功，第三方账号: {}, 绑定用户ID: {}", platform, account, userId);
        return safetyUser;
    }

    /**
     * 绑定第三方账号
     */
    @Override
    public boolean bindThirdPartyAccount(Long userId, String platform, String account, String password) {
        // 验证参数
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }
        if (!"wechat".equals(platform) && !"qq".equals(platform)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的平台类型");
        }
        if (account == null || account.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能为空");
        }
        
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NO_USER, "用户不存在");
        }
        
        // 检查该平台的账号是否已被其他用户绑定
        QueryWrapper<ThirdPartyAccount> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("platform", platform);
        checkWrapper.eq("account", account);
        ThirdPartyAccount existing = thirdPartyAccountMapper.selectOne(checkWrapper);
        if (existing != null && !existing.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该账号已被其他用户绑定");
        }
        
        // 如果已经绑定过，则更新密码
        if (existing != null && existing.getUserId().equals(userId)) {
            existing.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
            existing.setUpdateTime(new Date());
            return thirdPartyAccountMapper.updateById(existing) > 0;
        }
        
        // 创建新的绑定关系
        ThirdPartyAccount thirdPartyAccount = new ThirdPartyAccount();
        thirdPartyAccount.setUserId(userId);
        thirdPartyAccount.setPlatform(platform);
        thirdPartyAccount.setAccount(account);
        thirdPartyAccount.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        thirdPartyAccount.setCreateTime(new Date());
        thirdPartyAccount.setUpdateTime(new Date());
        
        return thirdPartyAccountMapper.insert(thirdPartyAccount) > 0;
    }

    /**
     * 解绑第三方账号
     */
    @Override
    public boolean unbindThirdPartyAccount(Long userId, String platform) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }
        if (!"wechat".equals(platform) && !"qq".equals(platform)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的平台类型");
        }
        
        QueryWrapper<ThirdPartyAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("platform", platform);
        
        return thirdPartyAccountMapper.delete(queryWrapper) > 0;
    }

    /**
     * 查询用户的第三方账号绑定情况
     */
    @Override
    public java.util.List<com.zzkkyy.usercenter.model.domain.ThirdPartyAccount> getUserThirdPartyAccounts(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }
        
        QueryWrapper<ThirdPartyAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        // 不返回密码字段
        queryWrapper.select("id", "user_id", "platform", "account", "create_time", "update_time");
        
        return thirdPartyAccountMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<User> getTopUsersByExperience(int limit) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // User实体有@TableLogic注解，不需要手动添加is_delete条件
        queryWrapper.orderByDesc("points")
                .last("LIMIT " + limit);
        
        return userMapper.selectList(queryWrapper);
    }
    
    @Override
    public User getUserById(long userId) {
        return userMapper.selectById(userId);
    }

}




