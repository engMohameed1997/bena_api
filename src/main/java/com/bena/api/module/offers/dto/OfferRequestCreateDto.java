package com.bena.api.module.offers.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO لإنشاء طلب عرض
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferRequestCreateDto {

    @NotNull(message = "معرف العرض مطلوب")
    private UUID offerId;

    @Size(max = 2000, message = "الرسالة يجب أن لا تتجاوز 2000 حرف")
    private String message;

    @Size(max = 20, message = "رقم الهاتف يجب أن لا يتجاوز 20 حرف")
    private String phone;

    private Integer projectArea;
}
