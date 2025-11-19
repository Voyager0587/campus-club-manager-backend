package com.club.campusclubmanager.vo;

import com.club.campusclubmanager.enums.SignupStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动报名视图对象
 */
@Data
public class ActivitySignupVO {

    /**
     * 报名记录ID
     */
    private Long id;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 活动标题
     */
    private String activityTitle;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 学号
     */
    private String studentId;

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
}
