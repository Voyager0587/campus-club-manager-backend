package com.club.campusclubmanager.vo;

import com.club.campusclubmanager.enums.ActivityStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动详情视图对象
 */
@Data
public class ActivityDetailVO {

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 所属社团ID
     */
    private Long clubId;

    /**
     * 所属社团名称
     */
    private String clubName;

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
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;

    /**
     * 报名开始时间
     */
    private LocalDateTime signupStartTime;

    /**
     * 报名截止时间
     */
    private LocalDateTime signupEndTime;

    /**
     * 活动状态
     */
    private ActivityStatus status;

    /**
     * 最大报名人数（NULL表示不限制）
     */
    private Integer maxMembers;

    /**
     * 当前报名人数
     */
    private Integer currentMembers;

    /**
     * 创建者用户ID
     */
    private Long createUser;

    /**
     * 创建者用户名
     */
    private String createUsername;

    /**
     * 当前用户是否已报名
     */
    private Boolean isSignedUp;

    /**
     * 当前用户的报名状态
     */
    private String signupStatus;

    /**
     * 是否可以报名
     */
    private Boolean canSignup;

    /**
     * 不能报名的原因
     */
    private String signupDisabledReason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
