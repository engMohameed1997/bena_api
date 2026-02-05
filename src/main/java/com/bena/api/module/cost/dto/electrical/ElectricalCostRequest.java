package com.bena.api.module.cost.dto.electrical;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * طلب حساب كلفة الكهرباء
 * Electrical Cost Calculation Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElectricalCostRequest {

    /**
     * نوع الحساب: by_area (حسب المساحة), by_points (حسب النقاط), detailed (تفصيلي)
     */
    @NotNull(message = "نوع الحساب مطلوب")
    private String calculationType;

    // === حساب حسب المساحة ===

    /**
     * مساحة البناء (م²)
     */
    @Positive(message = "المساحة يجب أن تكون أكبر من صفر")
    private BigDecimal buildingArea;

    /**
     * عدد الطوابق
     */
    @Positive(message = "عدد الطوابق يجب أن يكون أكبر من صفر")
    private Integer floors;

    // === حساب حسب النقاط ===

    /**
     * عدد نقاط الإنارة (لمبات)
     */
    private Integer lightPoints;

    /**
     * عدد نقاط المفاتيح
     */
    private Integer switchPoints;

    /**
     * عدد نقاط البرايز (الأباريز)
     */
    private Integer socketPoints;

    /**
     * عدد نقاط المكيفات
     */
    private Integer acPoints;

    /**
     * عدد نقاط السخانات
     */
    private Integer heaterPoints;

    // === الأسعار ===

    /**
     * سعر المتر الطولي من السلك (2.5 ملم)
     */
    @Positive(message = "سعر السلك يجب أن يكون أكبر من صفر")
    private BigDecimal wirePricePerMeter;

    /**
     * سعر المفتاح الواحد
     */
    @Positive(message = "سعر المفتاح يجب أن يكون أكبر من صفر")
    private BigDecimal switchPrice;

    /**
     * سعر البريزة الواحدة
     */
    @Positive(message = "سعر البريزة يجب أن يكون أكبر من صفر")
    private BigDecimal socketPrice;

    /**
     * سعر القاطع (بريكر)
     */
    @Positive(message = "سعر القاطع يجب أن يكون أكبر من صفر")
    private BigDecimal breakerPrice;

    /**
     * سعر لوحة التوزيع
     */
    @Positive(message = "سعر لوحة التوزيع يجب أن يكون أكبر من صفر")
    private BigDecimal distributionBoardPrice;

    /**
     * كلفة العمالة لكل نقطة
     */
    @Positive(message = "كلفة العمالة يجب أن تكون أكبر من صفر")
    private BigDecimal laborCostPerPoint;

    /**
     * نسبة الهدر
     */
    @Builder.Default
    private BigDecimal wastePercentage = new BigDecimal("0.15");

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
