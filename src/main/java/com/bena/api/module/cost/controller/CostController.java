package com.bena.api.module.cost.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.cost.dto.brick.BrickCostRequest;
import com.bena.api.module.cost.dto.brick.BrickCostResponse;
import com.bena.api.module.cost.dto.cement.CementCostRequest;
import com.bena.api.module.cost.dto.cement.CementCostResponse;
import com.bena.api.module.cost.dto.concrete.ConcreteCostRequest;
import com.bena.api.module.cost.dto.concrete.ConcreteCostResponse;
import com.bena.api.module.cost.dto.electrical.ElectricalCostRequest;
import com.bena.api.module.cost.dto.electrical.ElectricalCostResponse;
import com.bena.api.module.cost.dto.foundation.FoundationCostRequest;
import com.bena.api.module.cost.dto.foundation.FoundationCostResponse;
import com.bena.api.module.cost.dto.plumbing.PlumbingCostRequest;
import com.bena.api.module.cost.dto.plumbing.PlumbingCostResponse;
import com.bena.api.module.cost.dto.steel.SteelCostRequest;
import com.bena.api.module.cost.dto.steel.SteelCostResponse;
import com.bena.api.module.cost.dto.tiles.TilesCostRequest;
import com.bena.api.module.cost.dto.tiles.TilesCostResponse;
import com.bena.api.module.cost.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * قسم حساب الكلفة - أدوات المقاول
 * Cost Calculation Section - Contractor Tools
 */
@RestController
@RequestMapping("/v1/cost")
@RequiredArgsConstructor
@Tag(name = "Cost Calculation", description = "حساب كلفة البناء - أدوات المقاول")
public class CostController {

    private final BrickCostService brickCostService;
    private final CementCostService cementCostService;
    private final SteelCostService steelCostService;
    private final ConcreteCostService concreteCostService;
    private final FoundationCostService foundationCostService;
    private final TilesCostService tilesCostService;
    private final ElectricalCostService electricalCostService;
    private final PlumbingCostService plumbingCostService;

    // ==================== حساب كلفة الطابوق ====================

    @PostMapping("/brick")
    @Operation(
            summary = "حساب كلفة الطابوق",
            description = "يحسب كلفة الطابوق شاملة المواد والعمالة والمونة"
    )
    public ResponseEntity<ApiResponse<BrickCostResponse>> calculateBrickCost(
            @Valid @RequestBody BrickCostRequest request) {
        
        BrickCostResponse result = brickCostService.calculate(request);
        return ResponseEntity.ok(ApiResponse.success(result, "تم حساب كلفة الطابوق بنجاح"));
    }

    // ==================== حساب كلفة السمنت ====================

    @PostMapping("/cement")
    @Operation(
            summary = "حساب كلفة السمنت",
            description = "يحسب كلفة السمنت والرمل لبخ أو الأرضية أو البناء"
    )
    public ResponseEntity<ApiResponse<CementCostResponse>> calculateCementCost(
            @Valid @RequestBody CementCostRequest request) {
        
        CementCostResponse result = cementCostService.calculate(request);
        return ResponseEntity.ok(ApiResponse.success(result, "تم حساب كلفة السمنت بنجاح"));
    }

    // ==================== حساب كلفة الحديد ====================

    @PostMapping("/steel")
    @Operation(
            summary = "حساب كلفة الحديد",
            description = "يحسب كلفة الحديد حسب الحجم أو الوزن أو القضبان"
    )
    public ResponseEntity<ApiResponse<SteelCostResponse>> calculateSteelCost(
            @Valid @RequestBody SteelCostRequest request) {
        
        SteelCostResponse result = steelCostService.calculate(request);
        return ResponseEntity.ok(ApiResponse.success(result, "تم حساب كلفة الحديد بنجاح"));
    }

    // ==================== حساب كلفة الصبة (الخرسانة) ====================

    @PostMapping("/concrete")
    @Operation(
            summary = "حساب كلفة الصبة",
            description = "يحسب كلفة الصبة (أساس، سقف، عمود، جسر) شاملة الخرسانة والحديد والقالب والعمالة"
    )
    public ResponseEntity<ApiResponse<ConcreteCostResponse>> calculateConcreteCost(
            @Valid @RequestBody ConcreteCostRequest request) {
        
        ConcreteCostResponse result = concreteCostService.calculate(request);
        return ResponseEntity.ok(ApiResponse.success(result, "تم حساب كلفة الصبة بنجاح"));
    }

    // ==================== حساب كلفة الأساس والدفان ====================

