package com.bena.api.module.offers.dto;

import com.bena.api.module.offers.entity.OfferType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO لفلترة العروض
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferFilterRequest {
    private OfferType offerType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String city;
    private String area;
    private Integer minArea;
    private Integer maxArea;
    private Boolean isFeatured;
    private Long providerId;  // للفلترة حسب مقدم الخدمة
    private Boolean verifiedOnly;
    private String sortBy; // price, rating, date, views
    private String sortDirection; // asc, desc
}
