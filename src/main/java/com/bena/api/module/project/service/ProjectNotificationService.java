package com.bena.api.module.project.service;

import com.bena.api.module.project.dto.NotificationResponse;
import com.bena.api.module.project.entity.Bid;
import com.bena.api.module.project.entity.Notification;
import com.bena.api.module.project.entity.Project;
import com.bena.api.module.project.repository.ProjectNotificationRepository;
import com.bena.api.module.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectNotificationService {

    private final ProjectNotificationRepository notificationRepository;

    @Transactional
    public void notifyNewBid(Bid bid) {
        Notification notification = Notification.builder()
                .user(bid.getClient())
                .title("عرض جديد")
                .message("تلقيت عرضاً جديداً من " + bid.getProvider().getFullName())
                .notificationType(Notification.NotificationType.NEW_BID)
                .referenceId(bid.getId())
                .referenceType("BID")
                .priority(Notification.NotificationPriority.HIGH)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyBidAccepted(Bid bid) {
        Notification notification = Notification.builder()
                .user(bid.getProvider())
                .title("تم قبول عرضك")
                .message("قام " + bid.getClient().getFullName() + " بقبول عرضك")
                .notificationType(Notification.NotificationType.BID_ACCEPTED)
                .referenceId(bid.getId())
                .referenceType("BID")
                .priority(Notification.NotificationPriority.HIGH)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyBidRejected(Bid bid) {
        Notification notification = Notification.builder()
                .user(bid.getProvider())
                .title("تم رفض عرضك")
                .message("قام " + bid.getClient().getFullName() + " برفض عرضك")
                .notificationType(Notification.NotificationType.BID_REJECTED)
                .referenceId(bid.getId())
                .referenceType("BID")
                .priority(Notification.NotificationPriority.MEDIUM)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyProjectCreated(Project project) {
        Notification clientNotification = Notification.builder()
                .user(project.getClient())
                .title("تم إنشاء مشروع جديد")
                .message("تم إنشاء مشروع: " + project.getTitle())
                .notificationType(Notification.NotificationType.PROJECT_CREATED)
                .referenceId(project.getId())
                .referenceType("PROJECT")
                .priority(Notification.NotificationPriority.HIGH)
                .build();
        notificationRepository.save(clientNotification);

        Notification providerNotification = Notification.builder()
                .user(project.getProvider())
                .title("مشروع جديد")
                .message("تم تعيينك في مشروع: " + project.getTitle())
                .notificationType(Notification.NotificationType.PROJECT_CREATED)
                .referenceId(project.getId())
                .referenceType("PROJECT")
                .priority(Notification.NotificationPriority.HIGH)
                .build();
        notificationRepository.save(providerNotification);
    }

    @Transactional
    public void notifyProjectStatusChanged(Project project) {
        String message = "تم تحديث حالة المشروع: " + project.getTitle() + " إلى " + project.getStatus();
        
        Notification clientNotification = Notification.builder()
                .user(project.getClient())
                .title("تحديث حالة المشروع")
                .message(message)
                .notificationType(Notification.NotificationType.PROJECT_STARTED)
                .referenceId(project.getId())
                .referenceType("PROJECT")
                .priority(Notification.NotificationPriority.MEDIUM)
                .build();
        notificationRepository.save(clientNotification);

        Notification providerNotification = Notification.builder()
                .user(project.getProvider())
                .title("تحديث حالة المشروع")
                .message(message)
                .notificationType(Notification.NotificationType.PROJECT_STARTED)
                .referenceId(project.getId())
                .referenceType("PROJECT")
                .priority(Notification.NotificationPriority.MEDIUM)
                .build();
        notificationRepository.save(providerNotification);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(User user, Pageable pageable) {
        return notificationRepository.findByUser(user, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsRead(user, false);
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsReadByUser(user);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .referenceId(notification.getReferenceId())
                .referenceType(notification.getReferenceType())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .actionUrl(notification.getActionUrl())
                .priority(notification.getPriority())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
