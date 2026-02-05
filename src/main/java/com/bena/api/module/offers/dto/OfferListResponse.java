package com.bena.api.module.offers.dto;

import com.bena.api.module.offers.entity.OfferType;
import com.bena.api.module.offers.entity.PriceUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO مختصر لعرض العروض في القائمة
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferListResponse {
    private UUID id;
    private String title;
    private OfferType offerType;
    private String offerTypeArabic;
    private BigDecimal basePrice;
    private PriceUnit priceUnit;
    private String priceUnitArabic;
    private Integer executionDays;
    private String coverImageUrl;
    private String coverImageData;
    private Boolean isFeatured;
    private Integer viewCount;
    private String city;
    private String area;
    private OffsetDateTime createdAt;

    // بيانات مختصرة لمقدم العرض
    private Long providerId;  // معرف مقدم العرض للربط مع صفحته
    private String providerName;
    private String providerCategory;
    private String providerCategoryArabic;
    private String providerImageUrl;
    private String providerImageData;
    private Double providerRating;
    private Integer providerReviewCount;
    private Boolean providerVerified;

    // عدد المميزات المشمولة
    private Integer includedFeaturesCount;
}
