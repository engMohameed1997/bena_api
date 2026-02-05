package com.bena.api.module.cost.dto.cement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * نتيجة حساب كلفة السمنت
 * Cement Cost Calculation Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CementCostResponse {

    /**
     * المساحة أو الحجم المحسوب
     */
    private BigDecimal area;
    private BigDecimal volume;

    /**
     * حجم المونة المطلوب (م³)
     */
    private BigDecimal mortarVolumeM3;

    /**
     * كمية السمنت المطلوبة (طن)
     */
    private BigDecimal cementTon;

    /**
     * كمية السمنت (كيس 50 كغ)
     */
    private Integer cementBags;

    /**
     * كمية الرمل المطلوبة (م³)
     */
    private BigDecimal sandM3;

    /**
     * كلفة السمنت
     */
    private BigDecimal cementCost;

    /**
     * كلفة الرمل
     */
    private BigDecimal sandCost;

    /**
     * كلفة العمالة
     */
    private BigDecimal laborCost;

    /**
     * الكلفة الكلية
     */
    private BigDecimal totalCost;

    /**
     * الكلفة لكل م²
     */
    private BigDecimal costPerM2;

    /**
     * العملة
     */
    private String currency;

    /**
     * نوع الاستخدام
     */
    private String usageType;

    /**
     * نوع السمنت
     */
    private String cementType;

    /**
     * نسبة الخلط المستخدمة
     */
    private String mixRatioUsed;

    /**
     * تقدير عدد أيام العمل
     */
    private Integer estimatedWorkDays;

    /**
     * ملخص المدخلات
     */
    private CementCostRequest inputSummary;
}
