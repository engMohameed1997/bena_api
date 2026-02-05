package com.bena.api.module.offers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO لصور العرض
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferImageDto {
    private Long id;
    private String imageData;
    private String imageUrl;
    private String caption;
    private Integer displayOrder;
}
