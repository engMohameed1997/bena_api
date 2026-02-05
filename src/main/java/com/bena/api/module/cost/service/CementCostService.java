package com.bena.api.module.cost.service;

import com.bena.api.module.cost.dto.cement.CementCostRequest;
import com.bena.api.module.cost.dto.cement.CementCostResponse;
import com.bena.api.module.cost.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * خدمة حساب كلفة السمنت
 * Cement Cost Calculation Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CementCostService {

    private final MaterialRepository materialRepository;

    // القيم الافتراضية
    private static final BigDecimal DEFAULT_CEMENT_PRICE_PER_TON = new BigDecimal("180000");
    private static final BigDecimal DEFAULT_SAND_PRICE_PER_M3 = new BigDecimal("25000");
    private static final BigDecimal DEFAULT_LABOR_COST_PER_M2 = new BigDecimal("8000");
    
    // كثافة السمنت (طن/م³)
    private static final BigDecimal CEMENT_DENSITY = new BigDecimal("1.44");
    
    // وزن كيس السمنت (كغ)
    private static final int CEMENT_BAG_WEIGHT_KG = 50;

    public CementCostResponse calculate(CementCostRequest request) {
        log.debug("Calculating cement cost for usage type: {}", request.getUsageType());

        BigDecimal area = request.getArea();
        BigDecimal volume = request.getVolume();
        BigDecimal mortarVolume;

        // 1. حساب حجم المونة المطلوب
        String usageType = request.getUsageType().toLowerCase();
        switch (usageType) {
            case "plastering" -> {
                // اللياسة: المساحة × السماكة (سم → م)
                BigDecimal thickness = request.getThicknessCm() != null ? 
                        request.getThicknessCm() : new BigDecimal("2"); // 2 سم افتراضي
                mortarVolume = area.multiply(thickness.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            }
            case "flooring" -> {
                // الأرضية: المساحة × السماكة
                BigDecimal thickness = request.getThicknessCm() != null ? 
                        request.getThicknessCm() : new BigDecimal("5"); // 5 سم افتراضي
                mortarVolume = area.multiply(thickness.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            }
            case "bricklaying" -> {
                // بناء الطابوق: تقريباً 0.03 م³ مونة لكل م² جدار
                mortarVolume = area.multiply(new BigDecimal("0.03"));
            }
            case "concrete" -> {
                // الخرسانة: الحجم مباشرة
                mortarVolume = volume != null ? volume : BigDecimal.ZERO;
                area = null; // لا يوجد مساحة في هذه الحالة
            }
            default -> {
                mortarVolume = volume != null ? volume : BigDecimal.ZERO;
            }
        }

        // إضافة الهدر
        BigDecimal wastePercentage = request.getWastePercentage() != null ?
                request.getWastePercentage() : new BigDecimal("0.10");
        mortarVolume = mortarVolume.multiply(BigDecimal.ONE.add(wastePercentage))
                .setScale(3, RoundingMode.HALF_UP);

        // 2. حساب كميات السمنت والرمل حسب نسبة الخلط
        // نسبة الخلط 1:X تعني 1 جزء سمنت : X أجزاء رمل
        int mixRatio = request.getMixRatio() != null ? request.getMixRatio() : 4;
        int totalParts = 1 + mixRatio;

        // حجم السمنت الجاف (م³)
        BigDecimal cementVolumeM3 = mortarVolume
                .divide(BigDecimal.valueOf(totalParts), 4, RoundingMode.HALF_UP);
        
        // وزن السمنت (طن) = الحجم × الكثافة
        BigDecimal cementTon = cementVolumeM3.multiply(CEMENT_DENSITY)
                .setScale(3, RoundingMode.HALF_UP);
        
        // عدد أكياس السمنت
        int cementBags = cementTon.multiply(BigDecimal.valueOf(1000))
                .divide(BigDecimal.valueOf(CEMENT_BAG_WEIGHT_KG), 0, RoundingMode.CEILING)
                .intValue();

        // حجم الرمل (م³)
        BigDecimal sandM3 = mortarVolume
                .multiply(BigDecimal.valueOf(mixRatio))
                .divide(BigDecimal.valueOf(totalParts), 2, RoundingMode.HALF_UP);

        // 3. حساب الكلف
        BigDecimal cementPrice = request.getCementPricePerTon() != null ?
                request.getCementPricePerTon() : DEFAULT_CEMENT_PRICE_PER_TON;
        BigDecimal sandPrice = request.getSandPricePerM3() != null ?
                request.getSandPricePerM3() : DEFAULT_SAND_PRICE_PER_M3;
        BigDecimal laborCostPerM2 = request.getLaborCostPerM2() != null ?
                request.getLaborCostPerM2() : DEFAULT_LABOR_COST_PER_M2;

        BigDecimal cementCost = cementTon.multiply(cementPrice)
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal sandCost = sandM3.multiply(sandPrice)
                .setScale(0, RoundingMode.HALF_UP);
        
        // كلفة العمالة (حسب المساحة أو الحجم)
        BigDecimal laborCost;
        if (area != null && area.compareTo(BigDecimal.ZERO) > 0) {
            laborCost = area.multiply(laborCostPerM2).setScale(0, RoundingMode.HALF_UP);
        } else {
            // للخرسانة: نحسب العمالة حسب الحجم (تقريباً 50000 لكل م³)
            laborCost = mortarVolume.multiply(new BigDecimal("50000"))
                    .setScale(0, RoundingMode.HALF_UP);
        }

        BigDecimal totalCost = cementCost.add(sandCost).add(laborCost);

        // الكلفة لكل م²
        BigDecimal costPerM2 = BigDecimal.ZERO;
        if (area != null && area.compareTo(BigDecimal.ZERO) > 0) {
            costPerM2 = totalCost.divide(area, 0, RoundingMode.HALF_UP);
        }

        // تقدير أيام العمل (تقريبي: 20 م² يومياً للياسة، 30 م² للأرضية)
        int estimatedWorkDays = 1;
        if (area != null && area.compareTo(BigDecimal.ZERO) > 0) {
            int m2PerDay = usageType.equals("plastering") ? 20 : 30;
            estimatedWorkDays = area.divide(BigDecimal.valueOf(m2PerDay), 0, RoundingMode.CEILING).intValue();
        }

        log.debug("Cement cost calculation completed. Total: {} {}", totalCost, request.getCurrency());

        return CementCostResponse.builder()
                .area(area)
                .volume(volume)
                .mortarVolumeM3(mortarVolume)
                .cementTon(cementTon)
                .cementBags(cementBags)
                .sandM3(sandM3)
                .cementCost(cementCost)
                .sandCost(sandCost)
                .laborCost(laborCost)
                .totalCost(totalCost)
                .costPerM2(costPerM2.compareTo(BigDecimal.ZERO) > 0 ? costPerM2 : null)
                .currency(request.getCurrency())
                .usageType(request.getUsageType())
                .cementType(request.getCementType())
                .mixRatioUsed("1:" + mixRatio)
                .estimatedWorkDays(estimatedWorkDays)
                .inputSummary(request)
                .build();
    }
}
