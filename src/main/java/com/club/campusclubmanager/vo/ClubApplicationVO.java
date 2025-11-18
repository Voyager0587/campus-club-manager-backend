package com.club.campusclubmanager.vo;

import com.club.campusclubmanager.enums.ApplicationStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社团申请视图对象
 */
@Data
public class ClubApplicationVO {

    /**
     * 申请ID
     */
    private Long id;

    /**
     * 社团ID
     */
    private Long clubId;

    /**
     * 社团名称
     */
    private String clubName;

    /**
     * 申请用户ID
     */
    private Long userId;

    /**
     * 申请用户名
     */
    private String username;

    /**
     * 申请人真实姓名
     */
    private String realName;

    /**
     * 申请状态
     */
    private ApplicationStatus status;

    /**
     * 申请理由
     */
    private String reason;

    /**
     * 审核备注
     */
    private String reviewNote;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 审核人ID
     */
    private Long reviewerId;

    /**
     * 审核人姓名
     */
    private String reviewerName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
