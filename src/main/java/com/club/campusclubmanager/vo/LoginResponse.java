package com.club.campusclubmanager.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应
 */
@Data
@AllArgsConstructor
public class LoginResponse {

    /**
     * 访问令牌
     */
    private String token;

    /**
     * 用户信息
     */
    private UserInfoVO userInfo;
}
