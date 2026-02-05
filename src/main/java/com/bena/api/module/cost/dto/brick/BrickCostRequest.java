package com.bena.api.module.cost.dto.brick;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * طلب حساب كلفة الطابوق
 * Brick Cost Calculation Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrickCostRequest {

    /**
     * مساحة الجدران الكلية (م²)
     */
    @NotNull(message = "مساحة الجدران مطلوبة")
    @Positive(message = "مساحة الجدران يجب أن تكون أكبر من صفر")
    private BigDecimal wallArea;

    /**
     * مساحة الفتحات (أبواب + شبابيك) (م²)
     */
    @Min(value = 0, message = "مساحة الفتحات لا يمكن أن تكون سالبة")
    @Builder.Default
    private BigDecimal openingsArea = BigDecimal.ZERO;

    /**
     * كود نوع الطابوق (اختياري - إذا لم يُحدد يستخدم الافتراضي)
     */
    private String brickTypeCode;

    /**
     * عدد الطابوق لكل م² (اختياري - يُحسب من نوع الطابوق إذا لم يُحدد)
     */
    @Positive(message = "عدد الطابوق لكل م² يجب أن يكون أكبر من صفر")
    private Integer bricksPerM2;

    /**
     * نسبة الهدر (مثال: 0.07 = 7%)
     */
    @Min(value = 0, message = "نسبة الهدر لا يمكن أن تكون سالبة")
    @Builder.Default
    private BigDecimal wastePercentage = new BigDecimal("0.07");

    /**
     * سعر الطابوق لكل 1000 طابوقة (بالعملة المحددة)
     */
    @Positive(message = "سعر الطابوق يجب أن يكون أكبر من صفر")
    private BigDecimal brickPricePer1000;

    /**
     * كلفة العمالة لكل 1000 طابوقة
     */
    @Positive(message = "كلفة العمالة يجب أن تكون أكبر من صفر")
    private BigDecimal laborPricePer1000;

    /**
     * كلفة المونة (سمنت + رمل) لكل م²
     */
    @Positive(message = "كلفة المونة يجب أن تكون أكبر من صفر")
    private BigDecimal mortarCostPerM2;

    /**
     * العملة (IQD, USD)
     */
    @Builder.Default
    private String currency = "IQD";

    /**
     * المنطقة (لجلب الأسعار المحلية)
     */
    @Builder.Default
    private String region = "baghdad";
}
