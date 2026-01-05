package com.club.campusclubmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 发布通知请求
 */
@Data
public class PublishNotificationRequest {
    /**
     * 通知标题
     */
    @NotBlank(message = "通知标题不能为空")
    @Size(max = 100, message = "通知标题不能超过100个字符")
    private String title;

    /**
     * 通知内容
     */
    @NotBlank(message = "通知内容不能为空")
    @Size(max = 1000, message = "通知内容不能超过1000个字符")
    private String content;

    /**
     * 通知类型（system, audit, activity, club）
     */
    @NotBlank(message = "通知类型不能为空")
    private String type;

    /**
     * 优先级（LOW, NORMAL, HIGH, URGENT）
     */
    @NotNull(message = "优先级不能为空")
    private String priority;

    /**
     * 接收用户ID列表（批量发送时使用）
     */
    private List<Long> userIds;

    /**
     * 社团ID（向社团所有成员发送时使用）
     */
    private Long clubId;

    /**
     * 关联类型（可选，如activity, club等）
     */
    private String relatedType;

    /**
     * 关联ID（可选）
     */
    private Long relatedId;
}