package com.bena.api.module.cost.service;

import com.bena.api.module.cost.dto.steel.SteelCostRequest;
import com.bena.api.module.cost.dto.steel.SteelCostResponse;
import com.bena.api.module.cost.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * خدمة حساب كلفة الحديد
 * Steel/Rebar Cost Calculation Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SteelCostService {

    private final MaterialRepository materialRepository;

    // القيم الافتراضية
    private static final BigDecimal DEFAULT_STEEL_PRICE_PER_TON = new BigDecimal("1200000");
    private static final BigDecimal DEFAULT_CUTTING_COST_PER_TON = new BigDecimal("50000");
    private static final BigDecimal DEFAULT_INSTALLATION_COST_PER_TON = new BigDecimal("100000");

    // وزن المتر الطولي لكل قطر (كغ/م)
    private static final Map<Integer, BigDecimal> WEIGHT_PER_METER = Map.ofEntries(
            Map.entry(8, new BigDecimal("0.395")),
            Map.entry(10, new BigDecimal("0.617")),
            Map.entry(12, new BigDecimal("0.888")),
            Map.entry(14, new BigDecimal("1.208")),
            Map.entry(16, new BigDecimal("1.578")),
            Map.entry(18, new BigDecimal("1.998")),
            Map.entry(20, new BigDecimal("2.466")),
            Map.entry(22, new BigDecimal("2.984")),
            Map.entry(25, new BigDecimal("3.853")),
            Map.entry(28, new BigDecimal("4.834")),
            Map.entry(32, new BigDecimal("6.313"))
    );

    // نسب الحديد حسب العنصر الإنشائي (كغ/م³)
    private static final Map<String, BigDecimal> STEEL_RATIOS = Map.of(
            "foundation", new BigDecimal("100"),
            "slab", new BigDecimal("80"),
            "column", new BigDecimal("150"),
            "beam", new BigDecimal("120"),
            "wall", new BigDecimal("60")
    );

    public SteelCostResponse calculate(SteelCostRequest request) {
        log.debug("Calculating steel cost with method: {}", request.getCalculationMethod());

        BigDecimal weightTonNet;
        BigDecimal steelRatioUsed = null;
        List<SteelCostResponse.BarDetail> barDetails = null;
        String structuralElement = request.getStructuralElement();

        String method = request.getCalculationMethod().toLowerCase();

        switch (method) {
            case "by_volume" -> {
                // حساب حسب حجم الخرسانة
                BigDecimal concreteVolume = request.getConcreteVolumeM3();
                
                // تحديد نسبة الحديد
                steelRatioUsed = request.getSteelRatioKgPerM3();
                if (steelRatioUsed == null && structuralElement != null) {
                    steelRatioUsed = STEEL_RATIOS.getOrDefault(
                            structuralElement.toLowerCase(), 
                            new BigDecimal("100")
                    );
                } else if (steelRatioUsed == null) {
                    steelRatioUsed = new BigDecimal("100"); // افتراضي
                }

                // الوزن (كغ) = الحجم × النسبة
                BigDecimal weightKg = concreteVolume.multiply(steelRatioUsed);
                weightTonNet = weightKg.divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP);
            }
            case "by_weight" -> {
                // حساب حسب الوزن المباشر
                weightTonNet = request.getWeightTon();
            }
            case "by_bars" -> {
                // حساب حسب القضبان
                BigDecimal totalWeightKg = BigDecimal.ZERO;
                barDetails = new ArrayList<>();

                if (request.getBars() != null) {
                    for (SteelCostRequest.SteelBar bar : request.getBars()) {
                        BigDecimal weightPerMeter = WEIGHT_PER_METER.getOrDefault(
                                bar.getDiameterMm(), 
                                new BigDecimal("1.0")
                        );
                        
                        BigDecimal barWeight = weightPerMeter
                                .multiply(bar.getLengthM())
                                .multiply(BigDecimal.valueOf(bar.getQuantity()));
                        
                        totalWeightKg = totalWeightKg.add(barWeight);

                        barDetails.add(SteelCostResponse.BarDetail.builder()
                                .diameterMm(bar.getDiameterMm())
                                .lengthM(bar.getLengthM())
                                .quantity(bar.getQuantity())
                                .weightPerMeter(weightPerMeter)
                                .totalWeight(barWeight.setScale(2, RoundingMode.HALF_UP))
                                .build());
                    }
                }

                weightTonNet = totalWeightKg.divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP);
            }
            default -> {
                throw new IllegalArgumentException("طريقة الحساب غير معروفة: " + method);
            }
        }

        // إضافة الهدر
        BigDecimal wastePercentage = request.getWastePercentage() != null ?
                request.getWastePercentage() : new BigDecimal("0.05");
        BigDecimal weightTonWithWaste = weightTonNet.multiply(BigDecimal.ONE.add(wastePercentage))
                .setScale(3, RoundingMode.HALF_UP);

        BigDecimal weightKg = weightTonWithWaste.multiply(BigDecimal.valueOf(1000))
                .setScale(0, RoundingMode.HALF_UP);

        // تقدير عدد القضبان 12 متر (باستخدام قطر 12 ملم كمتوسط)
        BigDecimal avgWeightPer12m = new BigDecimal("0.888").multiply(BigDecimal.valueOf(12)); // ~10.66 كغ
        int estimatedBars = weightKg.divide(avgWeightPer12m, 0, RoundingMode.CEILING).intValue();

        // حساب الكلف
        BigDecimal steelPrice = request.getSteelPricePerTon() != null ?
                request.getSteelPricePerTon() : DEFAULT_STEEL_PRICE_PER_TON;
        BigDecimal cuttingCostPerTon = request.getCuttingCostPerTon() != null ?
                request.getCuttingCostPerTon() : DEFAULT_CUTTING_COST_PER_TON;
        BigDecimal installationCostPerTon = request.getInstallationCostPerTon() != null ?
                request.getInstallationCostPerTon() : DEFAULT_INSTALLATION_COST_PER_TON;

        BigDecimal steelCost = weightTonWithWaste.multiply(steelPrice)
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal cuttingCost = weightTonWithWaste.multiply(cuttingCostPerTon)
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal installationCost = weightTonWithWaste.multiply(installationCostPerTon)
                .setScale(0, RoundingMode.HALF_UP);

        BigDecimal totalCost = steelCost.add(cuttingCost).add(installationCost);

        // الكلفة لكل م³ (إذا كان الحساب بالحجم)
        BigDecimal costPerM3 = null;
        if ("by_volume".equals(method) && request.getConcreteVolumeM3() != null) {
            costPerM3 = totalCost.divide(request.getConcreteVolumeM3(), 0, RoundingMode.HALF_UP);
        }

        // تقدير أيام العمل (تقريبي: 1 طن يومياً)
        int estimatedWorkDays = weightTonWithWaste.setScale(0, RoundingMode.CEILING).intValue();
        if (estimatedWorkDays < 1) estimatedWorkDays = 1;

        log.debug("Steel cost calculation completed. Total: {} {}", totalCost, request.getCurrency());

        return SteelCostResponse.builder()
                .weightTonNet(weightTonNet.setScale(3, RoundingMode.HALF_UP))
                .weightTonWithWaste(weightTonWithWaste)
                .weightKg(weightKg)
                .estimatedBars12m(estimatedBars)
                .steelCost(steelCost)
                .cuttingCost(cuttingCost)
                .installationCost(installationCost)
                .totalCost(totalCost)
                .costPerM3(costPerM3)
                .currency(request.getCurrency())
                .calculationMethod(method)
                .structuralElement(structuralElement)
                .steelRatioUsed(steelRatioUsed)
                .barDetails(barDetails)
                .estimatedWorkDays(estimatedWorkDays)
                .inputSummary(request)
                .build();
    }
}
