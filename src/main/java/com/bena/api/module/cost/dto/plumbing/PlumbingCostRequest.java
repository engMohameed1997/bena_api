package com.bena.api.module.cost.dto.plumbing;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * طلب حساب كلفة السباكة (الماء والمجاري)
 * Plumbing (Water & Sewage) Cost Calculation Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlumbingCostRequest {

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
     * عدد الحمامات
     */
    private Integer bathroomCount;

    /**
     * عدد المطابخ
     */
    private Integer kitchenCount;

    /**
     * عدد نقاط الماء (حنفيات)
     */
    private Integer waterPoints;

    /**
     * عدد نقاط الصرف
     */
    private Integer drainPoints;

    /**
     * عدد المراحيض
     */
    private Integer toiletCount;

    /**
     * عدد المغاسل
     */
    private Integer sinkCount;

    /**
     * عدد الدوشات
     */
    private Integer showerCount;

    /**
     * هل يشمل سخان ماء؟
     */
    @Builder.Default
    private Boolean includeWaterHeater = false;

    /**
     * هل يشمل خزان ماء علوي؟
     */
    @Builder.Default
    private Boolean includeWaterTank = false;

    /**
     * هل يشمل مضخة ماء؟
     */
    @Builder.Default
    private Boolean includeWaterPump = false;

    // === الأسعار ===

    /**
     * سعر المتر الطولي من أنبوب الماء (PPR)
     */
    @Positive(message = "سعر أنبوب الماء يجب أن يكون أكبر من صفر")
    private BigDecimal waterPipePricePerMeter;

    /**
     * سعر المتر الطولي من أنبوب الصرف (PVC)
     */
    @Positive(message = "سعر أنبوب الصرف يجب أن يكون أكبر من صفر")
    private BigDecimal drainPipePricePerMeter;

    /**
     * سعر المرحاض
     */
    @Positive(message = "سعر المرحاض يجب أن يكون أكبر من صفر")
    private BigDecimal toiletPrice;

    /**
     * سعر المغسلة
     */
    @Positive(message = "سعر المغسلة يجب أن يكون أكبر من صفر")
    private BigDecimal sinkPrice;

    /**
     * سعر الخلاط (حنفية)
     */
    @Positive(message = "سعر الخلاط يجب أن يكون أكبر من صفر")
    private BigDecimal mixerPrice;

    /**
     * كلفة العمالة لكل نقطة
     */
    @Positive(message = "كلفة العمالة يجب أن تكون أكبر من صفر")
    private BigDecimal laborCostPerPoint;

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
