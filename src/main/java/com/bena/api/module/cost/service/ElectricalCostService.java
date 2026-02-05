package com.bena.api.module.cost.service;

import com.bena.api.module.cost.dto.electrical.ElectricalCostRequest;
import com.bena.api.module.cost.dto.electrical.ElectricalCostResponse;
import com.bena.api.module.cost.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * خدمة حساب كلفة الكهرباء
 * Electrical Cost Calculation Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ElectricalCostService {

    private final MaterialRepository materialRepository;

    // القيم الافتراضية
    private static final BigDecimal DEFAULT_WIRE_PRICE_PER_METER = new BigDecimal("1500");
    private static final BigDecimal DEFAULT_SWITCH_PRICE = new BigDecimal("8000");
    private static final BigDecimal DEFAULT_SOCKET_PRICE = new BigDecimal("6000");
    private static final BigDecimal DEFAULT_BREAKER_PRICE = new BigDecimal("15000");
    private static final BigDecimal DEFAULT_DISTRIBUTION_BOARD_PRICE = new BigDecimal("150000");
    private static final BigDecimal DEFAULT_LABOR_COST_PER_POINT = new BigDecimal("15000");
    
    // معدلات تقديرية
    private static final BigDecimal POINTS_PER_M2 = new BigDecimal("0.8"); // نقطة لكل 1.25 م²
    private static final BigDecimal WIRE_METERS_PER_POINT = new BigDecimal("8"); // 8 متر سلك لكل نقطة
    private static final BigDecimal CONDUIT_COST_RATIO = new BigDecimal("0.25"); // 25% من كلفة الأسلاك

    public ElectricalCostResponse calculate(ElectricalCostRequest request) {
        log.debug("Calculating electrical cost with method: {}", request.getCalculationType());

        int totalPoints;
        int lightPoints;
        int switchPoints;
        int socketPoints;
        BigDecimal buildingArea = request.getBuildingArea();

        String calcType = request.getCalculationType().toLowerCase();

        switch (calcType) {
            case "by_area" -> {
                // حساب تقديري حسب المساحة
                int floors = request.getFloors() != null ? request.getFloors() : 1;
                BigDecimal totalArea = buildingArea.multiply(BigDecimal.valueOf(floors));
                
                totalPoints = totalArea.multiply(POINTS_PER_M2).setScale(0, RoundingMode.CEILING).intValue();
                
                // توزيع تقديري للنقاط
                lightPoints = (int) (totalPoints * 0.35);
                switchPoints = (int) (totalPoints * 0.25);
                socketPoints = (int) (totalPoints * 0.30);
                int acPoints = (int) (totalPoints * 0.05);
                int heaterPoints = (int) (totalPoints * 0.05);
            }
            case "by_points" -> {
                // حساب حسب النقاط المحددة
                lightPoints = request.getLightPoints() != null ? request.getLightPoints() : 0;
                switchPoints = request.getSwitchPoints() != null ? request.getSwitchPoints() : 0;
                socketPoints = request.getSocketPoints() != null ? request.getSocketPoints() : 0;
                int acPoints = request.getAcPoints() != null ? request.getAcPoints() : 0;
                int heaterPoints = request.getHeaterPoints() != null ? request.getHeaterPoints() : 0;
                
                totalPoints = lightPoints + switchPoints + socketPoints + acPoints + heaterPoints;
            }
            default -> {
                throw new IllegalArgumentException("نوع الحساب غير معروف: " + calcType);
            }
        }

        // إضافة الهدر
        BigDecimal wastePercentage = request.getWastePercentage() != null ?
                request.getWastePercentage() : new BigDecimal("0.15");

        // حساب طول الأسلاك
        BigDecimal wireLength = BigDecimal.valueOf(totalPoints)
                .multiply(WIRE_METERS_PER_POINT)
                .multiply(BigDecimal.ONE.add(wastePercentage))
                .setScale(0, RoundingMode.CEILING);

        // عدد القواطع (تقريبي: قاطع لكل 4-6 نقاط)
        int breakerCount = Math.max(4, totalPoints / 5);
        
        // عدد لوحات التوزيع (واحدة لكل طابق أو لكل 30 قاطع)
        int floors = request.getFloors() != null ? request.getFloors() : 1;
        int distributionBoardCount = Math.max(1, Math.max(floors, breakerCount / 30));

        // جلب الأسعار
        BigDecimal wirePrice = request.getWirePricePerMeter() != null ?
                request.getWirePricePerMeter() : DEFAULT_WIRE_PRICE_PER_METER;
        BigDecimal switchPrice = request.getSwitchPrice() != null ?
                request.getSwitchPrice() : DEFAULT_SWITCH_PRICE;
        BigDecimal socketPrice = request.getSocketPrice() != null ?
                request.getSocketPrice() : DEFAULT_SOCKET_PRICE;
        BigDecimal breakerPrice = request.getBreakerPrice() != null ?
                request.getBreakerPrice() : DEFAULT_BREAKER_PRICE;
        BigDecimal distributionBoardPrice = request.getDistributionBoardPrice() != null ?
                request.getDistributionBoardPrice() : DEFAULT_DISTRIBUTION_BOARD_PRICE;
        BigDecimal laborCostPerPoint = request.getLaborCostPerPoint() != null ?
                request.getLaborCostPerPoint() : DEFAULT_LABOR_COST_PER_POINT;

        // حساب الكلف
        BigDecimal wireCost = wireLength.multiply(wirePrice).setScale(0, RoundingMode.HALF_UP);
        BigDecimal switchCost = BigDecimal.valueOf(switchPoints).multiply(switchPrice).setScale(0, RoundingMode.HALF_UP);
        BigDecimal socketCost = BigDecimal.valueOf(socketPoints).multiply(socketPrice).setScale(0, RoundingMode.HALF_UP);
        BigDecimal breakerCost = BigDecimal.valueOf(breakerCount).multiply(breakerPrice).setScale(0, RoundingMode.HALF_UP);
        BigDecimal distributionBoardCost = BigDecimal.valueOf(distributionBoardCount)
                .multiply(distributionBoardPrice).setScale(0, RoundingMode.HALF_UP);
        
        // كلفة الأنابيب والعلب (تقديرية)
        BigDecimal conduitAndBoxesCost = wireCost.multiply(CONDUIT_COST_RATIO).setScale(0, RoundingMode.HALF_UP);
        
        BigDecimal laborCost = BigDecimal.valueOf(totalPoints).multiply(laborCostPerPoint)
                .setScale(0, RoundingMode.HALF_UP);

        BigDecimal totalCost = wireCost.add(switchCost).add(socketCost)
                .add(breakerCost).add(distributionBoardCost)
                .add(conduitAndBoxesCost).add(laborCost);

        // الكلفة لكل م²
        BigDecimal costPerM2 = null;
        if (buildingArea != null && buildingArea.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalArea = buildingArea.multiply(BigDecimal.valueOf(floors));
            costPerM2 = totalCost.divide(totalArea, 0, RoundingMode.HALF_UP);
        }

        BigDecimal costPerPoint = totalPoints > 0 ?
                totalCost.divide(BigDecimal.valueOf(totalPoints), 0, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        // تقدير أيام العمل (تقريبي: 8-10 نقاط يومياً)
        int estimatedWorkDays = Math.max(1, totalPoints / 9);

        log.debug("Electrical cost calculation completed. Total: {} {}", totalCost, request.getCurrency());

        return ElectricalCostResponse.builder()
                .totalPoints(totalPoints)
                .wireLength(wireLength)
                .switchCount(switchPoints)
                .socketCount(socketPoints)
                .breakerCount(breakerCount)
                .distributionBoardCount(distributionBoardCount)
                .wireCost(wireCost)
                .switchCost(switchCost)
                .socketCost(socketCost)
                .breakerCost(breakerCost)
                .distributionBoardCost(distributionBoardCost)
                .conduitAndBoxesCost(conduitAndBoxesCost)
                .laborCost(laborCost)
                .totalCost(totalCost)
                .costPerM2(costPerM2)
                .costPerPoint(costPerPoint)
                .currency(request.getCurrency())
                .calculationType(calcType)
                .estimatedWorkDays(estimatedWorkDays)
                .inputSummary(request)
                .build();
    }
}
