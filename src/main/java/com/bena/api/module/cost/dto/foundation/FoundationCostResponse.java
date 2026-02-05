package com.bena.api.module.cost.dto.foundation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * نتيجة حساب كلفة الأساس والدفان والسبيس
 * Foundation, Fill, and Space Cost Calculation Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FoundationCostResponse {

    // === كميات الحفر ===
    /**
     * حجم الحفر (م³)
     */
    private BigDecimal excavationVolumeM3;

    /**
     * كلفة الحفر
     */
    private BigDecimal excavationCost;

    // === كميات القواعد ===
    /**
     * حجم خرسانة القواعد (م³)
     */
    private BigDecimal footingConcreteVolumeM3;

    /**
     * كمية حديد القواعد (طن)
     */
    private BigDecimal footingSteelTon;

    /**
     * كلفة خرسانة القواعد
     */
    private BigDecimal footingConcreteCost;

    /**
     * كلفة حديد القواعد
     */
    private BigDecimal footingSteelCost;

    // === كميات الدفان ===
    /**
     * حجم الدفان (م³)
     */
    private BigDecimal fillVolumeM3;

    /**
     * كلفة الدفان
     */
    private BigDecimal fillCost;

    // === كميات السبيس ===
    /**
     * حجم السبيس (م³)
     */
    private BigDecimal spaceVolumeM3;

    /**
     * كلفة السبيس
     */
    private BigDecimal spaceCost;

    // === العمالة ===
    /**
     * كلفة العمالة الكلية
     */
    private BigDecimal laborCost;

    // === الإجماليات ===
    /**
     * الكلفة الكلية
     */
    private BigDecimal totalCost;

    /**
     * الكلفة لكل م² من مساحة الأرض
     */
    private BigDecimal costPerM2;

    /**
     * العملة
     */
    private String currency;

    /**
     * تقدير عدد أيام العمل
     */
    private Integer estimatedWorkDays;

    /**
     * ملخص المدخلات
     */
    private FoundationCostRequest inputSummary;
}
