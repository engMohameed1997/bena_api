package com.bena.api.module.user.dto;

import com.bena.api.module.user.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private UUID id;
    private Long workerId;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private Boolean isActive;
    private String governorate;
    private String city;
    private String profilePictureUrl;
    private Boolean profileCompleted;
    private Boolean documentVerified;
    private String verificationStatus;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static UserResponse from(User user) {
        return from(user, null);
    }

    public static UserResponse from(User user, Long workerId) {
        // استخدام صورة الملف الشخصي من التسجيل الاجتماعي إذا لم تكن موجودة
        String pictureUrl = user.getProfilePictureUrl();
        if (pictureUrl == null && user.getProfilePicture() != null) {
            pictureUrl = user.getProfilePicture();
        }

        return UserResponse.builder()
                .id(user.getId())
                .workerId(workerId)
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone()) // ✅ رقم كامل للأدمن
                .role(user.getRole().name()) // ✅ إرجاع الدور للأدمن (للفلترة)
                .isActive(user.getIsActive())
                .governorate(user.getGovernorate())
                .city(user.getCity())
                .profilePictureUrl(pictureUrl)
                .profileCompleted(user.getProfileCompleted())
                .documentVerified(user.getDocumentVerified())
                .verificationStatus(user.getVerificationStatus() != null ? user.getVerificationStatus().name() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
