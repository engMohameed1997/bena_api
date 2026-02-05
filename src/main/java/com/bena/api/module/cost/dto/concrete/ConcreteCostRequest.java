package com.bena.api.module.cost.dto.concrete;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * طلب حساب كلفة الصبة (الخرسانة)
 * Concrete/Slab Cost Calculation Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcreteCostRequest {

    /**
     * نوع الصبة: foundation (أساس), slab (سقف), column (عمود), beam (جسر)
     */
    @NotNull(message = "نوع الصبة مطلوب")
    private String concreteType;

    /**
     * الطول (م)
     */
    @NotNull(message = "الطول مطلوب")
    @Positive(message = "الطول يجب أن يكون أكبر من صفر")
    private BigDecimal length;

    /**
     * العرض (م)
     */
    @NotNull(message = "العرض مطلوب")
    @Positive(message = "العرض يجب أن يكون أكبر من صفر")
    private BigDecimal width;

    /**
     * السماكة/الارتفاع (م)
     */
    @NotNull(message = "السماكة مطلوبة")
    @Positive(message = "السماكة يجب أن تكون أكبر من صفر")
    private BigDecimal thickness;

    /**
     * نوع السقف: hollow (هولو), styrofoam (فلين/ستايروفوم), solid (صلب)
     * يُستخدم فقط إذا كان concreteType = slab
     */
    private String slabType;

    /**
     * سعر المتر المكعب من الخرسانة الجاهزة
     */
    @Positive(message = "سعر الخرسانة يجب أن يكون أكبر من صفر")
    private BigDecimal concretePricePerM3;

    /**
     * سعر طن الحديد
     */
    @Positive(message = "سعر الحديد يجب أن يكون أكبر من صفر")
    private BigDecimal steelPricePerTon;

    /**
     * نسبة الحديد (كغ/م³) - اختياري، يُحسب تلقائياً حسب النوع
     */
    private BigDecimal steelRatioKgPerM3;

    /**
     * كلفة العمالة لكل م³
     */
    @Positive(message = "كلفة العمالة يجب أن تكون أكبر من صفر")
    private BigDecimal laborCostPerM3;

    /**
     * كلفة القالب (الطوبار) لكل م²
     */
    @Positive(message = "كلفة القالب يجب أن تكون أكبر من صفر")
    private BigDecimal formworkCostPerM2;

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
