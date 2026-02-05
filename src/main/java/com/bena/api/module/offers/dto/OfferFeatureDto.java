package com.bena.api.module.offers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO لمميزات العرض
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferFeatureDto {
    private Long id;
    private String featureText;
    private Boolean isIncluded;
    private Integer displayOrder;
}
