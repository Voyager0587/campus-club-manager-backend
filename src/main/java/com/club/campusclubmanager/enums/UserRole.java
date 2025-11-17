package com.club.campusclubmanager.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRole {
    /**
     * 普通用户（学生）
     */
    USER("user", "普通用户"),

    /**
     * 社团管理员
     */
    CLUB_ADMIN("club_admin", "社团管理员"),

    /**
     * 系统管理员
     */
    SYSTEM_ADMIN("system_admin", "系统管理员");

    @EnumValue
    @JsonValue
    private final String code;

    private final String description;

    UserRole(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static UserRole fromCode(String code) {
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("未知的角色类型: " + code);
    }
}
