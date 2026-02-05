package com.bena.api.module.cost.dto.concrete;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * نتيجة حساب كلفة الصبة (الخرسانة)
 * Concrete/Slab Cost Calculation Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConcreteCostResponse {

    /**
     * الحجم الكلي (م³)
     */
    private BigDecimal volumeM3;

    /**
     * المساحة (م²)
     */
    private BigDecimal areaM2;

    /**
     * كمية الخرسانة المطلوبة (م³) شامل الهدر
     */
    private BigDecimal concreteVolumeRequired;

    /**
     * كمية الحديد المطلوبة (طن)
     */
    private BigDecimal steelWeightTon;

    /**
     * كمية الحديد (كغ)
     */
    private BigDecimal steelWeightKg;

    /**
     * عدد البلوك (هولو) أو مساحة الفلين - للسقف فقط
     */
    private Integer hollowBlockCount;
    private BigDecimal styrofoamAreaM2;

    /**
     * كلفة الخرسانة
     */
    private BigDecimal concreteCost;

    /**
     * كلفة الحديد
     */
    private BigDecimal steelCost;

    /**
     * كلفة البلوك/الفلين (للسقف)
     */
    private BigDecimal slabMaterialCost;

    /**
     * كلفة القالب (الطوبار)
     */
    private BigDecimal formworkCost;

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
     * الكلفة لكل م³
     */
    private BigDecimal costPerM3;

    /**
     * العملة
     */
    private String currency;

    /**
     * نوع الصبة
     */
    private String concreteType;

    /**
     * نوع السقف (إن وجد)
     */
    private String slabType;

    /**
     * تقدير عدد أيام العمل
     */
    private Integer estimatedWorkDays;

    /**
     * ملخص المدخلات
     */
    private ConcreteCostRequest inputSummary;
}
