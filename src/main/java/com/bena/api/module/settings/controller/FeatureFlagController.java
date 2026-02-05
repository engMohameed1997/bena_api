package com.bena.api.module.settings.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.settings.dto.FeatureFlagDto;
import com.bena.api.module.settings.entity.FeatureFlag;
import com.bena.api.module.settings.service.FeatureFlagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller لإدارة Feature Flags (Admin Only)
 */
@RestController
@RequestMapping("/v1/admin/feature-flags")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Feature Flags", description = "إدارة ميزات النظام - للأدمن فقط")
public class FeatureFlagController {

    private final FeatureFlagService featureFlagService;

    @GetMapping
    @Operation(summary = "جلب جميع Feature Flags")
    public ResponseEntity<ApiResponse<List<FeatureFlagDto>>> getAllFlags() {
        List<FeatureFlag> flags = featureFlagService.getAllFlags();
        List<FeatureFlagDto> dtos = flags.stream().map(FeatureFlagDto::from).toList();
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/enabled")
    @Operation(summary = "جلب الميزات المُفعّلة فقط")
    public ResponseEntity<ApiResponse<List<FeatureFlagDto>>> getEnabledFlags() {
        List<FeatureFlag> flags = featureFlagService.getEnabledFlags();
        List<FeatureFlagDto> dtos = flags.stream().map(FeatureFlagDto::from).toList();
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/categories")
    @Operation(summary = "جلب قائمة الفئات")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(featureFlagService.getAllCategories()));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "جلب الميزات حسب الفئة")
    public ResponseEntity<ApiResponse<List<FeatureFlagDto>>> getFlagsByCategory(@PathVariable String category) {
        List<FeatureFlag> flags = featureFlagService.getFlagsByCategory(category);
        List<FeatureFlagDto> dtos = flags.stream().map(FeatureFlagDto::from).toList();
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/{featureKey}")
    @Operation(summary = "جلب feature flag محدد")
    public ResponseEntity<ApiResponse<FeatureFlagDto>> getFlag(@PathVariable String featureKey) {
        return featureFlagService.getByKey(featureKey)
                .map(flag -> ResponseEntity.ok(ApiResponse.success(FeatureFlagDto.from(flag))))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{featureKey}/check")
    @Operation(summary = "التحقق من تفعيل ميزة")
    public ResponseEntity<ApiResponse<Boolean>> checkFlag(@PathVariable String featureKey) {
        boolean enabled = featureFlagService.isEnabled(featureKey);
        return ResponseEntity.ok(ApiResponse.success(enabled));
    }

    @PostMapping
    @Operation(summary = "إنشاء feature flag جديد")
    public ResponseEntity<ApiResponse<FeatureFlagDto>> createFlag(@Valid @RequestBody FeatureFlagDto dto) {
        FeatureFlag flag = featureFlagService.create(
            dto.getFeatureKey(),
            dto.getName(),
            dto.getDescription(),
            dto.getCategory()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(FeatureFlagDto.from(flag), "تم إنشاء الميزة بنجاح"));
    }

    @PatchMapping("/{featureKey}/toggle")
    @Operation(summary = "تفعيل/إيقاف ميزة")
    public ResponseEntity<ApiResponse<FeatureFlagDto>> toggleFlag(@PathVariable String featureKey) {
        FeatureFlag flag = featureFlagService.toggle(featureKey);
        String message = flag.getIsEnabled() ? "تم تفعيل الميزة" : "تم إيقاف الميزة";
        return ResponseEntity.ok(ApiResponse.success(FeatureFlagDto.from(flag), message));
    }

    @PatchMapping("/{featureKey}/rollout")
    @Operation(summary = "تحديث نسبة الـ rollout")
    public ResponseEntity<ApiResponse<FeatureFlagDto>> updateRollout(
            @PathVariable String featureKey,
            @RequestParam Integer percentage
    ) {
        FeatureFlag flag = featureFlagService.updateRolloutPercentage(featureKey, percentage);
        return ResponseEntity.ok(ApiResponse.success(FeatureFlagDto.from(flag), "تم تحديث نسبة التفعيل"));
    }

    @DeleteMapping("/{featureKey}")
    @Operation(summary = "حذف feature flag")
    public ResponseEntity<ApiResponse<Void>> deleteFlag(@PathVariable String featureKey) {
        featureFlagService.delete(featureKey);
        return ResponseEntity.ok(ApiResponse.success(null, "تم حذف الميزة بنجاح"));
    }
}
