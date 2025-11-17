package com.club.campusclubmanager.vo;

import com.club.campusclubmanager.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息视图对象
 */
@Data
public class UserInfoVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 用户角色
     */
    private UserRole role;

    /**
     * 账户状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
