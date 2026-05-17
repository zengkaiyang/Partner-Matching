package com.zzkkyy.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.zzkkyy.usercenter.common.ErrorCode;
import com.zzkkyy.usercenter.exception.BusinessException;
import com.zzkkyy.usercenter.mapper.UserMapper;
import com.zzkkyy.usercenter.model.domain.User;
import com.zzkkyy.usercenter.model.vo.UserVO;
import com.zzkkyy.usercenter.service.UserService;
import com.zzkkyy.usercenter.utils.AlgorithmUtils;
import com.zzkkyy.usercenter.utils.AvatarUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.*;
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
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";

    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword,String plantCode,String tags) {
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
        // 为新用户分配随机头像
        user.setAvatarUrl(AvatarUtils.getRandomAvatar());
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
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度小于8位");
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
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;

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

        // 优化2: 使用并行流加速计算（适合多核CPU）
        List<Pair<User, Long>> userDistanceList = userList.parallelStream()
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

                        // 计算编辑距离
                        long distance = AlgorithmUtils.minDistance(searchTags, userTags);
                        return new Pair<>(user, distance);
                    } catch (Exception e) {
                        log.error("解析用户标签失败: {}", tagsStr, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 优化3: 按编辑距离从小到大排序，并限制返回数量
        List<Pair<User, Long>> sortedList = userDistanceList.stream()
                .sorted(Comparator.comparingLong(Pair::getValue))
                .limit(50)  // 先取前50个最匹配的
                .collect(Collectors.toList());

        // 优化4: 批量查询完整用户信息并进行脱敏处理
        List<Long> userIds = sortedList.stream()
                .map(Pair::getKey)
                .map(User::getId)
                .collect(Collectors.toList());

        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 通过ID批量查询，确保数据完整性
        Map<Long, User> userMap = this.listByIds(userIds).stream()
                .map(this::getSafetyUser)
                .collect(Collectors.toMap(User::getId, u -> u));

        // 保持排序顺序返回结果
        return sortedList.stream()
                .map(Pair::getKey)
                .map(User::getId)
                .map(userMap::get)
                .filter(Objects::nonNull)
                .limit(30)  // 最终只返回前10个
                .collect(Collectors.toList());
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

}




