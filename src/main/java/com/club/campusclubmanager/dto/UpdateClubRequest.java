package com.club.campusclubmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新社团信息请求
 */
@Data
public class UpdateClubRequest {

    /**
     * 社团ID
     */
    @NotNull(message = "社团ID不能为空")
    private Long id;

    /**
     * 社团名称
     */
    @NotBlank(message = "社团名称不能为空")
    private String name;

    /**
     * 社团简介
     */
    @NotBlank(message = "社团简介不能为空")
    private String description;

    /**
     * 社团图标URL
     */
    private String logo;
}
