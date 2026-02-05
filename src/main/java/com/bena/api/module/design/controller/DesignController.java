package com.bena.api.module.design.controller;

import com.bena.api.module.design.dto.CreateDesignRequest;
import com.bena.api.module.design.dto.DesignDTO;
import com.bena.api.module.design.entity.DesignCategory;
import com.bena.api.module.design.entity.DesignStyle;
import com.bena.api.module.design.service.DesignService;
import com.bena.api.module.design.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API للتصاميم - للمستخدمين العاديين
 */
@RestController
@RequestMapping("/v1/designs")
@RequiredArgsConstructor
public class DesignController {

    private static final Logger log = LoggerFactory.getLogger(DesignController.class);
    private final DesignService designService;
    private final ImageStorageService imageStorageService;

    /**
     * الحصول على التصاميم حسب الفئة مع الفلاتر
     */
    @GetMapping
    public ResponseEntity<Page<DesignDTO>> getDesigns(
            @RequestParam DesignCategory category,
            @RequestParam(required = false) DesignStyle style,
            @RequestParam(required = false) Integer minArea,
            @RequestParam(required = false) Integer maxArea,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("جلب التصاميم - الفئة: {}, النمط: {}", category, style);
        try {
            Page<DesignDTO> designs = designService.getDesigns(
                    category, style, minArea, maxArea, pageable
            );
            log.info("تم جلب {} تصميم", designs.getTotalElements());
            return ResponseEntity.ok(designs);
        } catch (Exception e) {
            log.error("خطأ في جلب التصاميم: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * الحصول على تصميم واحد بالتفاصيل
     */
    @GetMapping("/{id}")
    public ResponseEntity<DesignDTO> getDesignById(@PathVariable Long id) {
        DesignDTO design = designService.getDesignById(id);
        return ResponseEntity.ok(design);
    }

    /**
     * التصاميم المميزة
     */
    @GetMapping("/featured")
    public ResponseEntity<List<DesignDTO>> getFeaturedDesigns() {
        List<DesignDTO> designs = designService.getFeaturedDesigns();
        return ResponseEntity.ok(designs);
    }

    /**
     * الأكثر مشاهدة
     */
    @GetMapping("/popular")
    public ResponseEntity<List<DesignDTO>> getMostViewedDesigns() {
        List<DesignDTO> designs = designService.getMostViewedDesigns();
        return ResponseEntity.ok(designs);
    }

    /**
     * عدد التصاميم حسب الفئة
     */
    @GetMapping("/count/{category}")
    public ResponseEntity<Long> getDesignCount(@PathVariable DesignCategory category) {
        long count = designService.getDesignCountByCategory(category);
        return ResponseEntity.ok(count);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DesignDTO> createDesign(
            @RequestPart("image") MultipartFile image,
            @RequestPart("title") String title,
            @RequestPart("category") String category,
            @RequestPart("style") String style,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "areaInSquareMeters", required = false) String areaInSquareMetersStr,
            @RequestPart(value = "estimatedCost", required = false) String estimatedCostStr,
            @RequestPart(value = "isFeatured", required = false) String isFeaturedStr
    ) throws IOException {
        byte[] imageData = imageStorageService.saveImage(image);
        String imageType = imageStorageService.getImageType(imageData);

        Integer areaInSquareMeters = areaInSquareMetersStr != null ? Integer.parseInt(areaInSquareMetersStr) : null;
        Double estimatedCost = estimatedCostStr != null ? Double.parseDouble(estimatedCostStr) : null;
        Boolean isFeatured = isFeaturedStr != null ? Boolean.parseBoolean(isFeaturedStr) : false;

        CreateDesignRequest request = new CreateDesignRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setCategory(DesignCategory.valueOf(category.toUpperCase()));
        request.setStyle(DesignStyle.valueOf(style.toUpperCase()));
        request.setAreaInSquareMeters(areaInSquareMeters);
        request.setEstimatedCost(estimatedCost);
        request.setIsFeatured(isFeatured);
        request.setImageData(imageData);
        request.setImageType(imageType);

        DesignDTO design = designService.createDesign(request);
        return ResponseEntity.ok(design);
    }

}
