package com.club.campusclubmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审核社团申请请求
 */
@Data
public class ReviewApplicationRequest {

    /**
     * 申请ID
     */
    @NotNull(message = "申请ID不能为空")
    private Long applicationId;

    /**
     * 审核结果：approved-通过，rejected-拒绝
     */
    @NotBlank(message = "审核结果不能为空")
    private String status;

    /**
     * 审核备注
     */
    private String reviewNote;
}
