package com.bena.api.module.cost.dto.tiles;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * طلب حساب كلفة الكاشي والسيراميك
 * Tiles (Ceramic/Porcelain) Cost Calculation Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TilesCostRequest {

    /**
     * نوع البلاط: ceramic (سيراميك), porcelain (بورسلان), marble (رخام), granite (كرانيت)
     */
    @NotNull(message = "نوع البلاط مطلوب")
    private String tileType;

    /**
     * مكان التركيب: floor (أرضية), wall (جدار), bathroom (حمام), kitchen (مطبخ)
     */
    @NotNull(message = "مكان التركيب مطلوب")
    private String location;

    /**
     * المساحة (م²)
     */
    @NotNull(message = "المساحة مطلوبة")
    @Positive(message = "المساحة يجب أن تكون أكبر من صفر")
    private BigDecimal area;

    /**
     * سعر المتر المربع من البلاط
     */
    @Positive(message = "سعر البلاط يجب أن يكون أكبر من صفر")
    private BigDecimal tilePricePerM2;

    /**
     * سعر كيس السمنت الأبيض (للفواصل)
     */
    @Positive(message = "سعر السمنت الأبيض يجب أن يكون أكبر من صفر")
    private BigDecimal whiteCementPricePerBag;

    /**
     * سعر كيس الغراء
     */
    @Positive(message = "سعر الغراء يجب أن يكون أكبر من صفر")
    private BigDecimal adhesivePricePerBag;

    /**
     * كلفة العمالة لكل م²
     */
    @Positive(message = "كلفة العمالة يجب أن تكون أكبر من صفر")
    private BigDecimal laborCostPerM2;

    /**
     * نسبة الهدر (للقص والكسر)
     */
    @Builder.Default
    private BigDecimal wastePercentage = new BigDecimal("0.10");

    /**
     * هل يشمل وزرة (قاعدة الجدار)؟
     */
    @Builder.Default
    private Boolean includeBaseboard = false;

    /**
     * طول الوزرة (م) - إذا كان includeBaseboard = true
     */
    private BigDecimal baseboardLength;

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
