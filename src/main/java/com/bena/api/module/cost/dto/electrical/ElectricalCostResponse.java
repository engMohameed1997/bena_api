package com.bena.api.module.cost.dto.electrical;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * نتيجة حساب كلفة الكهرباء
 * Electrical Cost Calculation Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ElectricalCostResponse {

    // === الكميات ===

    /**
     * إجمالي النقاط الكهربائية
     */
    private Integer totalPoints;

    /**
     * طول الأسلاك المطلوبة (م)
     */
    private BigDecimal wireLength;

    /**
     * عدد المفاتيح
     */
    private Integer switchCount;

    /**
     * عدد البرايز
     */
    private Integer socketCount;

    /**
     * عدد القواطع (بريكرات)
     */
    private Integer breakerCount;

    /**
     * عدد لوحات التوزيع
     */
    private Integer distributionBoardCount;

    // === الكلف ===

    /**
     * كلفة الأسلاك
     */
    private BigDecimal wireCost;

    /**
     * كلفة المفاتيح
     */
    private BigDecimal switchCost;

    /**
     * كلفة البرايز
     */
    private BigDecimal socketCost;

    /**
     * كلفة القواطع
     */
    private BigDecimal breakerCost;

    /**
     * كلفة لوحات التوزيع
     */
    private BigDecimal distributionBoardCost;

    /**
     * كلفة الأنابيب والعلب
     */
    private BigDecimal conduitAndBoxesCost;

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
    private ElectricalCostRequest inputSummary;
}
