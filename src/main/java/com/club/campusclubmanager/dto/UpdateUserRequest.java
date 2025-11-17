package com.club.campusclubmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 更新用户信息请求
 */
@Data
public class UpdateUserRequest {

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;
}
