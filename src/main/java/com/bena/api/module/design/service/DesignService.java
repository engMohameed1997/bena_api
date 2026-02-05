package com.bena.api.module.design.service;

import com.bena.api.common.service.FileStorageService;
import com.bena.api.module.design.dto.CreateDesignRequest;
import com.bena.api.module.design.dto.DesignDTO;
import com.bena.api.module.design.entity.Design;
import com.bena.api.module.design.entity.DesignCategory;
import com.bena.api.module.design.entity.DesignStyle;
import com.bena.api.module.design.repository.DesignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DesignService {

    private final DesignRepository designRepository;
    private final FileStorageService fileStorageService;

    // الحصول على التصاميم حسب الفئة مع الفلاتر
    @Transactional(readOnly = true)
    public Page<DesignDTO> getDesigns(
            DesignCategory category,
            DesignStyle style,
            Integer minArea,
            Integer maxArea,
            Pageable pageable
    ) {
        Page<Design> designs = designRepository.findByFilters(
                category, style, minArea, maxArea, pageable
        );
        return designs.map(this::toDTO);
    }

    // الحصول على تصميم واحد
    @Transactional(readOnly = false)
    public DesignDTO getDesignById(Long id) {
        Design design = designRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("التصميم غير موجود"));
        
        // زيادة عدد المشاهدات
        design.setViewCount(design.getViewCount() + 1);
        designRepository.save(design);
        
        return toDTO(design);
    }

    // إنشاء تصميم جديد (للأدمن)
    @Transactional
    public DesignDTO createDesign(CreateDesignRequest request) {
        Design design = Design.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .imageData(request.getImageData())
                .imageType(request.getImageType())
                .category(request.getCategory())
                .style(request.getStyle())
                .areaInSquareMeters(request.getAreaInSquareMeters())
                .estimatedCost(request.getEstimatedCost())
                .materials(request.getMaterials())
                .features(request.getFeatures())
                .isFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
                .isActive(true)
                .viewCount(0)
                .build();

        Design saved = designRepository.save(design);
        return toDTO(saved);
    }

    // تحديث تصميم (للأدمن)
    @Transactional
    public DesignDTO updateDesign(Long id, CreateDesignRequest request) {
        Design design = designRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("التصميم غير موجود"));

        design.setTitle(request.getTitle());
        design.setDescription(request.getDescription());
        design.setImageUrl(request.getImageUrl());
        if (request.getImageData() != null && request.getImageData().length > 0) {
            design.setImageData(request.getImageData());
            design.setImageType(request.getImageType());
            design.setImageUrl(null);
        }
        design.setCategory(request.getCategory());
        design.setStyle(request.getStyle());
        design.setAreaInSquareMeters(request.getAreaInSquareMeters());
        design.setEstimatedCost(request.getEstimatedCost());
        design.setMaterials(request.getMaterials());
        design.setFeatures(request.getFeatures());
        if (request.getIsFeatured() != null) {
            design.setIsFeatured(request.getIsFeatured());
        }

        Design saved = designRepository.save(design);
        return toDTO(saved);
    }

    // حذف تصميم (soft delete)
    @Transactional
    public void deleteDesign(Long id) {
        Design design = designRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("التصميم غير موجود"));
        
        // حذف الصورة من filesystem
        if (design.getImageUrl() != null) {
            fileStorageService.delete(design.getImageUrl());
        }
        
        design.setIsActive(false);
        designRepository.save(design);
    }

    // التصاميم المميزة
    @Transactional(readOnly = true)
    public List<DesignDTO> getFeaturedDesigns() {
        return designRepository.findByIsFeaturedTrueAndIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // الأكثر مشاهدة
    @Transactional(readOnly = true)
    public List<DesignDTO> getMostViewedDesigns() {
        return designRepository.findTop10ByIsActiveTrueOrderByViewCountDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // إحصائيات
    public long getDesignCountByCategory(DesignCategory category) {
        return designRepository.countByCategoryAndIsActiveTrue(category);
    }

    private DesignDTO toDTO(Design design) {
        // دعم الصور القديمة (byte[]) والجديدة (URL)
        String imageUrl = design.getImageUrl();
        
        // إذا لم يكن هناك URL، حول byte[] إلى Base64 Data URL
        if (imageUrl == null && design.getImageData() != null && design.getImageData().length > 0) {
            String contentType = design.getImageType() != null ? design.getImageType() : "image/jpeg";
            imageUrl = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(design.getImageData());
        } else if (imageUrl == null) {
            imageUrl = "";
        }
        
        return DesignDTO.builder()
                .id(design.getId())
                .title(design.getTitle())
                .description(design.getDescription())
                .imageUrl(imageUrl)
                .category(design.getCategory())
                .style(design.getStyle())
                .areaInSquareMeters(design.getAreaInSquareMeters())
                .estimatedCost(design.getEstimatedCost())
                .materials(null)  // لا نرجع materials في list view
                .features(null)   // لا نرجع features في list view
                .viewCount(design.getViewCount())
                .isFeatured(design.getIsFeatured())
                .isActive(design.getIsActive())
                .createdAt(design.getCreatedAt())
                .build();
    }

}
