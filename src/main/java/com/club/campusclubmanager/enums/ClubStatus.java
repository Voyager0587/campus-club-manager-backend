package com.club.campusclubmanager.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 社团状态枚举
 */
@Getter
public enum ClubStatus {
    /**
     * 审核中
     */
    PENDING("pending", "审核中"),

    /**
     * 正常
     */
    NORMAL("normal", "正常"),

    /**
     * 已禁用
     */
    DISABLED("disabled", "已禁用");

    @EnumValue
    @JsonValue
    private final String code;

    private final String description;

    ClubStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static ClubStatus fromCode(String code) {
        for (ClubStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的社团状态: " + code);
    }
}
