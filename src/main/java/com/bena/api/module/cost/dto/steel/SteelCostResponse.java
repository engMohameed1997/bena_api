package com.bena.api.module.cost.dto.steel;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * نتيجة حساب كلفة الحديد
 * Steel/Rebar Cost Calculation Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SteelCostResponse {

    /**
     * الوزن الصافي (طن)
     */
    private BigDecimal weightTonNet;

    /**
     * الوزن شامل الهدر (طن)
     */
    private BigDecimal weightTonWithWaste;

    /**
     * الوزن (كغ)
     */
    private BigDecimal weightKg;

    /**
     * عدد القضبان (12 متر) المطلوبة تقريباً
     */
    private Integer estimatedBars12m;

    /**
     * كلفة الحديد
     */
    private BigDecimal steelCost;

    /**
     * كلفة التقطيع والتجنيط
     */
    private BigDecimal cuttingCost;

    /**
     * كلفة التركيب
     */
    private BigDecimal installationCost;

    /**
     * الكلفة الكلية
     */
    private BigDecimal totalCost;

    /**
     * الكلفة لكل م³ (إذا كان الحساب بالحجم)
     */
    private BigDecimal costPerM3;

    /**
     * العملة
     */
    private String currency;

    /**
     * طريقة الحساب المستخدمة
     */
    private String calculationMethod;

    /**
     * نوع العنصر الإنشائي (إن وجد)
     */
    private String structuralElement;

    /**
     * نسبة الحديد المستخدمة (كغ/م³)
     */
    private BigDecimal steelRatioUsed;

    /**
     * تفاصيل القضبان (إذا كان الحساب بالقضبان)
     */
    private List<BarDetail> barDetails;

    /**
     * تقدير عدد أيام العمل
     */
    private Integer estimatedWorkDays;

    /**
     * ملخص المدخلات
     */
    private SteelCostRequest inputSummary;

    /**
     * تفاصيل قضيب واحد
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BarDetail {
        private Integer diameterMm;
        private BigDecimal lengthM;
        private Integer quantity;
        private BigDecimal weightPerMeter;
        private BigDecimal totalWeight;
    }
}
