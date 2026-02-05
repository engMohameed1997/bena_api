package com.bena.api.module.offers.dto;

import com.bena.api.module.offers.entity.OfferRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO لعرض طلب العرض
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferRequestResponse {
    private UUID id;
    private OfferRequestStatus status;
    private String statusArabic;
    private String message;
    private String phone;
    private Integer projectArea;
    private String providerNotes;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // بيانات العرض
    private UUID offerId;
    private String offerTitle;
    private String offerCoverImageData;

    // بيانات مقدم الطلب (للمهني)
    private UUID userId;
    private String userName;
    private String userPhone;
    private String userEmail;
}
