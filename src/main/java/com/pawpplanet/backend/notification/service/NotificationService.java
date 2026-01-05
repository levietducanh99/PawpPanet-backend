package com.pawpplanet.backend.notification.service;

public interface NotificationService {
    void createNotification(Long targetUserId, String type, Long postId);
}
