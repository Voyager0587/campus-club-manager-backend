package com.club.campusclubmanager.vo;

import com.club.campusclubmanager.enums.ClubStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社团视图对象
 */
@Data
public class ClubVO {

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
     * 创建时间
     */
    private LocalDateTime createTime;
}
