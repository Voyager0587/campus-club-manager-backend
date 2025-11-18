package com.club.campusclubmanager.vo;

import com.club.campusclubmanager.enums.ClubStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社团详情视图对象
 */
@Data
public class ClubDetailVO {

    /**
     * 社团ID
     */
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
     * 创建者用户名
     */
    private String createUsername;

    /**
     * 成员数量
     */
    private Integer memberCount;

    /**
     * 当前用户是否已加入
     */
    private Boolean isJoined;

    /**
     * 当前用户在该社团的角色（如果已加入）
     */
    private String memberRole;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
