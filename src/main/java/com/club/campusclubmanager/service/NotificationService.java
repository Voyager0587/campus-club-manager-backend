 package com.club.campusclubmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.campusclubmanager.enums.NotificationPriority;
import com.club.campusclubmanager.enums.NotificationType;
import com.club.campusclubmanager.vo.NotificationSettingVO;
import com.club.campusclubmanager.vo.NotificationVO;

import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService {
    /**
     * 发送通知（根据用户偏好自动选择渠道）
     *
     * @param userId 用户ID
     * @param title 标题
     * @param content 内容
     * @param type 通知类型
     * @param relatedType 关联类型
     * @param relatedId 关联ID
     * @param priority 优先级
     */
    void sendNotification(Long userId, String title, String content,
                         NotificationType type, String relatedType, Long relatedId,
                         NotificationPriority priority);

    /**
     * 批量发送通知
     *
     * @param userIds 用户ID列表
     * @param title 标题
     * @param content 内容
     * @param type 通知类型
     * @param relatedType 关联类型
     * @param relatedId 关联ID
     * @param priority 优先级
     */
    void sendBatchNotification(List<Long> userIds, String title, String content,
                              NotificationType type, String relatedType, Long relatedId,
                              NotificationPriority priority);

    /**
     * 查询用户通知列表
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param type 通知类型（可选）
     * @param readFlag 是否已读（可选）
     * @return 分页结果
     */
    Page<NotificationVO> getUserNotifications(Long userId, Integer pageNum, Integer pageSize,
                                            String type, Boolean readFlag);

    /**
     * 标记通知为已读
     *
     * @param notificationId 通知ID
     * @param userId 用户ID
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * 标记用户所有通知为已读
     *
     * @param userId 用户ID
     */
    void markAllAsRead(Long userId);

    /**
     * 获取未读数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    Long getUnreadCount(Long userId);

    /**
     * 删除通知
     *
     * @param notificationId 通知ID
     * @param userId 用户ID
     */
    void deleteNotification(Long notificationId, Long userId);

    /**
     * 获取用户通知设置
     *
     * @param userId 用户ID
     * @return 通知设置
     */
    NotificationSettingVO getNotificationSetting(Long userId);

    /**
     * 更新用户通知设置
     *
     * @param userId 用户ID
     * @param setting 设置信息
     */
    void updateNotificationSetting(Long userId, NotificationSettingVO setting);

    /**
     * 发送通知给社团所有成员
     *
     * @param clubId 社团ID
     * @param title 标题
     * @param content 内容
     * @param type 通知类型
     * @param relatedType 关联类型
     * @param relatedId 关联ID
     * @param priority 优先级
     */
    void sendToClubMembers(Long clubId, String title, String content,
                          NotificationType type, String relatedType, Long relatedId,
                          NotificationPriority priority);
}


