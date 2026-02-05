package com.bena.api.module.cost.dto.foundation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * طلب حساب كلفة الأساس والدفان والسبيس
 * Foundation, Fill, and Space Cost Calculation Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoundationCostRequest {

    /**
     * مساحة الأرض (م²)
     */
    @NotNull(message = "مساحة الأرض مطلوبة")
    @Positive(message = "مساحة الأرض يجب أن تكون أكبر من صفر")
    private BigDecimal landArea;

    /**
     * عمق الحفر (م)
     */
    @NotNull(message = "عمق الحفر مطلوب")
    @Positive(message = "عمق الحفر يجب أن يكون أكبر من صفر")
    private BigDecimal excavationDepth;

    /**
     * عرض القاعدة (م)
     */
    @Positive(message = "عرض القاعدة يجب أن يكون أكبر من صفر")
    @Builder.Default
    private BigDecimal footingWidth = new BigDecimal("0.8");

    /**
     * ارتفاع القاعدة (م)
     */
    @Positive(message = "ارتفاع القاعدة يجب أن يكون أكبر من صفر")
    @Builder.Default
    private BigDecimal footingHeight = new BigDecimal("0.3");

    /**
     * طول القواعد الكلي (م) - محيط البناء + القواعد الداخلية
     */
    @Positive(message = "طول القواعد يجب أن يكون أكبر من صفر")
    private BigDecimal totalFootingLength;

    /**
     * عمق الدفان (م)
     */
    @Positive(message = "عمق الدفان يجب أن يكون أكبر من صفر")
    private BigDecimal fillDepth;

    /**
     * سماكة السبيس (م)
     */
    @Positive(message = "سماكة السبيس يجب أن تكون أكبر من صفر")
    @Builder.Default
    private BigDecimal spaceThickness = new BigDecimal("0.10");

    /**
     * سعر الحفر لكل م³
     */
    @Positive(message = "سعر الحفر يجب أن يكون أكبر من صفر")
    private BigDecimal excavationPricePerM3;

    /**
     * سعر الدفان لكل م³
     */
    @Positive(message = "سعر الدفان يجب أن يكون أكبر من صفر")
    private BigDecimal fillPricePerM3;

    /**
     * سعر الخرسانة لكل م³
     */
    @Positive(message = "سعر الخرسانة يجب أن يكون أكبر من صفر")
    private BigDecimal concretePricePerM3;

    /**
     * سعر الحديد لكل طن
     */
    @Positive(message = "سعر الحديد يجب أن يكون أكبر من صفر")
    private BigDecimal steelPricePerTon;

    /**
     * كلفة العمالة لكل م³
     */
    @Positive(message = "كلفة العمالة يجب أن تكون أكبر من صفر")
    private BigDecimal laborCostPerM3;

    /**
     * نسبة الهدر
     */
    @Builder.Default
    private BigDecimal wastePercentage = new BigDecimal("0.05");

    /**
     * العملة
     */
    @Builder.Default
    private String currency = "IQD";

    /**
     * المنطقة
     */
    @Builder.Default
    private String region = "baghdad";
}
