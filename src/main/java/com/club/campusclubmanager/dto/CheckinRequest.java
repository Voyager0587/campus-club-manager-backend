package com.club.campusclubmanager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 活动签到/标记缺席请求
 */
@Data
public class CheckinRequest {

    /**
     * 用户ID列表
     */
    @NotNull(message = "用户ID列表不能为空")
    private List<Long> userIds;

    /**
     * 操作类型：check_in-签到, absent-缺席
     */
    @NotNull(message = "操作类型不能为空")
    private String action;
}
