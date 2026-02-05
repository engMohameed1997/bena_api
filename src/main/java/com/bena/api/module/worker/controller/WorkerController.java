package com.bena.api.module.worker.controller;

import com.bena.api.module.worker.dto.*;
import com.bena.api.module.worker.entity.WorkerCategory;
import com.bena.api.module.worker.service.WorkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controller للمستخدمين - عرض العمال والتقييم
 */
@RestController
@RequestMapping("/v1/workers")
@RequiredArgsConstructor
@Tag(name = "Workers", description = "إدارة العمال والخلف")
public class WorkerController {

    private final WorkerService workerService;

    @GetMapping
    @Operation(summary = "جلب جميع العمال مع الفلترة")
    public ResponseEntity<Page<WorkerDTO>> getWorkers(
            @RequestParam(required = false) WorkerCategory category,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String location,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(workerService.getWorkers(category, minRating, location, pageable));
    }

    @GetMapping("/advanced-search")
    @Operation(summary = "البحث المتقدم عن عمال")
    public ResponseEntity<?> advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) WorkerCategory category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) BigDecimal maxPricePerMeter,
            @RequestParam(required = false) BigDecimal maxPricePerDay,
            @RequestParam(required = false) Boolean featuredOnly,
            @RequestParam(required = false) Boolean worksAtNight,
            @RequestParam(required = false) Integer maxCompletionDays,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double distanceKm,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        try {
            Page<WorkerDTO> results = workerService.advancedSearch(
                    name, category, city, area, minRating,
                    maxPricePerMeter, maxPricePerDay, featuredOnly,
                    worksAtNight, maxCompletionDays,
                    latitude, longitude, distanceKm, pageable
            );
            return ResponseEntity.ok(Map.of("success", true, "data", results));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/nearby")
    @Operation(summary = "جلب العمال القريبين")
    public ResponseEntity<?> getNearbyWorkers(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10") Double distanceKm,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        try {
            Page<WorkerDTO> results = workerService.getNearbyWorkers(latitude, longitude, distanceKm, pageable);
            return ResponseEntity.ok(Map.of("success", true, "data", results));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "جلب عامل واحد")
    public ResponseEntity<WorkerDTO> getWorker(@PathVariable Long id) {
        return ResponseEntity.ok(workerService.getWorkerById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "البحث عن عمال")
    public ResponseEntity<Page<WorkerDTO>> searchWorkers(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(workerService.searchWorkers(query, pageable));
    }

    @GetMapping("/featured")
    @Operation(summary = "جلب العمال المميزين")
    public ResponseEntity<List<WorkerDTO>> getFeaturedWorkers() {
        return ResponseEntity.ok(workerService.getFeaturedWorkers());
    }

    @GetMapping("/categories")
    @Operation(summary = "جلب الفئات مع عدد العمال")
    public ResponseEntity<List<CategoryCountDTO>> getCategories() {
        return ResponseEntity.ok(workerService.getCategoriesWithCount());
    }

    @GetMapping("/{id}/reviews")
    @Operation(summary = "جلب تقييمات عامل")
    public ResponseEntity<Page<WorkerReviewDTO>> getWorkerReviews(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(workerService.getWorkerReviews(id, pageable));
    }

    @PostMapping("/{id}/reviews")
    @Operation(summary = "إضافة تقييم لعامل")
    public ResponseEntity<WorkerReviewDTO> addReview(
            @PathVariable Long id,
            @RequestBody WorkerReviewCreateDTO dto
    ) {
        return ResponseEntity.ok(workerService.addReview(id, dto));
    }
}
