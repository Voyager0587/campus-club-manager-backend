package com.club.campusclubmanager.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 活动报名状态枚举
 */
@Getter
public enum SignupStatus {
    /**
     * 已报名
     */
    REGISTERED("registered", "已报名"),

    /**
     * 已取消
     */
    CANCELLED("cancelled", "已取消"),

    /**
     * 已签到
     */
    CHECKED_IN("checked_in", "已签到"),

    /**
     * 缺席
     */
    ABSENT("absent", "缺席");

    @EnumValue
    @JsonValue
    private final String code;

    private final String description;

    SignupStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static SignupStatus fromCode(String code) {
        for (SignupStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的报名状态: " + code);
    }
}
