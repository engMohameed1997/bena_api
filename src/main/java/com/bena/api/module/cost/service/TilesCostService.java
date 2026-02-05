package com.bena.api.module.cost.service;

import com.bena.api.module.cost.dto.tiles.TilesCostRequest;
import com.bena.api.module.cost.dto.tiles.TilesCostResponse;
import com.bena.api.module.cost.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * خدمة حساب كلفة الكاشي والسيراميك
 * Tiles Cost Calculation Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TilesCostService {

    private final MaterialRepository materialRepository;

    // القيم الافتراضية
    private static final Map<String, BigDecimal> DEFAULT_TILE_PRICES = Map.of(
            "ceramic", new BigDecimal("25000"),
            "porcelain", new BigDecimal("45000"),
            "marble", new BigDecimal("80000"),
            "granite", new BigDecimal("100000")
    );
    
    private static final BigDecimal DEFAULT_WHITE_CEMENT_PRICE_PER_BAG = new BigDecimal("8000");
    private static final BigDecimal DEFAULT_ADHESIVE_PRICE_PER_BAG = new BigDecimal("12000");
    private static final BigDecimal DEFAULT_LABOR_COST_PER_M2 = new BigDecimal("15000");
    
    // استهلاك المواد
    private static final BigDecimal ADHESIVE_COVERAGE_M2_PER_BAG = new BigDecimal("5"); // 5 م² لكل كيس غراء
    private static final BigDecimal WHITE_CEMENT_COVERAGE_M2_PER_BAG = new BigDecimal("15"); // 15 م² لكل كيس سمنت أبيض
    private static final BigDecimal TILES_PER_BOX_M2 = new BigDecimal("1.44"); // 1.44 م² لكل صندوق (60×60)

    public TilesCostResponse calculate(TilesCostRequest request) {
        log.debug("Calculating tiles cost for type: {}, location: {}", 
                request.getTileType(), request.getLocation());

        BigDecimal area = request.getArea();
        
        // إضافة الهدر
        BigDecimal wastePercentage = request.getWastePercentage() != null ?
                request.getWastePercentage() : new BigDecimal("0.10");
        BigDecimal areaWithWaste = area.multiply(BigDecimal.ONE.add(wastePercentage))
                .setScale(2, RoundingMode.HALF_UP);

        // حساب الكميات
        int tileBoxes = areaWithWaste.divide(TILES_PER_BOX_M2, 0, RoundingMode.CEILING).intValue();
        int adhesiveBags = areaWithWaste.divide(ADHESIVE_COVERAGE_M2_PER_BAG, 0, RoundingMode.CEILING).intValue();
        int whiteCementBags = areaWithWaste.divide(WHITE_CEMENT_COVERAGE_M2_PER_BAG, 0, RoundingMode.CEILING).intValue();

        // جلب الأسعار
        String tileType = request.getTileType().toLowerCase();
        BigDecimal tilePrice = request.getTilePricePerM2() != null ?
                request.getTilePricePerM2() : DEFAULT_TILE_PRICES.getOrDefault(tileType, new BigDecimal("25000"));
        BigDecimal adhesivePrice = request.getAdhesivePricePerBag() != null ?
                request.getAdhesivePricePerBag() : DEFAULT_ADHESIVE_PRICE_PER_BAG;
        BigDecimal whiteCementPrice = request.getWhiteCementPricePerBag() != null ?
                request.getWhiteCementPricePerBag() : DEFAULT_WHITE_CEMENT_PRICE_PER_BAG;
        BigDecimal laborCostPerM2 = request.getLaborCostPerM2() != null ?
                request.getLaborCostPerM2() : DEFAULT_LABOR_COST_PER_M2;

        // حساب الكلف
        BigDecimal tilesCost = areaWithWaste.multiply(tilePrice)
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal adhesiveCost = BigDecimal.valueOf(adhesiveBags).multiply(adhesivePrice)
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal whiteCementCost = BigDecimal.valueOf(whiteCementBags).multiply(whiteCementPrice)
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal laborCost = area.multiply(laborCostPerM2)
                .setScale(0, RoundingMode.HALF_UP);

        // كلفة الوزرة (إن وجدت)
        BigDecimal baseboardCost = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(request.getIncludeBaseboard()) && request.getBaseboardLength() != null) {
            // الوزرة عادة 10 سم ارتفاع، نحسبها بسعر البلاط + 50% للقص
            BigDecimal baseboardArea = request.getBaseboardLength().multiply(new BigDecimal("0.10"));
            baseboardCost = baseboardArea.multiply(tilePrice).multiply(new BigDecimal("1.5"))
                    .setScale(0, RoundingMode.HALF_UP);
        }

        BigDecimal totalCost = tilesCost.add(adhesiveCost).add(whiteCementCost)
                .add(laborCost).add(baseboardCost);

        BigDecimal costPerM2 = area.compareTo(BigDecimal.ZERO) > 0 ?
                totalCost.divide(area, 0, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        // تقدير أيام العمل (تقريبي: 8-10 م² يومياً)
        int estimatedWorkDays = area.divide(BigDecimal.valueOf(9), 0, RoundingMode.CEILING).intValue();

        log.debug("Tiles cost calculation completed. Total: {} {}", totalCost, request.getCurrency());

        return TilesCostResponse.builder()
                .areaWithWaste(areaWithWaste)
                .tileBoxes(tileBoxes)
                .adhesiveBags(adhesiveBags)
                .whiteCementBags(whiteCementBags)
                .tilesCost(tilesCost)
                .adhesiveCost(adhesiveCost)
                .whiteCementCost(whiteCementCost)
                .baseboardCost(baseboardCost.compareTo(BigDecimal.ZERO) > 0 ? baseboardCost : null)
                .laborCost(laborCost)
                .totalCost(totalCost)
                .costPerM2(costPerM2)
                .currency(request.getCurrency())
                .tileType(request.getTileType())
                .location(request.getLocation())
                .estimatedWorkDays(estimatedWorkDays)
                .inputSummary(request)
                .build();
    }
}
