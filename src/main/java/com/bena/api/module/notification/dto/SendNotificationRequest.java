package com.bena.api.module.notification.dto;

import com.bena.api.module.notification.entity.AppNotification;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class SendNotificationRequest {
    @NotBlank(message = "العنوان مطلوب")
    private String title;
    
    @NotBlank(message = "الرسالة مطلوبة")
    private String message;
    
    private AppNotification.NotificationType type = AppNotification.NotificationType.INFO;
    
    private UUID userId;
}
