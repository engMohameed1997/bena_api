package com.bena.api.module.cost.dto.steel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * طلب حساب كلفة الحديد
 * Steel/Rebar Cost Calculation Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SteelCostRequest {

    /**
     * طريقة الحساب: by_volume (حسب حجم الخرسانة), by_weight (حسب الوزن), by_bars (حسب القضبان)
     */
    @NotNull(message = "طريقة الحساب مطلوبة")
    private String calculationMethod;

    // === حساب حسب حجم الخرسانة ===

    /**
     * حجم الخرسانة (م³)
     */
    @Positive(message = "حجم الخرسانة يجب أن يكون أكبر من صفر")
    private BigDecimal concreteVolumeM3;

    /**
     * نوع العنصر الإنشائي: foundation (قاعدة), slab (سقف), column (عمود), beam (جسر), wall (جدار)
     */
    private String structuralElement;

    /**
     * نسبة الحديد (كغ/م³) - اختياري، يُحسب تلقائياً حسب النوع
     */
    private BigDecimal steelRatioKgPerM3;

    // === حساب حسب الوزن المباشر ===

    /**
     * الوزن المطلوب (طن)
     */
    @Positive(message = "الوزن يجب أن يكون أكبر من صفر")
    private BigDecimal weightTon;

    // === حساب حسب القضبان ===

    /**
     * قائمة القضبان المطلوبة
     */
    private List<SteelBar> bars;

    // === الأسعار ===

    /**
     * سعر طن الحديد
     */
    @Positive(message = "سعر الحديد يجب أن يكون أكبر من صفر")
    private BigDecimal steelPricePerTon;

    /**
     * كلفة التقطيع والتجنيط لكل طن
     */
    @Positive(message = "كلفة التقطيع يجب أن تكون أكبر من صفر")
    private BigDecimal cuttingCostPerTon;

    /**
     * كلفة التركيب لكل طن
     */
    @Positive(message = "كلفة التركيب يجب أن تكون أكبر من صفر")
    private BigDecimal installationCostPerTon;

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

    /**
     * تفاصيل قضيب حديد واحد
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SteelBar {
        /**
         * القطر (ملم): 8, 10, 12, 14, 16, 18, 20, 22, 25, 28, 32
         */
        private Integer diameterMm;

        /**
         * الطول (م)
         */
        private BigDecimal lengthM;

        /**
         * العدد
         */
        private Integer quantity;
    }
}
