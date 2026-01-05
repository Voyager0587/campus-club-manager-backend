package com.club.campusclubmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.campusclubmanager.entity.ClubMember;
import com.club.campusclubmanager.entity.Notification;
import com.club.campusclubmanager.entity.UserNotificationSetting;
import com.club.campusclubmanager.enums.NotificationChannel;
import com.club.campusclubmanager.enums.NotificationPriority;
import com.club.campusclubmanager.enums.NotificationType;
import com.club.campusclubmanager.exception.BusinessException;
import com.club.campusclubmanager.mapper.ClubMemberMapper;
import com.club.campusclubmanager.mapper.NotificationMapper;
import com.club.campusclubmanager.mapper.UserNotificationSettingMapper;
import com.club.campusclubmanager.notification.channel.NotificationChannelStrategy;
import com.club.campusclubmanager.service.NotificationService;
import com.club.campusclubmanager.vo.NotificationSettingVO;
import com.club.campusclubmanager.vo.NotificationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通知服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
        implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final UserNotificationSettingMapper settingMapper;
    private final ClubMemberMapper clubMemberMapper;
    private final List<NotificationChannelStrategy> channelStrategies;

    // 渠道策略映射
    private Map<NotificationChannel, NotificationChannelStrategy> strategyMap;

    /**
     * 初始化策略映射
     */
    private Map<NotificationChannel, NotificationChannelStrategy> getStrategyMap() {
        if (strategyMap == null) {
            strategyMap = channelStrategies.stream()
                    .collect(Collectors.toMap(
                            NotificationChannelStrategy::getChannel,
                            Function.identity()
                    ));
        }
        return strategyMap;
    }

    @Override
    @Async("notificationExecutor")
    public void sendNotification(Long userId, String title, String content,
                                NotificationType type, String relatedType, Long relatedId,
                                NotificationPriority priority) {
        try {
            // 获取用户通知设置
            UserNotificationSetting setting = getUserSetting(userId);

            // 检查是否启用该类型通知
            if (!isNotificationEnabled(setting, type)) {
                log.debug("用户已禁用该类型通知: userId={}, type={}", userId, type);
                return;
            }

            // 发送站内消息
            if (shouldSendInApp(setting, type)) {
                sendInAppNotification(userId, title, content, type, relatedType, relatedId, priority);
            }

            // 发送邮件
            if (shouldSendEmail(setting, type)) {
                sendEmailNotification(userId, title, content, type);
            }
        } catch (Exception e) {
            log.error("发送通知失败: userId={}, title={}", userId, title, e);
            // 不抛出异常，避免影响主业务流程
        }
    }

    @Override
    @Async("notificationExecutor")
    public void sendBatchNotification(List<Long> userIds, String title, String content,
                                     NotificationType type, String relatedType, Long relatedId,
                                     NotificationPriority priority) {
        try {
            // 批量插入站内消息
            List<Notification> notifications = userIds.stream()
                    .map(userId -> {
                        UserNotificationSetting setting = getUserSetting(userId);
                        if (!isNotificationEnabled(setting, type) || !shouldSendInApp(setting, type)) {
                            return null;
                        }
                        Notification notification = new Notification();
                        notification.setUserId(userId);
                        notification.setTitle(title);
                        notification.setContent(content);
                        notification.setType(type);
                        notification.setRelatedType(relatedType);
                        notification.setRelatedId(relatedId);
                        notification.setPriority(priority.getLevel());
                        notification.setReadFlag(0);
                        return notification;
                    })
                    .filter(n -> n != null)
                    .collect(Collectors.toList());

            if (!notifications.isEmpty()) {
                notificationMapper.batchInsert(notifications);
                log.debug("批量发送站内消息成功: count={}, title={}", notifications.size(), title);
            }

            // 批量发送邮件（逐个发送，避免被判定为垃圾邮件）
            for (Long userId : userIds) {
                UserNotificationSetting setting = getUserSetting(userId);
                if (isNotificationEnabled(setting, type) && shouldSendEmail(setting, type)) {
                    sendEmailNotification(userId, title, content, type);
                }
            }
        } catch (Exception e) {
            log.error("批量发送通知失败: title={}", title, e);
        }
    }

    @Override
    public Page<NotificationVO> getUserNotifications(Long userId, Integer pageNum, Integer pageSize,
                                                     String type, Boolean readFlag) {
        Page<Notification> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        if (type != null) {
            wrapper.eq(Notification::getType, NotificationType.fromCode(type));
        }
        if (readFlag != null) {
            wrapper.eq(Notification::getReadFlag, readFlag ? 1 : 0);
        }
        wrapper.orderByDesc(Notification::getCreateTime);

        Page<Notification> notificationPage = notificationMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<NotificationVO> voPage = new Page<>(pageNum, pageSize, notificationPage.getTotal());
        List<NotificationVO> voList = notificationPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            throw new BusinessException("通知不存在或无权限");
        }

        notification.setReadFlag(1);
        notification.setReadTime(LocalDateTime.now());
        notificationMapper.updateById(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return notificationMapper.countUnread(userId);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            throw new BusinessException("通知不存在或无权限");
        }
        notificationMapper.deleteById(notificationId);
    }

    @Override
    public NotificationSettingVO getNotificationSetting(Long userId) {
        UserNotificationSetting setting = getUserSetting(userId);
        NotificationSettingVO vo = new NotificationSettingVO();
        BeanUtils.copyProperties(setting, vo);
        vo.setEmailEnabled(setting.getEmailEnabled() == 1);
        vo.setInAppEnabled(setting.getInAppEnabled() == 1);
        vo.setAuditNotification(setting.getAuditNotification() == 1);
        vo.setActivityNotification(setting.getActivityNotification() == 1);
        vo.setClubNotification(setting.getClubNotification() == 1);
        vo.setSystemNotification(setting.getSystemNotification() == 1);
        return vo;
    }

    @Override
    @Transactional
    public void updateNotificationSetting(Long userId, NotificationSettingVO vo) {
        UserNotificationSetting setting = getUserSetting(userId);
        if (vo.getEmailEnabled() != null) {
            setting.setEmailEnabled(vo.getEmailEnabled() ? 1 : 0);
        }
        if (vo.getInAppEnabled() != null) {
            setting.setInAppEnabled(vo.getInAppEnabled() ? 1 : 0);
        }
        if (vo.getAuditNotification() != null) {
            setting.setAuditNotification(vo.getAuditNotification() ? 1 : 0);
        }
        if (vo.getActivityNotification() != null) {
            setting.setActivityNotification(vo.getActivityNotification() ? 1 : 0);
        }
        if (vo.getClubNotification() != null) {
            setting.setClubNotification(vo.getClubNotification() ? 1 : 0);
        }
        if (vo.getSystemNotification() != null) {
            setting.setSystemNotification(vo.getSystemNotification() ? 1 : 0);
        }
        settingMapper.updateById(setting);
    }

    /**
     * 获取用户通知设置（如果不存在则创建默认设置）
     */
    private UserNotificationSetting getUserSetting(Long userId) {
        LambdaQueryWrapper<UserNotificationSetting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserNotificationSetting::getUserId, userId);
        UserNotificationSetting setting = settingMapper.selectOne(wrapper);

        if (setting == null) {
            // 创建默认设置
            setting = new UserNotificationSetting();
            setting.setUserId(userId);
            setting.setEmailEnabled(1);
            setting.setInAppEnabled(1);
            setting.setAuditNotification(1);
            setting.setActivityNotification(1);
            setting.setClubNotification(1);
            setting.setSystemNotification(1);
            settingMapper.insert(setting);
        }

        return setting;
    }

    /**
     * 检查是否启用该类型通知
     */
    private boolean isNotificationEnabled(UserNotificationSetting setting, NotificationType type) {
        return switch (type) {
            case SYSTEM -> setting.getSystemNotification() == 1;
            case AUDIT -> setting.getAuditNotification() == 1;
            case ACTIVITY -> setting.getActivityNotification() == 1;
            case CLUB -> setting.getClubNotification() == 1;
        };
    }

    /**
     * 是否应该发送站内消息
     */
    private boolean shouldSendInApp(UserNotificationSetting setting, NotificationType type) {
        return setting.getInAppEnabled() == 1 && isNotificationEnabled(setting, type);
    }

    /**
     * 是否应该发送邮件
     */
    private boolean shouldSendEmail(UserNotificationSetting setting, NotificationType type) {
        return setting.getEmailEnabled() == 1 && isNotificationEnabled(setting, type);
    }

    /**
     * 发送站内消息
     */
    private void sendInAppNotification(Long userId, String title, String content,
                                      NotificationType type, String relatedType, Long relatedId,
                                      NotificationPriority priority) {
        NotificationChannelStrategy strategy = getStrategyMap().get(NotificationChannel.IN_APP);
        if (strategy != null && strategy.supports(type)) {
            // 先插入数据库
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setTitle(title);
            notification.setContent(content);
            notification.setType(type);
            notification.setRelatedType(relatedType);
            notification.setRelatedId(relatedId);
            notification.setPriority(priority.getLevel());
            notification.setReadFlag(0);
            notificationMapper.insert(notification);
        }
    }

    /**
     * 发送邮件通知
     */
    private void sendEmailNotification(Long userId, String title, String content, NotificationType type) {
        NotificationChannelStrategy strategy = getStrategyMap().get(NotificationChannel.EMAIL);
        if (strategy != null && strategy.supports(type)) {
            // 重试机制（最多3次）
            int maxRetries = 3;
            for (int i = 0; i < maxRetries; i++) {
                if (strategy.send(userId, title, content, type)) {
                    return;
                }
                if (i < maxRetries - 1) {
                    try {
                        Thread.sleep(1000 * (i + 1)); // 指数退避
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            log.warn("邮件发送失败（已重试{}次）: userId={}, title={}", maxRetries, userId, title);
        }
    }

    /**
     * 转换为VO
     */
    private NotificationVO convertToVO(Notification notification) {
        NotificationVO vo = new NotificationVO();
        BeanUtils.copyProperties(notification, vo);
        vo.setRead(notification.getReadFlag() == 1);
        return vo;
    }

    @Override
    @Async("notificationExecutor")
    public void sendToClubMembers(Long clubId, String title, String content,
                                 NotificationType type, String relatedType, Long relatedId,
                                 NotificationPriority priority) {
        try {
            // 查询社团所有成员的用户ID
            LambdaQueryWrapper<ClubMember> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ClubMember::getClubId, clubId);
            List<ClubMember> members = clubMemberMapper.selectList(wrapper);

            if (members.isEmpty()) {
                log.warn("社团没有成员，无法发送通知: clubId={}", clubId);
                return;
            }

            // 提取所有成员的用户ID
            List<Long> userIds = members.stream()
                    .map(ClubMember::getUserId)
                    .collect(Collectors.toList());

            // 使用批量发送通知的方法
            sendBatchNotification(userIds, title, content, type, relatedType, relatedId, priority);

            log.info("成功向社团所有成员发送通知: clubId={}, memberCount={}, title={}",
                     clubId, userIds.size(), title);
        } catch (Exception e) {
            log.error("向社团成员发送通知失败: clubId={}, title={}", clubId, title, e);
        }
    }
}

