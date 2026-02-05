package com.bena.api.module.design.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.design.dto.CreateDesignRequest;
import com.bena.api.module.design.dto.DesignDTO;
import com.bena.api.module.design.entity.DesignCategory;
import com.bena.api.module.design.service.DesignService;
import com.bena.api.module.design.service.ImageStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * API إدارة التصاميم - للأدمن فقط
 */
@RestController
@RequestMapping("/v1/admin/designs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class DesignAdminController {

    private final DesignService designService;
    private final ImageStorageService imageStorageService;

    /**
     * الحصول على جميع التصاميم (للأدمن)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<DesignDTO>>> getAllDesigns(
            @RequestParam(required = false) DesignCategory category,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<DesignDTO> designs = designService.getDesigns(
                category, null, null, null, pageable
        );
        return ResponseEntity.ok(ApiResponse.success(designs));
    }

    /**
     * إنشاء تصميم جديد مع رفع صورة
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DesignDTO>> createDesign(
            @RequestPart("image") MultipartFile image,
            @RequestPart("title") String title,
            @RequestPart("category") String category,
            @RequestPart("style") String style,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "areaInSquareMeters", required = false) String areaInSquareMetersStr,
            @RequestPart(value = "estimatedCost", required = false) String estimatedCostStr,
            @RequestPart(value = "isFeatured", required = false) String isFeaturedStr
    ) throws IOException {
        
        try {
            // حفظ الصورة
            byte[] imageData = imageStorageService.saveImage(image);
            String imageType = imageStorageService.getImageType(imageData);

            // تحويل القيم
            Integer areaInSquareMeters = areaInSquareMetersStr != null ? Integer.parseInt(areaInSquareMetersStr) : null;
            Double estimatedCost = estimatedCostStr != null ? Double.parseDouble(estimatedCostStr) : null;
            Boolean isFeatured = isFeaturedStr != null ? Boolean.parseBoolean(isFeaturedStr) : false;

            // إنشاء request
            CreateDesignRequest request = new CreateDesignRequest();
            request.setTitle(title);
            request.setDescription(description);
            request.setCategory(DesignCategory.valueOf(category.toUpperCase()));
            request.setStyle(com.bena.api.module.design.entity.DesignStyle.valueOf(style.toUpperCase()));
            request.setAreaInSquareMeters(areaInSquareMeters);
            request.setEstimatedCost(estimatedCost);
            request.setIsFeatured(isFeatured);
            request.setImageData(imageData);
            request.setImageType(imageType);

            DesignDTO design = designService.createDesign(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(design, "تم إضافة التصميم بنجاح"));
        } catch (Exception e) {
            log.error("خطأ في حفظ التصميم", e);
            throw e;
        }
    }

    /**
     * تحديث تصميم
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DesignDTO>> updateDesign(
            @PathVariable Long id,
            @Valid @RequestBody CreateDesignRequest request
    ) {
        DesignDTO design = designService.updateDesign(id, request);
        return ResponseEntity.ok(ApiResponse.success(design, "تم تحديث التصميم بنجاح"));
    }

    /**
     * حذف تصميم
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDesign(@PathVariable Long id) {
        designService.deleteDesign(id);
        return ResponseEntity.ok(ApiResponse.success(null, "تم حذف التصميم بنجاح"));
    }
}
