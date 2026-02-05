package com.bena.api.module.cost.dto.brick;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * نتيجة حساب كلفة الطابوق
 * Brick Cost Calculation Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrickCostResponse {

    /**
     * المساحة الصافية بعد خصم الفتحات (م²)
     */
    private BigDecimal wallAreaNet;

    /**
     * عدد الطابوق المطلوب (شامل الهدر)
     */
    private Long brickCount;

    /**
     * عدد الطابوق بدون هدر
     */
    private Long brickCountRaw;

    /**
     * كلفة الطابوق
     */
    private BigDecimal brickCost;

    /**
     * كلفة المونة (سمنت + رمل)
     */
    private BigDecimal mortarCost;

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
     * نوع الطابوق المستخدم
     */
    private String brickType;

    /**
     * عدد الطابوق لكل م² المستخدم في الحساب
     */
    private Integer bricksPerM2Used;

    /**
     * نسبة الهدر المستخدمة
     */
    private BigDecimal wastePercentageUsed;

    /**
     * تقدير عدد أيام العمل (اختياري)
     */
    private Integer estimatedWorkDays;

    /**
     * تقدير عدد العمال المطلوبين (اختياري)
     */
    private Integer estimatedWorkers;

    /**
     * ملخص المدخلات
     */
    private BrickCostRequest inputSummary;
}
