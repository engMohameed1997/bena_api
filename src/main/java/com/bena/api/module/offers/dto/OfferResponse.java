package com.bena.api.module.offers.dto;

import com.bena.api.module.offers.entity.OfferType;
import com.bena.api.module.offers.entity.PriceUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO لعرض تفاصيل العرض الكاملة
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferResponse {
    private UUID id;
    private String title;
    private String description;
    private OfferType offerType;
    private String offerTypeArabic;
    private BigDecimal basePrice;
    private PriceUnit priceUnit;
    private String priceUnitArabic;
    private Integer minArea;
    private Integer maxArea;
    private Integer executionDays;
    private String coverImageUrl;
    private String coverImageData;
    private Boolean isActive;
    private Boolean isFeatured;
    private Integer viewCount;
    private String city;
    private String area;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // بيانات مقدم العرض
    private ProviderInfo provider;

    // المميزات والصور
    private List<OfferFeatureDto> features;
    private List<OfferImageDto> images;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderInfo {
        private Long id;
        private String name;
        private String category;
        private String categoryArabic;
        private String profileImageUrl;
        private String profileImageData;
        private Double averageRating;
        private Integer reviewCount;
        private String phoneNumber;
        private String whatsappNumber;
        private Boolean isVerified;
        private String city;
        private String area;
        private Integer experienceYears;
        private Integer completedProjectsCount;
    }
}
