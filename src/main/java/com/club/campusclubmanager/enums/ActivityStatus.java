package com.club.campusclubmanager.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 活动状态枚举
 */
@Getter
public enum ActivityStatus {
    /**
     * 草稿
     */
    DRAFT("draft", "草稿"),

    /**
     * 审核中
     */
    PENDING("pending", "审核中"),

    /**
     * 已发布
     */
    PUBLISHED("published", "已发布"),

    /**
     * 已取消
     */
    CANCELLED("cancelled", "已取消"),

    /**
     * 已完成
     */
    COMPLETED("completed", "已完成");

    @EnumValue
    @JsonValue
    private final String code;

    private final String description;

    ActivityStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static ActivityStatus fromCode(String code) {
        for (ActivityStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的活动状态: " + code);
    }
}
