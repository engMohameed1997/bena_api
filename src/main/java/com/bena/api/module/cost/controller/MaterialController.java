package com.bena.api.module.cost.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.common.dto.PageResponse;
import com.bena.api.module.cost.entity.Material;
import com.bena.api.module.cost.repository.MaterialRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * إدارة المواد والأسعار
 * Materials and Prices Management
 */
@RestController
@RequestMapping("/v1/materials")
@RequiredArgsConstructor
@Tag(name = "Materials", description = "إدارة المواد وأسعارها")
public class MaterialController {

    private final MaterialRepository materialRepository;

    @GetMapping
    @Operation(summary = "جلب جميع المواد", description = "يعرض قائمة المواد مع دعم الترقيم والبحث")
    public ResponseEntity<ApiResponse<PageResponse<Material>>> getAllMaterials(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("category", "nameAr"));
        
        Page<Material> materials;
        if (search != null && !search.isBlank()) {
            materials = materialRepository.searchMaterials(search.trim(), pageRequest);
        } else {
            materials = materialRepository.findAllActive(pageRequest);
        }
        
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(materials)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "جلب مادة بالمعرف", description = "يعرض تفاصيل مادة محددة")
    public ResponseEntity<ApiResponse<Material>> getMaterialById(@PathVariable UUID id) {
        return materialRepository.findById(id)
                .map(material -> ResponseEntity.ok(ApiResponse.success(material)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "جلب مادة بالكود", description = "يعرض تفاصيل مادة بناءً على كودها")
    public ResponseEntity<ApiResponse<Material>> getMaterialByCode(@PathVariable String code) {
        return materialRepository.findByCode(code)
                .map(material -> ResponseEntity.ok(ApiResponse.success(material)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "جلب المواد حسب الفئة", description = "يعرض قائمة المواد في فئة محددة")
    public ResponseEntity<ApiResponse<List<Material>>> getMaterialsByCategory(
            @PathVariable String category) {
        
        List<Material> materials = materialRepository.findActiveByCategoryOrderByNameAr(category);
        return ResponseEntity.ok(ApiResponse.success(materials));
    }

    @GetMapping("/categories")
    @Operation(summary = "جلب فئات المواد", description = "يعرض قائمة بجميع فئات المواد المتاحة")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() {
        List<String> categories = materialRepository.findAllActiveCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}
