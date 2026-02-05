package com.bena.api.module.cost.dto.tiles;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * نتيجة حساب كلفة الكاشي والسيراميك
 * Tiles Cost Calculation Response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TilesCostResponse {

    /**
     * المساحة المطلوبة (م²) شامل الهدر
     */
    private BigDecimal areaWithWaste;

    /**
     * عدد صناديق البلاط (تقريبي - حسب حجم الصندوق)
     */
    private Integer tileBoxes;

    /**
     * كمية الغراء (كيس)
     */
    private Integer adhesiveBags;

    /**
     * كمية السمنت الأبيض (كيس)
     */
    private Integer whiteCementBags;

    /**
     * كلفة البلاط
     */
    private BigDecimal tilesCost;

    /**
     * كلفة الغراء
     */
    private BigDecimal adhesiveCost;

    /**
     * كلفة السمنت الأبيض
     */
    private BigDecimal whiteCementCost;

    /**
     * كلفة الوزرة (إن وجدت)
     */
    private BigDecimal baseboardCost;

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
     * نوع البلاط
     */
    private String tileType;

    /**
     * مكان التركيب
     */
    private String location;

    /**
     * تقدير عدد أيام العمل
     */
    private Integer estimatedWorkDays;

    /**
     * ملخص المدخلات
     */
    private TilesCostRequest inputSummary;
}
