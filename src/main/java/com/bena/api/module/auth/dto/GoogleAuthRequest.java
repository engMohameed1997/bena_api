package com.bena.api.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * طلب تسجيل الدخول بواسطة Google
 * Google Sign-In Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAuthRequest {

    @NotBlank(message = "Google ID Token مطلوب")
    private String idToken;

    /**
     * الدور المطلوب (اختياري) - إذا كان مستخدم جديد
     * USER, WORKER, CLIENT, CONTRACTOR, ENGINEER, DESIGNER
     */
    private String role;
}
