package com.bena.api.module.offers.dto;

import com.bena.api.module.offers.entity.OfferRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO لتحديث حالة طلب العرض
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferRequestUpdateDto {

    @NotNull(message = "الحالة مطلوبة")
    private OfferRequestStatus status;

    private String providerNotes;
}
