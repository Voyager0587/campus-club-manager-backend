package com.club.campusclubmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.club.campusclubmanager.enums.MemberRole;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社团成员实体
 */
@Data
@TableName("club_member")
public class ClubMember {
    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 社团ID
     */
    private Long clubId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 成员角色
     */
    private MemberRole role;

    /**
     * 加入时间
     */
    private LocalDateTime joinTime;

    /**
     * 备注
     */
    private String memo;

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