    @PostMapping("/foundation")
    @Operation(
            summary = "حساب كلفة الأساس والدفان والسبيس",
            description = "يحسب كلفة الحفر والقواعد والدفان والسبيس شاملة جميع المواد والعمالة"
    )
    public ResponseEntity<ApiResponse<FoundationCostResponse>> calculateFoundationCost(
            @Valid @RequestBody FoundationCostRequest request) {
        
        FoundationCostResponse result = foundationCostService.calculate(request);
        return ResponseEntity.ok(ApiResponse.success(result, "تم حساب كلفة الأساس بنجاح"));
    }

    // ==================== حساب كلفة الكاشي والسيراميك ====================

    @PostMapping("/tiles")
    @Operation(
            summary = "حساب كلفة الكاشي والسيراميك",
            description = "يحسب كلفة البلاط (سيراميك، بورسلان، رخام) شاملة الغراء والسمنت الأبيض والعمالة"
    )
    public ResponseEntity<ApiResponse<TilesCostResponse>> calculateTilesCost(
            @Valid @RequestBody TilesCostRequest request) {
        
        TilesCostResponse result = tilesCostService.calculate(request);
        return ResponseEntity.ok(ApiResponse.success(result, "تم حساب كلفة الكاشي بنجاح"));
    }

    // ==================== حساب كلفة الكهرباء ====================

    @PostMapping("/electrical")
    @Operation(
            summary = "حساب كلفة الكهرباء",
            description = "يحسب كلفة الأسلاك والمفاتيح والبرايز والقواطع والعمالة"
    )
    public ResponseEntity<ApiResponse<ElectricalCostResponse>> calculateElectricalCost(
            @Valid @RequestBody ElectricalCostRequest request) {
        
        ElectricalCostResponse result = electricalCostService.calculate(request);
        return ResponseEntity.ok(ApiResponse.success(result, "تم حساب كلفة الكهرباء بنجاح"));
    }

    // ==================== حساب كلفة السباكة (الماء والمجاري) ====================

    @PostMapping("/plumbing")
    @Operation(
            summary = "حساب كلفة السباكة",
            description = "يحسب كلفة أنابيب الماء والصرف والأدوات الصحية والعمالة"
    )
    public ResponseEntity<ApiResponse<PlumbingCostResponse>> calculatePlumbingCost(
            @Valid @RequestBody PlumbingCostRequest request) {
        
        PlumbingCostResponse result = plumbingCostService.calculate(request);
        return ResponseEntity.ok(ApiResponse.success(result, "تم حساب كلفة السباكة بنجاح"));
    }

    // ==================== قائمة أنواع الحسابات المتاحة ====================

    @GetMapping("/types")
    @Operation(
            summary = "أنواع الحسابات المتاحة",
            description = "يعرض قائمة بجميع أنواع حسابات الكلفة المتاحة في النظام"
    )
    public ResponseEntity<ApiResponse<Object>> getAvailableCalculationTypes() {
        var types = java.util.List.of(
                java.util.Map.of(
                        "code", "brick",
                        "nameAr", "حساب كلفة الطابوق",
                        "nameEn", "Brick Cost",
                        "endpoint", "/v1/cost/brick"
                ),
                java.util.Map.of(
                        "code", "cement",
                        "nameAr", "حساب كلفة السمنت",
                        "nameEn", "Cement Cost",
                        "endpoint", "/v1/cost/cement"
                ),
                java.util.Map.of(
                        "code", "steel",
                        "nameAr", "حساب كلفة الحديد",
                        "nameEn", "Steel/Rebar Cost",
                        "endpoint", "/v1/cost/steel"
                ),
                java.util.Map.of(
                        "code", "concrete",
                        "nameAr", "حساب كلفة الصبة",
                        "nameEn", "Concrete Cost",
                        "endpoint", "/v1/cost/concrete"
                ),
                java.util.Map.of(
                        "code", "foundation",
                        "nameAr", "حساب كلفة الأساس والدفان",
                        "nameEn", "Foundation Cost",
                        "endpoint", "/v1/cost/foundation"
                ),
                java.util.Map.of(
                        "code", "tiles",
                        "nameAr", "حساب كلفة الكاشي والسيراميك",
                        "nameEn", "Tiles Cost",
                        "endpoint", "/v1/cost/tiles"
                ),
                java.util.Map.of(
                        "code", "electrical",
                        "nameAr", "حساب كلفة الكهرباء",
                        "nameEn", "Electrical Cost",
                        "endpoint", "/v1/cost/electrical"
                ),
                java.util.Map.of(
                        "code", "plumbing",
                        "nameAr", "حساب كلفة السباكة (الماء والمجاري)",
                        "nameEn", "Plumbing Cost",
                        "endpoint", "/v1/cost/plumbing"
                )
        );
        
        return ResponseEntity.ok(ApiResponse.success(types));
    }
}
