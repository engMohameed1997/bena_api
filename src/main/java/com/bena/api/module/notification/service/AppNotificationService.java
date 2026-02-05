package com.bena.api.module.notification.service;

import com.bena.api.module.notification.entity.AppNotification;
import com.bena.api.module.notification.repository.AppNotificationRepository;
import com.bena.api.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppNotificationService {

    private final AppNotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * إرسال إشعار لمستخدم محدد
     */
    @Async
    @Transactional
    public void sendToUser(UUID userId, String title, String message, AppNotification.NotificationType type) {
        AppNotification notification = AppNotification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .build();
        
        notificationRepository.save(notification);
        log.info("🔔 Notification sent to user {}: {}", userId, title);
    }

    /**
     * إرسال إشعار لجميع المستخدمين
     */
    @Async
    @Transactional
    public void sendToAll(String title, String message, AppNotification.NotificationType type) {
        AppNotification notification = AppNotification.builder()
                .userId(null) // Global
                .title(title)
                .message(message)
                .type(type)
                .build();
        
        notificationRepository.save(notification);
        log.info("🔔 Global notification created: {}", title);
    }

    public Page<AppNotification> getUserNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsRead(userId);
    }
}
