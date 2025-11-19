package com.club.campusclubmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.club.campusclubmanager.enums.ActivityStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动实体
 */
@Data
@TableName("activity")
public class Activity {
    /**
     * 活动ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属社团ID
     */
    private Long clubId;

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
     * 当前报名人数（冗余字段）
     */
    private Integer currentMembers;

    /**
     * 创建者用户ID
     */
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer isDeleted;
}
