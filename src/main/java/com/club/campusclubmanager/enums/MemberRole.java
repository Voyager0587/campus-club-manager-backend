package com.club.campusclubmanager.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 社团成员角色枚举
 */
@Getter
public enum MemberRole {
    /**
     * 普通成员
     */
    MEMBER("member", "普通成员"),

    /**
     * 社团负责人
     */
    LEADER("leader", "社团负责人");

    @EnumValue
    @JsonValue
    private final String code;

    private final String description;

    MemberRole(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static MemberRole fromCode(String code) {
        for (MemberRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("未知的成员角色: " + code);
    }
}
