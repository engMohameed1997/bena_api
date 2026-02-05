package com.bena.api.module.buildingsteps.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.buildingsteps.dto.BuildingStepDto;
import com.bena.api.module.buildingsteps.service.BuildingStepsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin/building-steps")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class BuildingStepAdminController {

    private final BuildingStepsService buildingStepsService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BuildingStepDto>>> getAllSteps(
            @PageableDefault(sort = "stepOrder", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId) {
        
        Page<BuildingStepDto> steps = buildingStepsService.getSteps(pageable, q, categoryId);
        return ResponseEntity.ok(ApiResponse.success(steps));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BuildingStepDto>> getStepById(@PathVariable Long id) {
        BuildingStepDto step = buildingStepsService.getStepById(id);
        if (step == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("الخطوة غير موجودة"));
        }
        return ResponseEntity.ok(ApiResponse.success(step));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BuildingStepDto>> createStep(@RequestBody BuildingStepDto dto) {
        BuildingStepDto created = buildingStepsService.createStep(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "تم إنشاء الخطوة بنجاح"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BuildingStepDto>> updateStep(
            @PathVariable Long id,
            @RequestBody BuildingStepDto dto) {
        
        BuildingStepDto updated = buildingStepsService.updateStep(id, dto);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("الخطوة غير موجودة"));
        }
        return ResponseEntity.ok(ApiResponse.success(updated, "تم تحديث الخطوة بنجاح"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStep(@PathVariable Long id) {
        if (buildingStepsService.getStepById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("الخطوة غير موجودة"));
        }
        buildingStepsService.deleteStep(id);
        return ResponseEntity.ok(ApiResponse.success(null, "تم حذف الخطوة بنجاح"));
    }

    // ==================== Categories ====================

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<java.util.List<com.bena.api.module.buildingsteps.dto.StepCategoryDto>>> getAllCategories() {
        java.util.List<com.bena.api.module.buildingsteps.dto.StepCategoryDto> categories = buildingStepsService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<com.bena.api.module.buildingsteps.dto.StepCategoryDto>> createCategory(@RequestBody com.bena.api.module.buildingsteps.dto.StepCategoryDto dto) {
        com.bena.api.module.buildingsteps.dto.StepCategoryDto created = buildingStepsService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "تم إنشاء التصنيف بنجاح"));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<com.bena.api.module.buildingsteps.dto.StepCategoryDto>> updateCategory(@PathVariable Long id, @RequestBody com.bena.api.module.buildingsteps.dto.StepCategoryDto dto) {
        com.bena.api.module.buildingsteps.dto.StepCategoryDto updated = buildingStepsService.updateCategory(id, dto);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("التصنيف غير موجود"));
        }
        return ResponseEntity.ok(ApiResponse.success(updated, "تم تحديث التصنيف بنجاح"));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        if (buildingStepsService.getCategoryById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("التصنيف غير موجود"));
        }
        buildingStepsService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "تم حذف التصنيف بنجاح"));
    }

    // ==================== Sub Steps ====================

    @PostMapping("/{stepId}/sub-steps")
    public ResponseEntity<ApiResponse<com.bena.api.module.buildingsteps.dto.SubStepDto>> createSubStep(
            @PathVariable Long stepId, 
            @RequestBody com.bena.api.module.buildingsteps.dto.SubStepDto dto) {
        
        com.bena.api.module.buildingsteps.dto.SubStepDto created = buildingStepsService.createSubStep(stepId, dto);
        if (created == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("فشل إنشاء الخطوة الفرعية، تأكد من وجود الخطوة الرئيسية"));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "تم إنشاء الخطوة الفرعية بنجاح"));
    }

    @PutMapping("/sub-steps/{id}")
    public ResponseEntity<ApiResponse<com.bena.api.module.buildingsteps.dto.SubStepDto>> updateSubStep(
            @PathVariable Long id, 
            @RequestBody com.bena.api.module.buildingsteps.dto.SubStepDto dto) {
        
        com.bena.api.module.buildingsteps.dto.SubStepDto updated = buildingStepsService.updateSubStep(id, dto);
        if (updated == null) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("الخطوة الفرعية غير موجودة"));
        }
        return ResponseEntity.ok(ApiResponse.success(updated, "تم تحديث الخطوة الفرعية بنجاح"));
    }

    @DeleteMapping("/sub-steps/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubStep(@PathVariable Long id) {
        buildingStepsService.deleteSubStep(id); 
        return ResponseEntity.ok(ApiResponse.success(null, "تم حذف الخطوة الفرعية بنجاح"));
    }

    // ==================== Media Upload ====================

    @PostMapping(value = "/{stepId}/media", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<com.bena.api.module.buildingsteps.dto.StepMediaDto>> uploadMedia(
            @PathVariable Long stepId,
            @RequestParam(required = false) Long subStepId,
            @RequestPart("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam String mediaType,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String caption) {
        
        // MIME Type Validation
        String contentType = file.getContentType();
        if (contentType == null || 
            (!contentType.startsWith("image/") && 
             !contentType.startsWith("video/") && 
             !contentType.equals("application/pdf"))) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("نوع الملف غير مدعوم. يرجى رفع صور، فيديو، أو ملفات PDF فقط."));
        }

        try {
            com.bena.api.module.buildingsteps.dto.StepMediaDto media = buildingStepsService.uploadMedia(stepId, subStepId, file, mediaType, title, caption);
            if (media == null) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("الخطوة غير موجودة"));
            }
            return ResponseEntity.ok(ApiResponse.success(media, "تم رفع الملف بنجاح"));
        } catch (java.io.IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("فشل رفع الملف: " + e.getMessage()));
        }
    }

    @DeleteMapping("/media/{mediaId}")
    public ResponseEntity<ApiResponse<Void>> deleteMedia(@PathVariable Long mediaId) {
        buildingStepsService.deleteMedia(mediaId);
        return ResponseEntity.ok(ApiResponse.success(null, "تم حذف الملف بنجاح"));
    }
}
