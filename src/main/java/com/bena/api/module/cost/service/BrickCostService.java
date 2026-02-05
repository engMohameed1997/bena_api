package com.bena.api.module.cost.service;

import com.bena.api.module.cost.dto.brick.BrickCostRequest;
import com.bena.api.module.cost.dto.brick.BrickCostResponse;
import com.bena.api.module.cost.entity.Material;
import com.bena.api.module.cost.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * خدمة حساب كلفة الطابوق
 * Brick Cost Calculation Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BrickCostService {

    private final MaterialRepository materialRepository;

    // القيم الافتراضية
    private static final int DEFAULT_BRICKS_PER_M2 = 130;
    private static final BigDecimal DEFAULT_BRICK_PRICE_PER_1000 = new BigDecimal("150000");
    private static final BigDecimal DEFAULT_LABOR_PRICE_PER_1000 = new BigDecimal("80000");
    private static final BigDecimal DEFAULT_MORTAR_COST_PER_M2 = new BigDecimal("5000");
    private static final int BRICKS_PER_WORKER_PER_DAY = 500;

    public BrickCostResponse calculate(BrickCostRequest request) {
        log.debug("Calculating brick cost for wall area: {} m²", request.getWallArea());

        // 1. حساب المساحة الصافية
        BigDecimal wallAreaNet = request.getWallArea()
                .subtract(request.getOpeningsArea() != null ? request.getOpeningsArea() : BigDecimal.ZERO);
        if (wallAreaNet.compareTo(BigDecimal.ZERO) < 0) {
            wallAreaNet = BigDecimal.ZERO;
        }

        // 2. جلب بيانات نوع الطابوق من قاعدة البيانات (إن وجد)
        Integer bricksPerM2 = request.getBricksPerM2();
        BigDecimal brickPrice = request.getBrickPricePer1000();
        BigDecimal laborPrice = request.getLaborPricePer1000();
        BigDecimal mortarCost = request.getMortarCostPerM2();
        String brickTypeName = "طابوق عادي";

        if (request.getBrickTypeCode() != null) {
            Material material = materialRepository.findByCode(request.getBrickTypeCode()).orElse(null);
            if (material != null) {
                brickTypeName = material.getNameAr();
                
                // استخراج bricks_per_m2 من specifications
                Map<String, Object> specs = material.getSpecifications();
                if (specs != null && specs.containsKey("bricks_per_m2")) {
                    Object bpm2 = specs.get("bricks_per_m2");
                    if (bpm2 instanceof Number) {
                        bricksPerM2 = bricksPerM2 != null ? bricksPerM2 : ((Number) bpm2).intValue();
                    }
                }
                
                // استخدام الأسعار الافتراضية من المادة إذا لم تُحدد
                if (brickPrice == null && material.getDefaultPrice() != null) {
                    brickPrice = material.getDefaultPrice();
                }
                if (laborPrice == null && material.getDefaultLaborCost() != null) {
                    laborPrice = material.getDefaultLaborCost();
                }
            }
        }

        // تطبيق القيم الافتراضية
        bricksPerM2 = bricksPerM2 != null ? bricksPerM2 : DEFAULT_BRICKS_PER_M2;
        brickPrice = brickPrice != null ? brickPrice : DEFAULT_BRICK_PRICE_PER_1000;
        laborPrice = laborPrice != null ? laborPrice : DEFAULT_LABOR_PRICE_PER_1000;
        mortarCost = mortarCost != null ? mortarCost : DEFAULT_MORTAR_COST_PER_M2;
        BigDecimal wastePercentage = request.getWastePercentage() != null ? 
                request.getWastePercentage() : new BigDecimal("0.07");

        // 3. حساب عدد الطابوق
        BigDecimal brickCountRaw = wallAreaNet.multiply(BigDecimal.valueOf(bricksPerM2));
        BigDecimal brickCountWithWaste = brickCountRaw.multiply(BigDecimal.ONE.add(wastePercentage));
        long brickCount = brickCountWithWaste.setScale(0, RoundingMode.CEILING).longValue();

        // 4. حساب الكلف
        // كلفة الطابوق = (عدد الطابوق / 1000) * سعر الألف
        BigDecimal brickCostTotal = BigDecimal.valueOf(brickCount)
                .divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP)
                .multiply(brickPrice)
                .setScale(0, RoundingMode.HALF_UP);

        // كلفة المونة = المساحة الصافية * كلفة المونة لكل م²
        BigDecimal mortarCostTotal = wallAreaNet.multiply(mortarCost)
                .setScale(0, RoundingMode.HALF_UP);

        // كلفة العمالة = (عدد الطابوق / 1000) * كلفة العمالة لكل ألف
        BigDecimal laborCostTotal = BigDecimal.valueOf(brickCount)
                .divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP)
                .multiply(laborPrice)
                .setScale(0, RoundingMode.HALF_UP);

        // الكلفة الكلية
        BigDecimal totalCost = brickCostTotal.add(mortarCostTotal).add(laborCostTotal);

        // الكلفة لكل م²
        BigDecimal costPerM2 = wallAreaNet.compareTo(BigDecimal.ZERO) > 0 ?
                totalCost.divide(wallAreaNet, 0, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        // 5. تقدير العمالة
        int estimatedWorkDays = (int) Math.ceil((double) brickCount / BRICKS_PER_WORKER_PER_DAY);
        int estimatedWorkers = Math.max(1, estimatedWorkDays / 5); // افتراض 5 أيام عمل

        log.debug("Brick cost calculation completed. Total: {} {}", totalCost, request.getCurrency());

        return BrickCostResponse.builder()
                .wallAreaNet(wallAreaNet)
                .brickCount(brickCount)
                .brickCountRaw(brickCountRaw.setScale(0, RoundingMode.HALF_UP).longValue())
                .brickCost(brickCostTotal)
                .mortarCost(mortarCostTotal)
                .laborCost(laborCostTotal)
                .totalCost(totalCost)
                .costPerM2(costPerM2)
                .currency(request.getCurrency())
                .brickType(brickTypeName)
                .bricksPerM2Used(bricksPerM2)
                .wastePercentageUsed(wastePercentage)
                .estimatedWorkDays(estimatedWorkDays)
                .estimatedWorkers(estimatedWorkers)
                .inputSummary(request)
                .build();
    }
}
