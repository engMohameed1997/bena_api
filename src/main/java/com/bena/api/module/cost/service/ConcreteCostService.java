package com.bena.api.module.cost.service;

import com.bena.api.module.cost.dto.concrete.ConcreteCostRequest;
import com.bena.api.module.cost.dto.concrete.ConcreteCostResponse;
import com.bena.api.module.cost.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * خدمة حساب كلفة الصبة (الخرسانة)
 * Concrete/Slab Cost Calculation Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConcreteCostService {

    private final MaterialRepository materialRepository;

    // القيم الافتراضية
    private static final BigDecimal DEFAULT_CONCRETE_PRICE_PER_M3 = new BigDecimal("180000");
    private static final BigDecimal DEFAULT_STEEL_PRICE_PER_TON = new BigDecimal("1200000");
    private static final BigDecimal DEFAULT_LABOR_COST_PER_M3 = new BigDecimal("80000");
    private static final BigDecimal DEFAULT_FORMWORK_COST_PER_M2 = new BigDecimal("25000");
    
    // نسب الحديد حسب نوع الصبة (كغ/م³)
    private static final BigDecimal STEEL_RATIO_FOUNDATION = new BigDecimal("100");
    private static final BigDecimal STEEL_RATIO_SLAB = new BigDecimal("80");
    private static final BigDecimal STEEL_RATIO_COLUMN = new BigDecimal("150");
    private static final BigDecimal STEEL_RATIO_BEAM = new BigDecimal("120");

    // أسعار مواد السقف
    private static final BigDecimal HOLLOW_BLOCK_PRICE = new BigDecimal("2500");
    private static final int HOLLOW_BLOCKS_PER_M2 = 8;
    private static final BigDecimal STYROFOAM_PRICE_PER_M2 = new BigDecimal("15000");

    public ConcreteCostResponse calculate(ConcreteCostRequest request) {
        log.debug("Calculating concrete cost for type: {}", request.getConcreteType());

        // 1. حساب الحجم والمساحة
        BigDecimal length = request.getLength();
        BigDecimal width = request.getWidth();
        BigDecimal thickness = request.getThickness();

        BigDecimal areaM2 = length.multiply(width);
        BigDecimal volumeM3 = areaM2.multiply(thickness);

        // إضافة الهدر
        BigDecimal wastePercentage = request.getWastePercentage() != null ?
                request.getWastePercentage() : new BigDecimal("0.05");
        BigDecimal concreteVolumeRequired = volumeM3.multiply(BigDecimal.ONE.add(wastePercentage))
                .setScale(2, RoundingMode.HALF_UP);

        // 2. تحديد نسبة الحديد حسب النوع
        BigDecimal steelRatio = request.getSteelRatioKgPerM3();
        if (steelRatio == null) {
            steelRatio = switch (request.getConcreteType().toLowerCase()) {
                case "foundation" -> STEEL_RATIO_FOUNDATION;
                case "slab" -> STEEL_RATIO_SLAB;
                case "column" -> STEEL_RATIO_COLUMN;
                case "beam" -> STEEL_RATIO_BEAM;
                default -> STEEL_RATIO_SLAB;
            };
        }

        // 3. حساب كمية الحديد
        BigDecimal steelWeightKg = concreteVolumeRequired.multiply(steelRatio)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal steelWeightTon = steelWeightKg.divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP);

        // 4. جلب الأسعار
        BigDecimal concretePrice = request.getConcretePricePerM3() != null ?
                request.getConcretePricePerM3() : DEFAULT_CONCRETE_PRICE_PER_M3;
        BigDecimal steelPrice = request.getSteelPricePerTon() != null ?
                request.getSteelPricePerTon() : DEFAULT_STEEL_PRICE_PER_TON;
        BigDecimal laborCost = request.getLaborCostPerM3() != null ?
                request.getLaborCostPerM3() : DEFAULT_LABOR_COST_PER_M3;
        BigDecimal formworkCost = request.getFormworkCostPerM2() != null ?
                request.getFormworkCostPerM2() : DEFAULT_FORMWORK_COST_PER_M2;

        // 5. حساب الكلف
        BigDecimal concreteCost = concreteVolumeRequired.multiply(concretePrice)
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal steelCost = steelWeightTon.multiply(steelPrice)
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal formworkCostTotal = areaM2.multiply(formworkCost)
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal laborCostTotal = concreteVolumeRequired.multiply(laborCost)
                .setScale(0, RoundingMode.HALF_UP);

        // 6. حساب مواد السقف (إن كان سقف)
        Integer hollowBlockCount = null;
        BigDecimal styrofoamArea = null;
        BigDecimal slabMaterialCost = BigDecimal.ZERO;

        if ("slab".equalsIgnoreCase(request.getConcreteType()) && request.getSlabType() != null) {
            switch (request.getSlabType().toLowerCase()) {
                case "hollow" -> {
                    hollowBlockCount = areaM2.multiply(BigDecimal.valueOf(HOLLOW_BLOCKS_PER_M2))
                            .setScale(0, RoundingMode.CEILING).intValue();
                    slabMaterialCost = BigDecimal.valueOf(hollowBlockCount).multiply(HOLLOW_BLOCK_PRICE);
                }
                case "styrofoam" -> {
                    styrofoamArea = areaM2;
                    slabMaterialCost = areaM2.multiply(STYROFOAM_PRICE_PER_M2);
                }
            }
        }

        // 7. الكلفة الكلية
        BigDecimal totalCost = concreteCost
                .add(steelCost)
                .add(formworkCostTotal)
                .add(laborCostTotal)
                .add(slabMaterialCost);

        BigDecimal costPerM2 = areaM2.compareTo(BigDecimal.ZERO) > 0 ?
                totalCost.divide(areaM2, 0, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal costPerM3 = concreteVolumeRequired.compareTo(BigDecimal.ZERO) > 0 ?
                totalCost.divide(concreteVolumeRequired, 0, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        // 8. تقدير أيام العمل (تقريبي: 10 م³ يوميًا)
        int estimatedWorkDays = concreteVolumeRequired.divide(BigDecimal.TEN, 0, RoundingMode.CEILING).intValue();

        log.debug("Concrete cost calculation completed. Total: {} {}", totalCost, request.getCurrency());

        return ConcreteCostResponse.builder()
                .volumeM3(volumeM3.setScale(2, RoundingMode.HALF_UP))
                .areaM2(areaM2.setScale(2, RoundingMode.HALF_UP))
                .concreteVolumeRequired(concreteVolumeRequired)
                .steelWeightKg(steelWeightKg)
                .steelWeightTon(steelWeightTon.setScale(3, RoundingMode.HALF_UP))
                .hollowBlockCount(hollowBlockCount)
                .styrofoamAreaM2(styrofoamArea)
                .concreteCost(concreteCost)
                .steelCost(steelCost)
                .slabMaterialCost(slabMaterialCost.compareTo(BigDecimal.ZERO) > 0 ? slabMaterialCost : null)
                .formworkCost(formworkCostTotal)
                .laborCost(laborCostTotal)
                .totalCost(totalCost)
                .costPerM2(costPerM2)
                .costPerM3(costPerM3)
                .currency(request.getCurrency())
                .concreteType(request.getConcreteType())
                .slabType(request.getSlabType())
                .estimatedWorkDays(estimatedWorkDays)
                .inputSummary(request)
                .build();
    }
}
