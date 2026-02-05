package com.bena.api.module.cost.service;

import com.bena.api.module.cost.dto.plumbing.PlumbingCostRequest;
import com.bena.api.module.cost.dto.plumbing.PlumbingCostResponse;
import com.bena.api.module.cost.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * خدمة حساب كلفة السباكة (الماء والمجاري)
 * Plumbing Cost Calculation Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlumbingCostService {

    private final MaterialRepository materialRepository;

    // القيم الافتراضية
    private static final BigDecimal DEFAULT_WATER_PIPE_PRICE_PER_METER = new BigDecimal("3000");
    private static final BigDecimal DEFAULT_DRAIN_PIPE_PRICE_PER_METER = new BigDecimal("5000");
    private static final BigDecimal DEFAULT_TOILET_PRICE = new BigDecimal("250000");
    private static final BigDecimal DEFAULT_SINK_PRICE = new BigDecimal("150000");
    private static final BigDecimal DEFAULT_MIXER_PRICE = new BigDecimal("80000");
    private static final BigDecimal DEFAULT_LABOR_COST_PER_POINT = new BigDecimal("25000");
    
    // أسعار الإضافات
    private static final BigDecimal DEFAULT_WATER_HEATER_PRICE = new BigDecimal("350000");
    private static final BigDecimal DEFAULT_WATER_TANK_PRICE = new BigDecimal("500000");
    private static final BigDecimal DEFAULT_WATER_PUMP_PRICE = new BigDecimal("400000");
    
    // معدلات تقديرية
    private static final int WATER_POINTS_PER_BATHROOM = 5; // مرحاض + مغسلة + دوش + 2 حنفية
    private static final int DRAIN_POINTS_PER_BATHROOM = 3; // مرحاض + مغسلة + دوش
    private static final int WATER_POINTS_PER_KITCHEN = 3;
    private static final int DRAIN_POINTS_PER_KITCHEN = 2;
    private static final BigDecimal PIPE_METERS_PER_POINT = new BigDecimal("4");
    private static final BigDecimal FITTINGS_COST_RATIO = new BigDecimal("0.30"); // 30% من كلفة الأنابيب

    public PlumbingCostResponse calculate(PlumbingCostRequest request) {
        log.debug("Calculating plumbing cost with method: {}", request.getCalculationType());

        int totalWaterPoints;
        int totalDrainPoints;
        int toiletCount;
        int sinkCount;
        int mixerCount;
        BigDecimal buildingArea = request.getBuildingArea();

        String calcType = request.getCalculationType().toLowerCase();

        switch (calcType) {
            case "by_area" -> {
                // حساب تقديري حسب المساحة
                int floors = request.getFloors() != null ? request.getFloors() : 1;
                BigDecimal totalArea = buildingArea.multiply(BigDecimal.valueOf(floors));
                
                // تقدير عدد الحمامات والمطابخ حسب المساحة
                int estimatedBathrooms = Math.max(1, totalArea.divide(BigDecimal.valueOf(50), 0, RoundingMode.HALF_UP).intValue());
                int estimatedKitchens = Math.max(1, floors);
                
                totalWaterPoints = (estimatedBathrooms * WATER_POINTS_PER_BATHROOM) + 
                                   (estimatedKitchens * WATER_POINTS_PER_KITCHEN);
                totalDrainPoints = (estimatedBathrooms * DRAIN_POINTS_PER_BATHROOM) + 
                                   (estimatedKitchens * DRAIN_POINTS_PER_KITCHEN);
                
                toiletCount = estimatedBathrooms;
                sinkCount = estimatedBathrooms + estimatedKitchens;
                mixerCount = totalWaterPoints / 2;
            }
            case "by_points" -> {
                // حساب حسب النقاط المحددة
                int bathroomCount = request.getBathroomCount() != null ? request.getBathroomCount() : 0;
                int kitchenCount = request.getKitchenCount() != null ? request.getKitchenCount() : 0;
                
                if (request.getWaterPoints() != null) {
                    totalWaterPoints = request.getWaterPoints();
                } else {
                    totalWaterPoints = (bathroomCount * WATER_POINTS_PER_BATHROOM) + 
                                       (kitchenCount * WATER_POINTS_PER_KITCHEN);
                }
                
                if (request.getDrainPoints() != null) {
                    totalDrainPoints = request.getDrainPoints();
                } else {
                    totalDrainPoints = (bathroomCount * DRAIN_POINTS_PER_BATHROOM) + 
                                       (kitchenCount * DRAIN_POINTS_PER_KITCHEN);
                }
                
                toiletCount = request.getToiletCount() != null ? request.getToiletCount() : bathroomCount;
                sinkCount = request.getSinkCount() != null ? request.getSinkCount() : (bathroomCount + kitchenCount);
                mixerCount = totalWaterPoints / 2;
            }
            default -> {
                throw new IllegalArgumentException("نوع الحساب غير معروف: " + calcType);
            }
        }

        // إضافة الهدر
        BigDecimal wastePercentage = request.getWastePercentage() != null ?
                request.getWastePercentage() : new BigDecimal("0.10");

        // حساب أطوال الأنابيب
        BigDecimal waterPipeLength = BigDecimal.valueOf(totalWaterPoints)
                .multiply(PIPE_METERS_PER_POINT)
                .multiply(BigDecimal.ONE.add(wastePercentage))
                .setScale(0, RoundingMode.CEILING);
        
        BigDecimal drainPipeLength = BigDecimal.valueOf(totalDrainPoints)
                .multiply(PIPE_METERS_PER_POINT.multiply(new BigDecimal("1.5"))) // أنابيب الصرف أطول
                .multiply(BigDecimal.ONE.add(wastePercentage))
                .setScale(0, RoundingMode.CEILING);

        // جلب الأسعار
        BigDecimal waterPipePrice = request.getWaterPipePricePerMeter() != null ?
                request.getWaterPipePricePerMeter() : DEFAULT_WATER_PIPE_PRICE_PER_METER;
        BigDecimal drainPipePrice = request.getDrainPipePricePerMeter() != null ?
                request.getDrainPipePricePerMeter() : DEFAULT_DRAIN_PIPE_PRICE_PER_METER;
        BigDecimal toiletPrice = request.getToiletPrice() != null ?
                request.getToiletPrice() : DEFAULT_TOILET_PRICE;
        BigDecimal sinkPrice = request.getSinkPrice() != null ?
                request.getSinkPrice() : DEFAULT_SINK_PRICE;
        BigDecimal mixerPrice = request.getMixerPrice() != null ?
                request.getMixerPrice() : DEFAULT_MIXER_PRICE;
        BigDecimal laborCostPerPoint = request.getLaborCostPerPoint() != null ?
                request.getLaborCostPerPoint() : DEFAULT_LABOR_COST_PER_POINT;

        // حساب الكلف
        BigDecimal waterPipeCost = waterPipeLength.multiply(waterPipePrice).setScale(0, RoundingMode.HALF_UP);
        BigDecimal drainPipeCost = drainPipeLength.multiply(drainPipePrice).setScale(0, RoundingMode.HALF_UP);
        BigDecimal toiletCost = BigDecimal.valueOf(toiletCount).multiply(toiletPrice).setScale(0, RoundingMode.HALF_UP);
        BigDecimal sinkCost = BigDecimal.valueOf(sinkCount).multiply(sinkPrice).setScale(0, RoundingMode.HALF_UP);
        BigDecimal mixerCost = BigDecimal.valueOf(mixerCount).multiply(mixerPrice).setScale(0, RoundingMode.HALF_UP);
        
        // كلفة الوصلات والإكسسوارات
        BigDecimal fittingsCost = waterPipeCost.add(drainPipeCost)
                .multiply(FITTINGS_COST_RATIO).setScale(0, RoundingMode.HALF_UP);

        // الإضافات
        BigDecimal waterHeaterCost = Boolean.TRUE.equals(request.getIncludeWaterHeater()) ?
                DEFAULT_WATER_HEATER_PRICE : BigDecimal.ZERO;
        BigDecimal waterTankCost = Boolean.TRUE.equals(request.getIncludeWaterTank()) ?
                DEFAULT_WATER_TANK_PRICE : BigDecimal.ZERO;
        BigDecimal waterPumpCost = Boolean.TRUE.equals(request.getIncludeWaterPump()) ?
                DEFAULT_WATER_PUMP_PRICE : BigDecimal.ZERO;

        int totalPoints = totalWaterPoints + totalDrainPoints;
        BigDecimal laborCost = BigDecimal.valueOf(totalPoints).multiply(laborCostPerPoint)
                .setScale(0, RoundingMode.HALF_UP);

        BigDecimal totalCost = waterPipeCost.add(drainPipeCost)
                .add(toiletCost).add(sinkCost).add(mixerCost)
                .add(fittingsCost).add(laborCost)
                .add(waterHeaterCost).add(waterTankCost).add(waterPumpCost);

        // الكلفة لكل م²
        BigDecimal costPerM2 = null;
        if (buildingArea != null && buildingArea.compareTo(BigDecimal.ZERO) > 0) {
            int floors = request.getFloors() != null ? request.getFloors() : 1;
            BigDecimal totalArea = buildingArea.multiply(BigDecimal.valueOf(floors));
            costPerM2 = totalCost.divide(totalArea, 0, RoundingMode.HALF_UP);
        }

        BigDecimal costPerPoint = totalPoints > 0 ?
                totalCost.divide(BigDecimal.valueOf(totalPoints), 0, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        // تقدير أيام العمل (تقريبي: 5-6 نقاط يومياً)
        int estimatedWorkDays = Math.max(1, totalPoints / 5);

        log.debug("Plumbing cost calculation completed. Total: {} {}", totalCost, request.getCurrency());

        return PlumbingCostResponse.builder()
                .totalWaterPoints(totalWaterPoints)
                .totalDrainPoints(totalDrainPoints)
                .waterPipeLength(waterPipeLength)
                .drainPipeLength(drainPipeLength)
                .toiletCount(toiletCount)
                .sinkCount(sinkCount)
                .mixerCount(mixerCount)
                .waterPipeCost(waterPipeCost)
                .drainPipeCost(drainPipeCost)
                .toiletCost(toiletCost)
                .sinkCost(sinkCost)
                .mixerCost(mixerCost)
                .fittingsCost(fittingsCost)
                .waterHeaterCost(waterHeaterCost.compareTo(BigDecimal.ZERO) > 0 ? waterHeaterCost : null)
                .waterTankCost(waterTankCost.compareTo(BigDecimal.ZERO) > 0 ? waterTankCost : null)
                .waterPumpCost(waterPumpCost.compareTo(BigDecimal.ZERO) > 0 ? waterPumpCost : null)
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
