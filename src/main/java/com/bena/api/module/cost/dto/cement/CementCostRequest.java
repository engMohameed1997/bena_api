package com.bena.api.module.cost.dto.cement;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * طلب حساب كلفة السمنت
 * Cement Cost Calculation Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CementCostRequest {

    /**
     * نوع الاستخدام: plastering (لياسة), flooring (أرضية), bricklaying (بناء طابوق), concrete (خرسانة)
     */
    @NotNull(message = "نوع الاستخدام مطلوب")
    private String usageType;

    /**
     * المساحة (م²) - للياسة والأرضية
     */
    @Positive(message = "المساحة يجب أن تكون أكبر من صفر")
    private BigDecimal area;

    /**
     * الحجم (م³) - للخرسانة
     */
    @Positive(message = "الحجم يجب أن يكون أكبر من صفر")
    private BigDecimal volume;

    /**
     * سماكة اللياسة أو الأرضية (سم)
     */
    @Positive(message = "السماكة يجب أن تكون أكبر من صفر")
    private BigDecimal thicknessCm;

    /**
     * نسبة الخلط (1:3, 1:4, 1:5, 1:6)
     * الرقم يمثل نسبة الرمل للسمنت
     */
    @Builder.Default
    private Integer mixRatio = 4;

    /**
     * نوع السمنت: portland (بورتلاندي), resistant (مقاوم)
     */
    @Builder.Default
    private String cementType = "portland";

    /**
     * سعر طن السمنت
     */
    @Positive(message = "سعر السمنت يجب أن يكون أكبر من صفر")
    private BigDecimal cementPricePerTon;

    /**
     * سعر م³ الرمل
     */
    @Positive(message = "سعر الرمل يجب أن يكون أكبر من صفر")
    private BigDecimal sandPricePerM3;

    /**
     * كلفة العمالة لكل م²
     */
    @Positive(message = "كلفة العمالة يجب أن تكون أكبر من صفر")
    private BigDecimal laborCostPerM2;

    /**
     * نسبة الهدر
     */
    @Builder.Default
    private BigDecimal wastePercentage = new BigDecimal("0.10");

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
