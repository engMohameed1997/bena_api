package com.bena.api.module.cost.dto.plumbing;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * نتيجة حساب كلفة السباكة (الماء والمجاري)
 * Plumbing Cost Calculation Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlumbingCostResponse {

    // === الكميات ===

    /**
     * إجمالي نقاط الماء
     */
    private Integer totalWaterPoints;

    /**
     * إجمالي نقاط الصرف
     */
    private Integer totalDrainPoints;

    /**
     * طول أنابيب الماء (م)
     */
    private BigDecimal waterPipeLength;

    /**
     * طول أنابيب الصرف (م)
     */
    private BigDecimal drainPipeLength;

    /**
     * عدد المراحيض
     */
    private Integer toiletCount;

    /**
     * عدد المغاسل
     */
    private Integer sinkCount;

    /**
     * عدد الخلاطات
     */
    private Integer mixerCount;

    // === الكلف ===

    /**
     * كلفة أنابيب الماء
     */
    private BigDecimal waterPipeCost;

    /**
     * كلفة أنابيب الصرف
     */
    private BigDecimal drainPipeCost;

    /**
     * كلفة المراحيض
     */
    private BigDecimal toiletCost;

    /**
     * كلفة المغاسل
     */
    private BigDecimal sinkCost;

    /**
     * كلفة الخلاطات
     */
    private BigDecimal mixerCost;

    /**
     * كلفة الوصلات والإكسسوارات
     */
    private BigDecimal fittingsCost;

    /**
     * كلفة السخان (إن وجد)
     */
    private BigDecimal waterHeaterCost;

    /**
     * كلفة الخزان (إن وجد)
     */
    private BigDecimal waterTankCost;

    /**
     * كلفة المضخة (إن وجدت)
     */
    private BigDecimal waterPumpCost;

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
     * الكلفة لكل نقطة
     */
    private BigDecimal costPerPoint;

    /**
     * العملة
     */
    private String currency;

    /**
     * نوع الحساب المستخدم
     */
    private String calculationType;

    /**
     * تقدير عدد أيام العمل
     */
    private Integer estimatedWorkDays;

    /**
     * ملخص المدخلات
     */
    private PlumbingCostRequest inputSummary;
}
