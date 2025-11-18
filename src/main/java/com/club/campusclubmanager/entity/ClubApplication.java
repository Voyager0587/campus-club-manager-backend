package com.club.campusclubmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.club.campusclubmanager.enums.ApplicationStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社团申请实体
 */
@Data
@TableName("club_application")
public class ClubApplication {
    /**
     * 申请ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 社团ID
     */
    private Long clubId;

    /**
     * 申请用户ID
     */
    private Long userId;

    /**
     * 申请状态
     */
    private ApplicationStatus status;

    /**
     * 申请理由
     */
    private String reason;

    /**
     * 审核备注（管理员填写）
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
