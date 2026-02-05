package com.bena.api.module.consultation.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.common.dto.PageResponse;
import com.bena.api.module.consultation.dto.ConsultationCategoryResponse;
import com.bena.api.module.consultation.dto.ConsultationItemResponse;
import com.bena.api.module.consultation.dto.ConsultationSpecialistResponse;
import com.bena.api.module.consultation.service.ConsultationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * قسم الاستشارات - معلومات البناء
 * Consultation Section - Construction Information
 */
@RestController
@RequestMapping("/v1/consultation")
@RequiredArgsConstructor
@Tag(name = "Consultation", description = "استشارات ومعلومات البناء")
public class ConsultationController {

    private final ConsultationService consultationService;

    // ==================== الأقسام ====================

    @GetMapping("/categories")
    @Operation(
            summary = "جلب جميع أقسام الاستشارات",
            description = "يعرض قائمة بجميع أقسام الاستشارات المتاحة مع عدد العناصر في كل قسم"
    )
    public ResponseEntity<ApiResponse<List<ConsultationCategoryResponse>>> getAllCategories() {
        List<ConsultationCategoryResponse> categories = consultationService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/categories/{code}")
    @Operation(
            summary = "جلب قسم بالكود",
            description = "يعرض تفاصيل قسم استشارات محدد"
    )
    public ResponseEntity<ApiResponse<ConsultationCategoryResponse>> getCategoryByCode(
            @PathVariable String code) {
        ConsultationCategoryResponse category = consultationService.getCategoryByCode(code);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    // ==================== العناصر ====================

    @GetMapping("/categories/{categoryCode}/items")
    @Operation(
            summary = "جلب عناصر قسم",
            description = "يعرض جميع العناصر في قسم محدد (مثل: أنواع الطابوق، أنواع الحديد)"
    )
    public ResponseEntity<ApiResponse<List<ConsultationItemResponse>>> getItemsByCategory(
            @PathVariable String categoryCode) {
        List<ConsultationItemResponse> items = consultationService.getItemsByCategory(categoryCode);
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @GetMapping("/categories/{categoryCode}/items/{itemCode}")
    @Operation(
            summary = "جلب عنصر بالكود",
            description = "يعرض تفاصيل عنصر محدد مع زيادة عداد المشاهدات"
    )
    public ResponseEntity<ApiResponse<ConsultationItemResponse>> getItemByCode(
            @PathVariable String categoryCode,
            @PathVariable String itemCode) {
        ConsultationItemResponse item = consultationService.getItemByCode(categoryCode, itemCode);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    @GetMapping("/items/{id}")
    @Operation(
            summary = "جلب عنصر بالمعرف",
            description = "يعرض تفاصيل عنصر بالـ UUID"
    )
    public ResponseEntity<ApiResponse<ConsultationItemResponse>> getItemById(
            @PathVariable UUID id) {
        ConsultationItemResponse item = consultationService.getItemById(id);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    // ==================== العناصر المميزة ====================

    @GetMapping("/featured")
    @Operation(
            summary = "العناصر المميزة",
            description = "يعرض العناصر المميزة من جميع الأقسام"
    )
    public ResponseEntity<ApiResponse<List<ConsultationItemResponse>>> getFeaturedItems() {
        List<ConsultationItemResponse> items = consultationService.getFeaturedItems();
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @GetMapping("/most-viewed")
    @Operation(
            summary = "الأكثر مشاهدة",
            description = "يعرض العناصر الأكثر مشاهدة"
    )
    public ResponseEntity<ApiResponse<List<ConsultationItemResponse>>> getMostViewedItems(
            @RequestParam(defaultValue = "10") int limit) {
        List<ConsultationItemResponse> items = consultationService.getMostViewedItems(limit);
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @GetMapping("/top-rated")
    @Operation(
            summary = "الأعلى تقييماً",
            description = "يعرض العناصر الأعلى تقييماً"
    )
    public ResponseEntity<ApiResponse<List<ConsultationItemResponse>>> getTopRatedItems(
            @RequestParam(defaultValue = "10") int limit) {
        List<ConsultationItemResponse> items = consultationService.getTopRatedItems(limit);
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    // ==================== البحث ====================

    @GetMapping("/search")
    @Operation(
            summary = "البحث في الاستشارات",
            description = "البحث في جميع عناصر الاستشارات بالاسم أو الوصف"
    )
    public ResponseEntity<ApiResponse<PageResponse<ConsultationItemResponse>>> searchItems(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ConsultationItemResponse> items = consultationService.searchItems(q, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(items)));
    }

    // ==================== المختصين (الاستشارات) ====================

    @GetMapping("/specialists")
    @Operation(
            summary = "جلب جميع المختصين للاستشارات",
            description = "يعرض قائمة المختصين (مهندسين/مقاولين/مصممين/خلف) مع البحث والفلترة والـ pagination"
    )
    public ResponseEntity<ApiResponse<PageResponse<ConsultationSpecialistResponse>>> getSpecialists(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) Boolean availableNow,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<ConsultationSpecialistResponse> result = consultationService.getSpecialists(
                category,
                q,
                city,
                area,
                availableNow,
                PageRequest.of(page, size)
        );
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(result)));
    }

    // ==================== اختصارات للأقسام الشائعة ====================

    @GetMapping("/brick-types")
    @Operation(summary = "أنواع الطابوق", description = "جلب جميع أنواع الطابوق")
    public ResponseEntity<ApiResponse<List<ConsultationItemResponse>>> getBrickTypes() {
        return ResponseEntity.ok(ApiResponse.success(consultationService.getItemsByCategory("brick_types")));
    }

    @GetMapping("/steel-types")
    @Operation(summary = "أنواع الحديد", description = "جلب جميع أنواع الحديد")
    public ResponseEntity<ApiResponse<List<ConsultationItemResponse>>> getSteelTypes() {
        return ResponseEntity.ok(ApiResponse.success(consultationService.getItemsByCategory("steel_types")));
    }

    @GetMapping("/concrete-types")
    @Operation(summary = "أنواع الصب", description = "جلب جميع أنواع الصب")
    public ResponseEntity<ApiResponse<List<ConsultationItemResponse>>> getConcreteTypes() {
        return ResponseEntity.ok(ApiResponse.success(consultationService.getItemsByCategory("concrete_types")));
    }

    @GetMapping("/slab-types")
    @Operation(summary = "أنواع السقف", description = "جلب جميع أنواع السقف")
    public ResponseEntity<ApiResponse<List<ConsultationItemResponse>>> getSlabTypes() {
        return ResponseEntity.ok(ApiResponse.success(consultationService.getItemsByCategory("slab_types")));
    }

    @GetMapping("/soil-types")
    @Operation(summary = "أنواع التربة", description = "جلب جميع أنواع التربة")
    public ResponseEntity<ApiResponse<List<ConsultationItemResponse>>> getSoilTypes() {
        return ResponseEntity.ok(ApiResponse.success(consultationService.getItemsByCategory("soil_types")));
    }

    @GetMapping("/plumbing-types")
    @Operation(summary = "أنواع المجاري والسباكة", description = "جلب جميع أنواع أنابيب الماء والصرف")
    public ResponseEntity<ApiResponse<List<ConsultationItemResponse>>> getPlumbingTypes() {
        return ResponseEntity.ok(ApiResponse.success(consultationService.getItemsByCategory("plumbing_types")));
    }

    @GetMapping("/pre-concrete")
    @Operation(summary = "نصائح ما قبل الصب", description = "جلب نصائح ما قبل صب الخرسانة")
    public ResponseEntity<ApiResponse<List<ConsultationItemResponse>>> getPreConcreteAdvice() {
        return ResponseEntity.ok(ApiResponse.success(consultationService.getItemsByCategory("pre_concrete")));
    }

    @GetMapping("/roof-types")
    @Operation(summary = "أنواع السقف", description = "جلب جميع أنواع السقف")
    public ResponseEntity<ApiResponse<List<ConsultationItemResponse>>> getRoofTypes() {
        return ResponseEntity.ok(ApiResponse.success(consultationService.getItemsByCategory("roof_types")));
    }

    @GetMapping("/electrical-types")
    @Operation(summary = "أنواع الكهرباء", description = "جلب جميع أنواع التمديدات الكهربائية")
    public ResponseEntity<ApiResponse<List<ConsultationItemResponse>>> getElectricalTypes() {
        return ResponseEntity.ok(ApiResponse.success(consultationService.getItemsByCategory("electrical_types")));
    }

    @GetMapping("/insulation-types")
    @Operation(summary = "أنواع العزل", description = "جلب جميع أنواع العزل الحراري والمائي")
    public ResponseEntity<ApiResponse<List<ConsultationItemResponse>>> getInsulationTypes() {
        return ResponseEntity.ok(ApiResponse.success(consultationService.getItemsByCategory("insulation_types")));
    }
}
