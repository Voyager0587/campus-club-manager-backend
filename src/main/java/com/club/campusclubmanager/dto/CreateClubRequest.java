package com.club.campusclubmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建社团请求
 */
@Data
public class CreateClubRequest {

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
