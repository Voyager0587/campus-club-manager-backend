package com.club.campusclubmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.club.campusclubmanager.enums.ClubStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社团实体
 */
@Data
@TableName("club")
public class Club {
    /**
     * 社团ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 社团名称
     */
    private String name;

    /**
     * 社团简介
     */
    private String description;

    /**
     * 社团图标URL
     */
    private String logo;

    /**
     * 社团状态
     */
    private ClubStatus status;

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
