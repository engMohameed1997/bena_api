package com.bena.api.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * طلب تسجيل الدخول بواسطة Apple
 * Apple Sign-In Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppleAuthRequest {

    @NotBlank(message = "Apple Identity Token مطلوب")
    private String identityToken;

    /**
     * Authorization Code من Apple (اختياري)
     */
    private String authorizationCode;

    /**
     * بيانات المستخدم (قد تكون null بعد أول تسجيل دخول)
     * Apple يرسل البيانات فقط في أول مرة
     */
    private AppleUser user;

    /**
     * الدور المطلوب (اختياري) - إذا كان مستخدم جديد
     */
    private String role;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppleUser {
        private String email;
        private AppleName name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppleName {
        private String firstName;
        private String lastName;
    }
}
