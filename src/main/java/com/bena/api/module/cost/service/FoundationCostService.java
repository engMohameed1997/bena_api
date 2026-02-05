package com.bena.api.module.cost.service;

import com.bena.api.module.cost.dto.foundation.FoundationCostRequest;
import com.bena.api.module.cost.dto.foundation.FoundationCostResponse;
import com.bena.api.module.cost.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * خدمة حساب كلفة الأساس والدفان والسبيس
 * Foundation, Fill, and Space Cost Calculation Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FoundationCostService {

    private final MaterialRepository materialRepository;

    // القيم الافتراضية
    private static final BigDecimal DEFAULT_EXCAVATION_PRICE_PER_M3 = new BigDecimal("15000");
    private static final BigDecimal DEFAULT_FILL_PRICE_PER_M3 = new BigDecimal("45000"); // دفان + رص
    private static final BigDecimal DEFAULT_CONCRETE_PRICE_PER_M3 = new BigDecimal("180000");
    private static final BigDecimal DEFAULT_STEEL_PRICE_PER_TON = new BigDecimal("1200000");
    private static final BigDecimal DEFAULT_LABOR_COST_PER_M3 = new BigDecimal("60000");
    
    // نسبة الحديد للقواعد (كغ/م³)
    private static final BigDecimal STEEL_RATIO_FOOTING = new BigDecimal("100");

    public FoundationCostResponse calculate(FoundationCostRequest request) {
        log.debug("Calculating foundation cost for land area: {} m²", request.getLandArea());

        BigDecimal wastePercentage = request.getWastePercentage() != null ?
                request.getWastePercentage() : new BigDecimal("0.05");

        // === 1. حساب الحفر ===
        BigDecimal excavationVolume = request.getLandArea()
                .multiply(request.getExcavationDepth())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal excavationPrice = request.getExcavationPricePerM3() != null ?
                request.getExcavationPricePerM3() : DEFAULT_EXCAVATION_PRICE_PER_M3;
        BigDecimal excavationCost = excavationVolume.multiply(excavationPrice)
                .setScale(0, RoundingMode.HALF_UP);

        // === 2. حساب القواعد ===
        BigDecimal footingLength = request.getTotalFootingLength();
        BigDecimal footingWidth = request.getFootingWidth();
        BigDecimal footingHeight = request.getFootingHeight();

        BigDecimal footingConcreteVolume = BigDecimal.ZERO;
        BigDecimal footingSteelTon = BigDecimal.ZERO;
        BigDecimal footingConcreteCost = BigDecimal.ZERO;
        BigDecimal footingSteelCost = BigDecimal.ZERO;

        if (footingLength != null && footingLength.compareTo(BigDecimal.ZERO) > 0) {
            footingConcreteVolume = footingLength
                    .multiply(footingWidth)
                    .multiply(footingHeight)
                    .multiply(BigDecimal.ONE.add(wastePercentage))
                    .setScale(2, RoundingMode.HALF_UP);

            // حساب الحديد
            BigDecimal steelWeightKg = footingConcreteVolume.multiply(STEEL_RATIO_FOOTING);
            footingSteelTon = steelWeightKg.divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);

            // الكلف
            BigDecimal concretePrice = request.getConcretePricePerM3() != null ?
                    request.getConcretePricePerM3() : DEFAULT_CONCRETE_PRICE_PER_M3;
            BigDecimal steelPrice = request.getSteelPricePerTon() != null ?
                    request.getSteelPricePerTon() : DEFAULT_STEEL_PRICE_PER_TON;

            footingConcreteCost = footingConcreteVolume.multiply(concretePrice)
                    .setScale(0, RoundingMode.HALF_UP);
            footingSteelCost = footingSteelTon.multiply(steelPrice)
                    .setScale(0, RoundingMode.HALF_UP);
        }

        // === 3. حساب الدفان ===
        BigDecimal fillVolume = BigDecimal.ZERO;
        BigDecimal fillCost = BigDecimal.ZERO;

        if (request.getFillDepth() != null && request.getFillDepth().compareTo(BigDecimal.ZERO) > 0) {
            fillVolume = request.getLandArea()
                    .multiply(request.getFillDepth())
                    .multiply(BigDecimal.ONE.add(wastePercentage))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal fillPrice = request.getFillPricePerM3() != null ?
                    request.getFillPricePerM3() : DEFAULT_FILL_PRICE_PER_M3;
            fillCost = fillVolume.multiply(fillPrice)
                    .setScale(0, RoundingMode.HALF_UP);
        }

        // === 4. حساب السبيس ===
        BigDecimal spaceVolume = request.getLandArea()
                .multiply(request.getSpaceThickness())
                .multiply(BigDecimal.ONE.add(wastePercentage))
                .setScale(2, RoundingMode.HALF_UP);

        // السبيس = خرسانة عادية (بدون حديد) بسعر أقل
        BigDecimal spaceConcretePrice = DEFAULT_CONCRETE_PRICE_PER_M3
                .multiply(new BigDecimal("0.7")); // 70% من سعر الخرسانة المسلحة
        BigDecimal spaceCost = spaceVolume.multiply(spaceConcretePrice)
                .setScale(0, RoundingMode.HALF_UP);

        // === 5. حساب العمالة ===
        BigDecimal totalVolume = excavationVolume
                .add(footingConcreteVolume)
                .add(fillVolume)
                .add(spaceVolume);

        BigDecimal laborPrice = request.getLaborCostPerM3() != null ?
                request.getLaborCostPerM3() : DEFAULT_LABOR_COST_PER_M3;
        BigDecimal laborCost = totalVolume.multiply(laborPrice)
                .setScale(0, RoundingMode.HALF_UP);

        // === 6. الكلفة الكلية ===
        BigDecimal totalCost = excavationCost
                .add(footingConcreteCost)
                .add(footingSteelCost)
                .add(fillCost)
                .add(spaceCost)
                .add(laborCost);

        BigDecimal costPerM2 = request.getLandArea().compareTo(BigDecimal.ZERO) > 0 ?
                totalCost.divide(request.getLandArea(), 0, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        // تقدير أيام العمل (تقريبي: 15 م³ يوميًا)
        int estimatedWorkDays = totalVolume.divide(BigDecimal.valueOf(15), 0, RoundingMode.CEILING).intValue();

        log.debug("Foundation cost calculation completed. Total: {} {}", totalCost, request.getCurrency());

        return FoundationCostResponse.builder()
                .excavationVolumeM3(excavationVolume)
                .excavationCost(excavationCost)
                .footingConcreteVolumeM3(footingConcreteVolume)
                .footingSteelTon(footingSteelTon)
                .footingConcreteCost(footingConcreteCost)
                .footingSteelCost(footingSteelCost)
                .fillVolumeM3(fillVolume)
                .fillCost(fillCost)
                .spaceVolumeM3(spaceVolume)
                .spaceCost(spaceCost)
                .laborCost(laborCost)
                .totalCost(totalCost)
                .costPerM2(costPerM2)
                .currency(request.getCurrency())
                .estimatedWorkDays(estimatedWorkDays)
                .inputSummary(request)
                .build();
    }
}
