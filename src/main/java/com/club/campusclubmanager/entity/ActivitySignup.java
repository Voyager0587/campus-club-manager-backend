package com.club.campusclubmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.club.campusclubmanager.enums.SignupStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动报名实体
 */
@Data
@TableName("activity_signup")
public class ActivitySignup {
    /**
     * 报名记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 报名状态
     */
    private SignupStatus status;

    /**
     * 报名时间
     */
    private LocalDateTime signupTime;

    /**
     * 签到时间
     */
    private LocalDateTime checkinTime;

    /**
     * 备注信息
     */
    private String remark;

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
