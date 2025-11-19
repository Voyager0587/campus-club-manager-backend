package com.club.campusclubmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 审核活动请求（系统管理员）
 */
@Data
public class ReviewActivityRequest {

    /**
     * 审核状态：published-通过, rejected-拒绝
     */
    @NotBlank(message = "审核状态不能为空")
    private String status;

    /**
     * 审核备注
     */
    private String note;
}
