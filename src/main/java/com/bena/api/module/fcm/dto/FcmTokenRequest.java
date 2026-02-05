package com.bena.api.module.fcm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 📦 FcmTokenRequest DTO
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * Purpose: Request body for saving FCM token
 * Security: userId extracted from JWT (NOT from request body)
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FcmTokenRequest {

    @NotBlank(message = "FCM token is required")
    private String fcmToken;

    private String deviceType; // Optional: "Android", "iOS", "Web"
}
