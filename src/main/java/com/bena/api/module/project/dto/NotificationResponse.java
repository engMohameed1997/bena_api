package com.bena.api.module.project.dto;

import com.bena.api.module.project.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private UUID id;
    private String title;
    private String message;
    private Notification.NotificationType notificationType;
    private UUID referenceId;
    private String referenceType;
    private Boolean isRead;
    private LocalDateTime readAt;
    private String actionUrl;
    private Notification.NotificationPriority priority;
    private LocalDateTime createdAt;
}
