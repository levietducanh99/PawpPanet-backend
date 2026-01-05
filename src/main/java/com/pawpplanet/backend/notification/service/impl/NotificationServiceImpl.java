package com.pawpplanet.backend.notification.service.impl;

import com.pawpplanet.backend.notification.entity.NotificationEntity;
import com.pawpplanet.backend.notification.repository.NotificationRepository;
import com.pawpplanet.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void createNotification(Long targetUserId, String type, Long postId) {
        NotificationEntity notification = new NotificationEntity();
        notification.setUserId(targetUserId);
        notification.setType(type);
        notification.setReferenceId(postId);

        notificationRepository.save(notification);
    }
}
