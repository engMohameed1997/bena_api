package com.bena.api.module.user.dto;

import com.bena.api.module.user.enums.VerificationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileStatusResponse {
    private Boolean profileCompleted;
    private Boolean documentVerified;
    private VerificationStatus verificationStatus;
    private String governorate;
    private String city;
    private String documentType;
    private String documentUrl;
    private String rejectionReason;
    private Boolean canUseFullFeatures; // هل يمكن استخدام الميزات الكاملة
    private Boolean isAccountSuspended; // هل الحساب معلق من قبل الإدارة
}
