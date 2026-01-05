package com.pawpplanet.backend.notification.service;

import com.pawpplanet.backend.common.dto.PagedResult;
import com.pawpplanet.backend.notification.dto.CreateNotificationRequest;
import com.pawpplanet.backend.notification.dto.NotificationResponse;
import com.pawpplanet.backend.notification.enums.NotificationType;
import com.pawpplanet.backend.notification.enums.TargetType;

import java.util.Map;

public interface NotificationService {

    /**
     * Create notification (simple method for backward compatibility)
     */
    @Deprecated
    void createNotification(Long targetUserId, String type, Long postId);

    /**
     * Create notification with full parameters
     */
    void createNotification(CreateNotificationRequest request);

    /**
     * Helper method to create notification with common parameters
     */
    void createNotification(
            Long recipientId,
            Long actorId,
            NotificationType type,
            TargetType targetType,
            Long targetId,
            Map<String, Object> metadata
    );

    /**
     * Get notifications for current user (paginated)
     */
    PagedResult<NotificationResponse> getMyNotifications(int page, int size);

    /**
     * Get unread notifications for current user (paginated)
     */
    PagedResult<NotificationResponse> getMyUnreadNotifications(int page, int size);

    /**
     * Get unread count for current user
     */
    Long getUnreadCount();

    /**
     * Mark notification as read
     */
    void markAsRead(Long notificationId);

    /**
     * Mark all notifications as read
     */
    void markAllAsRead();

    /**
     * Delete notification
     */
    void deleteNotification(Long notificationId);
}
