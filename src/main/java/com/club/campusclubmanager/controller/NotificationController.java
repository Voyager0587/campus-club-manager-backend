package com.club.campusclubmanager.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.campusclubmanager.common.Result;
import com.club.campusclubmanager.dto.MarkReadRequest;
import com.club.campusclubmanager.dto.PublishNotificationRequest;
import com.club.campusclubmanager.dto.UpdateNotificationSettingRequest;
import com.club.campusclubmanager.enums.NotificationPriority;
import com.club.campusclubmanager.enums.NotificationType;
import com.club.campusclubmanager.service.NotificationService;
import com.club.campusclubmanager.vo.NotificationSettingVO;
import com.club.campusclubmanager.vo.NotificationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 通知控制器
 */
@Tag(name = "通知管理", description = "通知查询、标记已读、设置等接口")
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@SaCheckLogin 
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 分页查询我的通知
     */
    @Operation(summary = "分页查询我的通知", description = "需要登录，支持按类型和已读状态筛选")
    @GetMapping("/list")
    public Result<Page<NotificationVO>> listNotifications(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "通知类型") @RequestParam(required = false) String type,
            @Parameter(description = "是否已读") @RequestParam(required = false) Boolean readFlag
    ) {
        Long userId = getCurrentUserId();
        Page<NotificationVO> page = notificationService.getUserNotifications(
                userId, pageNum, pageSize, type, readFlag);
        return Result.success(page);
    }

    /**
     * 获取未读数量
     */
    @Operation(summary = "获取未读数量", description = "需要登录，获取当前用户的未读通知数量")
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount() {
        Long userId = getCurrentUserId();
        Long count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 标记已读
     */
    @Operation(summary = "标记已读", description = "需要登录，标记单条或全部通知为已读")
    @PutMapping("/mark-read")
    public Result<Void> markAsRead(@Valid @RequestBody MarkReadRequest request) {
        Long userId = getCurrentUserId();
        if (request.getMarkAll()) {
            notificationService.markAllAsRead(userId);
        } else {
            if (request.getNotificationId() == null) {
                return Result.fail("通知ID不能为空");
            }
            notificationService.markAsRead(request.getNotificationId(), userId);
        }
        return Result.success("操作成功", null);
    }

    /**
     * 删除通知
     */
    @Operation(summary = "删除通知", description = "需要登录，删除指定的通知")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(
            @Parameter(description = "通知ID") @PathVariable Long id
    ) {
        Long userId = getCurrentUserId();
        notificationService.deleteNotification(id, userId);
        return Result.success("删除成功", null);
    }

    /**
     * 获取通知设置
     */
    @Operation(summary = "获取通知设置", description = "需要登录，获取当前用户的通知偏好设置")
    @GetMapping("/settings")
    public Result<NotificationSettingVO> getNotificationSetting() {
        Long userId = getCurrentUserId();
        NotificationSettingVO setting = notificationService.getNotificationSetting(userId);
        return Result.success(setting);
    }

    /**
     * 更新通知设置
     */
    @Operation(summary = "更新通知设置", description = "需要登录，更新当前用户的通知偏好设置")
    @PutMapping("/settings")
    public Result<Void> updateNotificationSetting(
            @Valid @RequestBody UpdateNotificationSettingRequest request
    ) {
        Long userId = getCurrentUserId();
        NotificationSettingVO vo = new NotificationSettingVO();
        BeanUtils.copyProperties(request, vo);
        notificationService.updateNotificationSetting(userId, vo);
        return Result.success("更新成功", null);
    }

    /**
     * 系统管理员发布通知
     */
    @Operation(summary = "发布通知", description = "仅系统管理员可访问，发布通知给指定用户或所有用户")
    @SaCheckRole("system_admin")
    @PostMapping("/systempublish")
    public Result<Void> publishNotification1(@Valid @RequestBody PublishNotificationRequest request) {
        try {
            NotificationType type = NotificationType.fromCode(request.getType());
            NotificationPriority priority = NotificationPriority.valueOf(request.getPriority());
            
            if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
                // 批量发送通知给指定用户
                notificationService.sendBatchNotification(
                        request.getUserIds(),
                        request.getTitle(),
                        request.getContent(),
                        type,
                        request.getRelatedType(),
                        request.getRelatedId(),
                        priority
                );
            } else {
                // 如果未指定用户ID，应该提供发送给所有用户的功能
                // 这里暂时返回错误，因为NotificationService中没有提供发送给所有用户的方法
                return Result.fail("请指定接收通知的用户ID列表");
            }
            
            return Result.success("通知发布成功", null);
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail("通知发布失败：" + e.getMessage());
        }
    }

    /**
     * 俱乐部发布通知
     */
    @Operation(summary = "发布通知", description = "仅系统管理员可访问，发布通知给指定用户或所有用户")
    @SaCheckRole(
            value = {"club_admin", "system_admin"},
            mode = SaMode.OR
    )
    @PostMapping("/clubpublish")
    public Result<Void> publishNotification2(@Valid @RequestBody PublishNotificationRequest request) {
        try {
            NotificationType type = NotificationType.fromCode(request.getType());
            NotificationPriority priority = NotificationPriority.valueOf(request.getPriority());
            
            if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
                // 批量发送通知给指定用户
                notificationService.sendBatchNotification(
                        request.getUserIds(),
                        request.getTitle(),
                        request.getContent(),
                        type,
                        request.getRelatedType(),
                        request.getRelatedId(),
                        priority
                );
            } else {
                // 如果未指定用户ID，应该提供发送给所有用户的功能
                // 这里暂时返回错误，因为NotificationService中没有提供发送给所有用户的方法
                return Result.fail("请指定接收通知的用户ID列表");
            }
            
            return Result.success("通知发布成功", null);
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail("通知发布失败：" + e.getMessage());
        }
    }
    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        return Long.parseLong(cn.dev33.satoken.stp.StpUtil.getLoginIdAsString());
    }
}


