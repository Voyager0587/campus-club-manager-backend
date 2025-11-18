package com.club.campusclubmanager.vo;

import com.club.campusclubmanager.enums.MemberRole;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社团成员视图对象
 */
@Data
public class ClubMemberVO {

    /**
     * 记录ID
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
}
