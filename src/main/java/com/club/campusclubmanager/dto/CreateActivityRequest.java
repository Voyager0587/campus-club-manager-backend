package com.club.campusclubmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建活动请求
 */
@Data
public class CreateActivityRequest {

    /**
     * 所属社团ID
     */
    @NotNull(message = "社团ID不能为空")
    private Long clubId;

    /**
     * 活动标题
     */
    @NotBlank(message = "活动标题不能为空")
    private String title;

    /**
     * 活动内容详情
     */
    @NotBlank(message = "活动内容不能为空")
    private String content;

    /**
     * 活动封面图URL
     */
    private String cover;

    /**
     * 活动地点
     */
    private String location;

    /**
     * 活动开始时间
     */
    @NotNull(message = "活动开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    @NotNull(message = "活动结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 报名开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signupStartTime;

    /**
     * 报名截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signupEndTime;

    /**
     * 最大报名人数（NULL表示不限制）
     */
    private Integer maxMembers;
}
