package com.club.campusclubmanager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.campusclubmanager.dto.LoginRequest;
import com.club.campusclubmanager.dto.RegisterRequest;
import com.club.campusclubmanager.dto.UpdateUserRequest;
import com.club.campusclubmanager.entity.User;
import com.club.campusclubmanager.enums.UserRole;
import com.club.campusclubmanager.exception.BusinessException;
import com.club.campusclubmanager.mapper.UserMapper;
import com.club.campusclubmanager.service.UserService;
import com.club.campusclubmanager.vo.LoginResponse;
import com.club.campusclubmanager.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final String SALT = "campus_club_manager";

    /**
     * 用户注册
     */
    @Override
    public void register(RegisterRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getUsername());
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 检查学号是否已存在
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getStudentId, request.getStudentId());
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException("学号已存在");
        }

        // 检查邮箱是否已存在
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, request.getEmail());
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException("邮箱已存在");
        }

        // 创建用户
        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setPassword(encryptPassword(request.getPassword()));
        user.setRole(UserRole.USER); // 默认为普通用户
        user.setStatus(1); // 默认启用

        this.save(user);
    }

    /**
     * 用户登录
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getUsername());
        User user = this.getOne(queryWrapper);

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!user.getPassword().equals(encryptPassword(request.getPassword()))) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查账户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账户已被禁用");
        }

        // 登录成功，生成Token
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        // 构造用户信息
        UserInfoVO userInfo = convertToUserInfoVO(user);

        return new LoginResponse(token, userInfo);
    }

    /**
     * 获取当前用户信息
     */
    @Override
    public UserInfoVO getCurrentUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToUserInfoVO(user);
    }

    /**
     * 更新用户信息
     */
    @Override
    public void updateUserInfo(UpdateUserRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 如果修改邮箱，检查是否重复
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getEmail, request.getEmail());
            if (this.count(queryWrapper) > 0) {
                throw new BusinessException("邮箱已存在");
            }
        }

        // 更新用户信息
        BeanUtils.copyProperties(request, user, "id", "username", "password", "studentId", "role", "status");
        this.updateById(user);
    }

    /**
     * 加密密码
     */
    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex((password + SALT).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 转换为用户信息VO
     */
    private UserInfoVO convertToUserInfoVO(User user) {
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
