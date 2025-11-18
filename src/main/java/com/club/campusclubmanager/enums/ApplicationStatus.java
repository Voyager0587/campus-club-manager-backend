package com.club.campusclubmanager.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 申请状态枚举
 */
@Getter
public enum ApplicationStatus {
    /**
     * 待审核
     */
    PENDING("pending", "待审核"),

    /**
     * 已通过
     */
    APPROVED("approved", "已通过"),

    /**
     * 已拒绝
     */
    REJECTED("rejected", "已拒绝");

    @EnumValue
    @JsonValue
    private final String code;

    private final String description;

    ApplicationStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static ApplicationStatus fromCode(String code) {
        for (ApplicationStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的申请状态: " + code);
    }
}
