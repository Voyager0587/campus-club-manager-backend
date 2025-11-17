package com.club.campusclubmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.club.campusclubmanager.dto.LoginRequest;
import com.club.campusclubmanager.dto.RegisterRequest;
import com.club.campusclubmanager.dto.UpdateUserRequest;
import com.club.campusclubmanager.entity.User;
import com.club.campusclubmanager.vo.LoginResponse;
import com.club.campusclubmanager.vo.UserInfoVO;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    void register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 获取当前用户信息
     */
    UserInfoVO getCurrentUserInfo();

    /**
     * 更新用户信息
     */
    void updateUserInfo(UpdateUserRequest request);
}
