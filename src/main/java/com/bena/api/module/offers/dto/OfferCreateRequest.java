package com.bena.api.module.offers.dto;

import com.bena.api.module.offers.entity.OfferType;
import com.bena.api.module.offers.entity.PriceUnit;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO لإنشاء عرض جديد
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferCreateRequest {

    @NotBlank(message = "عنوان العرض مطلوب")
    @Size(max = 200, message = "العنوان يجب أن لا يتجاوز 200 حرف")
    private String title;

    @Size(max = 5000, message = "الوصف يجب أن لا يتجاوز 5000 حرف")
    private String description;

    @NotNull(message = "نوع العرض مطلوب")
    private OfferType offerType;

    @NotNull(message = "السعر الأساسي مطلوب")
    @DecimalMin(value = "0.0", inclusive = false, message = "السعر يجب أن يكون أكبر من صفر")
    private BigDecimal basePrice;

    private PriceUnit priceUnit;

    @Min(value = 1, message = "الحد الأدنى للمساحة يجب أن يكون 1 على الأقل")
    private Integer minArea;

    @Max(value = 100000, message = "الحد الأقصى للمساحة يجب أن لا يتجاوز 100000")
    private Integer maxArea;

    @Min(value = 1, message = "مدة التنفيذ يجب أن تكون يوم واحد على الأقل")
    private Integer executionDays;

    private String coverImageData;

    @Size(max = 100, message = "اسم المدينة يجب أن لا يتجاوز 100 حرف")
    private String city;

    @Size(max = 100, message = "اسم المنطقة يجب أن لا يتجاوز 100 حرف")
    private String area;

    private List<OfferFeatureDto> features;

    private List<OfferImageDto> images;
}
