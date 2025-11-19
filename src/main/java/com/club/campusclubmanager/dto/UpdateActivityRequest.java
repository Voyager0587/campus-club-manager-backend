package com.club.campusclubmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 更新活动请求
 */
@Data
public class UpdateActivityRequest {

    /**
     * 活动标题
     */
    private String title;

    /**
     * 活动内容详情
     */
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
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
