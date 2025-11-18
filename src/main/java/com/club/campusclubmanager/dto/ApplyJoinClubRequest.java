package com.club.campusclubmanager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 申请加入社团请求
 */
@Data
public class ApplyJoinClubRequest {

    /**
     * 社团ID
     */
    @NotNull(message = "社团ID不能为空")
    private Long clubId;

    /**
     * 申请理由
     */
    private String reason;
}
