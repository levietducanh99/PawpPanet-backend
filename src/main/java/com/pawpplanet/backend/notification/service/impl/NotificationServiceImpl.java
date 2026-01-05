package com.pawpplanet.backend.notification.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawpplanet.backend.common.dto.PagedResult;
import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import com.pawpplanet.backend.notification.dto.CreateNotificationRequest;
import com.pawpplanet.backend.notification.dto.NotificationResponse;
import com.pawpplanet.backend.notification.entity.NotificationEntity;
import com.pawpplanet.backend.notification.enums.NotificationType;
import com.pawpplanet.backend.notification.enums.TargetType;
import com.pawpplanet.backend.notification.repository.NotificationRepository;
import com.pawpplanet.backend.notification.service.NotificationService;
import com.pawpplanet.backend.user.repository.UserRepository;
import com.pawpplanet.backend.utils.SecurityHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SecurityHelper securityHelper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Deprecated
    public void createNotification(Long targetUserId, String type, Long postId) {
        // Backward compatibility
        createNotification(
                targetUserId,
                null,
                NotificationType.valueOf(type),
                TargetType.POST,
                postId,
                new HashMap<>()
        );
    }

    @Override
    @Transactional
    public void createNotification(CreateNotificationRequest request) {
        createNotification(
                request.getRecipientId(),
                request.getActorId(),
                request.getType(),
                request.getTargetType(),
                request.getTargetId(),
                request.getMetadata()
        );
    }

    @Override
    @Transactional
    public void createNotification(
            Long recipientId,
            Long actorId,
            NotificationType type,
            TargetType targetType,
            Long targetId,
            Map<String, Object> metadata
    ) {
        // Don't create notification if actor = recipient
        if (actorId != null && actorId.equals(recipientId)) {
            log.debug("Skipping self-notification for user {}", recipientId);
            return;
        }

        // Verify recipient exists
        if (!userRepository.existsById(recipientId)) {
            log.error("Cannot create notification: recipient {} not found", recipientId);
            return;
        }

        // Enrich metadata with actor info
        Map<String, Object> enrichedMetadata = new HashMap<>();
        if (metadata != null) {
            enrichedMetadata.putAll(metadata);
        }

        if (actorId != null) {
            userRepository.findById(actorId).ifPresent(actor -> {
                enrichedMetadata.put("actorUsername", actor.getUsername());
                enrichedMetadata.put("actorAvatar", actor.getAvatarUrl());
            });
        }

        // Convert metadata to JSON
        String metadataJson;
        try {
            metadataJson = objectMapper.writeValueAsString(enrichedMetadata);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize metadata", e);
            metadataJson = "{}";
        }

        // Create notification
        NotificationEntity notification = NotificationEntity.builder()
                .recipientId(recipientId)
                .actorId(actorId)
                .type(type.name())
                .targetType(targetType.name())
                .targetId(targetId)
                .metadata(metadataJson)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.info("Created notification: type={}, recipient={}, actor={}", type, recipientId, actorId);
    }

    @Override
    public PagedResult<NotificationResponse> getMyNotifications(int page, int size) {
        Long currentUserId = securityHelper.getCurrentUser().getId();

        int zeroBasedPage = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(zeroBasedPage, Math.max(1, size));

        Page<NotificationEntity> notificationPage = notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(currentUserId, pageable);

        return mapToPagedResult(notificationPage);
    }

    @Override
    public PagedResult<NotificationResponse> getMyUnreadNotifications(int page, int size) {
        Long currentUserId = securityHelper.getCurrentUser().getId();

        int zeroBasedPage = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(zeroBasedPage, Math.max(1, size));

        Page<NotificationEntity> notificationPage = notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(currentUserId, pageable);

        return mapToPagedResult(notificationPage);
    }

    @Override
    public Long getUnreadCount() {
        Long currentUserId = securityHelper.getCurrentUser().getId();
        return notificationRepository.countByRecipientIdAndIsReadFalse(currentUserId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Long currentUserId = securityHelper.getCurrentUser().getId();

        int updated = notificationRepository.markAsRead(notificationId, currentUserId);
        if (updated == 0) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        Long currentUserId = securityHelper.getCurrentUser().getId();
        notificationRepository.markAllAsRead(currentUserId);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        Long currentUserId = securityHelper.getCurrentUser().getId();

        int deleted = notificationRepository.deleteByIdAndRecipientId(notificationId, currentUserId);
        if (deleted == 0) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    private NotificationResponse mapToResponse(NotificationEntity entity) {
        NotificationResponse response = new NotificationResponse();
        response.setId(entity.getId());
        response.setType(entity.getType());
        response.setIsRead(entity.getIsRead());
        response.setCreatedAt(entity.getCreatedAt());

        // Map actor info
        if (entity.getActorId() != null) {
            userRepository.findById(entity.getActorId()).ifPresent(actor -> {
                NotificationResponse.ActorInfo actorInfo = new NotificationResponse.ActorInfo();
                actorInfo.setId(actor.getId());
                actorInfo.setUsername(actor.getUsername());
                actorInfo.setAvatarUrl(actor.getAvatarUrl());
                response.setActor(actorInfo);
            });
        }

        // Map target info
        NotificationResponse.TargetInfo targetInfo = new NotificationResponse.TargetInfo();
        targetInfo.setType(entity.getTargetType());
        targetInfo.setId(entity.getTargetId());
        response.setTarget(targetInfo);

        // Parse metadata JSON
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> metadata = objectMapper.readValue(entity.getMetadata(), Map.class);
            response.setMetadata(metadata);
        } catch (Exception e) {
            log.error("Failed to parse metadata for notification {}", entity.getId(), e);
            response.setMetadata(new HashMap<>());
        }

        return response;
    }

    private PagedResult<NotificationResponse> mapToPagedResult(Page<NotificationEntity> page) {
        PagedResult<NotificationResponse> result = new PagedResult<>();
        result.setItems(page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
        result.setTotalElements(page.getTotalElements());
        result.setPage(page.getNumber() + 1);
        result.setSize(page.getSize());
        return result;
    }
}
