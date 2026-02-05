package com.bena.api.module.buildingsteps.service;

import com.bena.api.common.service.FileUploadService;
import com.bena.api.module.buildingsteps.dto.*;
import com.bena.api.module.buildingsteps.entity.*;
import com.bena.api.module.buildingsteps.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Slf4j
public class BuildingStepsService {
    
    @Autowired
    private BuildingStepRepository buildingStepRepository;
    
    @Autowired
    private StepCategoryRepository stepCategoryRepository;
    
    @Autowired
    private StepMediaRepository stepMediaRepository;
    
    @Autowired
    private SubStepRepository subStepRepository;

    @Autowired
    private FileUploadService fileUploadService;
    
    // ==================== Categories ====================
    
    public List<StepCategoryDto> getAllCategories() {
        return stepCategoryRepository.findByIsActiveTrueOrderByCategoryOrderAsc()
                .stream()
                .map(this::mapCategoryToDto)
                .collect(Collectors.toList());
    }
    
    public StepCategoryDto getCategoryById(Long id) {
        return stepCategoryRepository.findById(id)
                .map(this::mapCategoryToDto)
                .orElse(null);
    }
    
    @Transactional
    public StepCategoryDto createCategory(StepCategoryDto dto) {
        StepCategory category = new StepCategory();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setIconName(dto.getIconName());
        category.setColorHex(dto.getColorHex());
        category.setCategoryOrder(dto.getCategoryOrder());
        category.setIsActive(true);
        
        return mapCategoryToDto(stepCategoryRepository.save(category));
    }
    
    @Transactional
    public StepCategoryDto updateCategory(Long id, StepCategoryDto dto) {
        StepCategory category = stepCategoryRepository.findById(id).orElse(null);
        if (category == null) return null;
        
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setIconName(dto.getIconName());
        category.setColorHex(dto.getColorHex());
        category.setCategoryOrder(dto.getCategoryOrder());
        
        return mapCategoryToDto(stepCategoryRepository.save(category));
    }
    
    @Transactional
    public void deleteCategory(Long id) {
        stepCategoryRepository.findById(id).ifPresent(category -> {
            category.setIsActive(false);
            stepCategoryRepository.save(category);
        });
    }
    
    // ==================== Building Steps ====================
    
    public List<BuildingStepDto> getAllSteps() {
        return buildingStepRepository.findByIsActiveTrueOrderByStepOrderAsc()
                .stream()
                .map(this::mapStepToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * جلب عدد الخطوات الإجمالي
     */
    public long getTotalStepsCount() {
        return buildingStepRepository.countByIsActiveTrue();
    }
    
    public List<BuildingStepDto> getStepsByCategory(Long categoryId) {
        return buildingStepRepository.findByCategoryIdAndIsActiveTrueOrderByStepOrderAsc(categoryId)
                .stream()
                .map(this::mapStepToDto)
                .collect(Collectors.toList());
    }
    
    public BuildingStepDto getStepById(Long id) {
        return buildingStepRepository.findById(id)
                .map(this::mapStepToDto)
                .orElse(null);
    }
    
    @Transactional(readOnly = true)
    public BuildingStepDto getStepWithDetails(Long id) {
        try {
            // استخدام query يجلب كل البيانات مرة واحدة لتجنب LazyInitializationException
            BuildingStep step = buildingStepRepository.findByIdWithAllDetails(id);
            if (step == null) {
                log.warn("Building step not found with id: {}", id);
                return null;
            }
            
            BuildingStepDto dto = mapStepToDto(step);
            
            // تحويل الوسائط
            if (step.getMediaList() != null && !step.getMediaList().isEmpty()) {
                dto.setMediaList(step.getMediaList().stream()
                        .map(this::mapMediaToDto)
                        .collect(Collectors.toList()));
            }
            
            // تحويل الخطوات الفرعية النشطة فقط
            if (step.getSubSteps() != null && !step.getSubSteps().isEmpty()) {
                dto.setSubSteps(step.getSubSteps().stream()
                        .filter(s -> s.getIsActive() != null && s.getIsActive())
                        .map(this::mapSubStepToDto)
                        .collect(Collectors.toList()));
            }
            
            log.debug("Successfully fetched building step details for id: {}", id);
            return dto;
        } catch (Exception e) {
            log.error("Error fetching building step details for id: {}", id, e);
            throw new RuntimeException("فشل جلب تفاصيل الخطوة: " + e.getMessage(), e);
        }
    }

    public Page<BuildingStepDto> getSteps(Pageable pageable, String search, Long categoryId) {
        Page<BuildingStep> page;
        
        // Use simple query when no filters for better performance
        if ((search == null || search.isEmpty()) && categoryId == null) {
            page = buildingStepRepository.findByIsActiveTrue(pageable);
        } else {
            // Use empty string instead of null for search to work with COALESCE
            String searchParam = (search == null || search.isEmpty()) ? "" : search;
            Long categoryParam = (categoryId == null) ? 0L : categoryId;
            page = buildingStepRepository.findAllWithFilters(searchParam, categoryParam, pageable);
        }
        
        return page.map(this::mapStepToDto);
    }
    
    @Transactional
    public BuildingStepDto createStep(BuildingStepDto dto) {
        BuildingStep step = new BuildingStep();
        step.setTitle(dto.getTitle());
        step.setDescription(dto.getDescription());
        step.setStepOrder(dto.getStepOrder());
        step.setCategoryId(dto.getCategoryId());
        step.setIconName(dto.getIconName());
        step.setEstimatedDuration(dto.getEstimatedDuration());
        step.setEstimatedCostPercentage(dto.getEstimatedCostPercentage());
        step.setIsActive(true);
        
        return mapStepToDto(buildingStepRepository.save(step));
    }
    
    @Transactional
    public BuildingStepDto updateStep(Long id, BuildingStepDto dto) {
        BuildingStep step = buildingStepRepository.findById(id).orElse(null);
        if (step == null) return null;
        
        step.setTitle(dto.getTitle());
        step.setDescription(dto.getDescription());
        step.setStepOrder(dto.getStepOrder());
        step.setCategoryId(dto.getCategoryId());
        step.setIconName(dto.getIconName());
        step.setEstimatedDuration(dto.getEstimatedDuration());
        step.setEstimatedCostPercentage(dto.getEstimatedCostPercentage());
        
        return mapStepToDto(buildingStepRepository.save(step));
    }
    
    @Transactional
    public void deleteStep(Long id) {
        buildingStepRepository.findById(id).ifPresent(step -> {
            step.setIsActive(false);
            buildingStepRepository.save(step);
        });
    }
    
    // ==================== Sub Steps ====================
    
    @Transactional
    public SubStepDto createSubStep(Long stepId, SubStepDto dto) {
        BuildingStep step = buildingStepRepository.findById(stepId).orElse(null);
        if (step == null) return null;
        
        SubStep subStep = new SubStep();
        subStep.setBuildingStep(step);
        subStep.setTitle(dto.getTitle());
        subStep.setDescription(dto.getDescription());
        subStep.setSubStepOrder(dto.getSubStepOrder());
        subStep.setTips(dto.getTips());
        subStep.setWarnings(dto.getWarnings());
        subStep.setIsActive(true);
        
        return mapSubStepToDto(subStepRepository.save(subStep));
    }
    
    @Transactional
    public SubStepDto updateSubStep(Long id, SubStepDto dto) {
        SubStep subStep = subStepRepository.findById(id).orElse(null);
        if (subStep == null) return null;
        
        subStep.setTitle(dto.getTitle());
        subStep.setDescription(dto.getDescription());
        subStep.setSubStepOrder(dto.getSubStepOrder());
        subStep.setTips(dto.getTips());
        subStep.setWarnings(dto.getWarnings());
        
        return mapSubStepToDto(subStepRepository.save(subStep));
    }
    
    @Transactional
    public void deleteSubStep(Long id) {
        subStepRepository.findById(id).ifPresent(subStep -> {
            subStep.setIsActive(false);
            subStepRepository.save(subStep);
        });
    }
    
    // ==================== Media Upload ====================
    
    @Transactional
    public StepMediaDto uploadMedia(Long stepId, Long subStepId, MultipartFile file, 
                                     String mediaType, String title, String caption) throws IOException {
        BuildingStep step = buildingStepRepository.findById(stepId).orElse(null);
        if (step == null) return null;
        
        SubStep subStep = null;
        if (subStepId != null) {
            subStep = subStepRepository.findById(subStepId).orElse(null);
        }

        String folder = "building-steps";
        String relativePath;

        if (mediaType != null && mediaType.equalsIgnoreCase("VIDEO")) {
            relativePath = fileUploadService.uploadVideo(file, folder);
        } else if (mediaType != null && mediaType.equalsIgnoreCase("IMAGE")) {
            relativePath = fileUploadService.uploadImage(file, folder);
        } else {
            relativePath = fileUploadService.uploadFile(file, folder);
        }

        String url = fileUploadService.getFileUrl(relativePath);
        
        // Create media record
        StepMedia media = new StepMedia();
        media.setBuildingStep(step);
        media.setSubStep(subStep);
        media.setMediaType(mediaType != null ? mediaType.toUpperCase() : "FILE");
        media.setUrl(url);
        media.setTitle(title);
        media.setCaption(caption);
        media.setMediaOrder(stepMediaRepository.findByBuildingStepIdOrderByMediaOrderAsc(stepId).size() + 1);

        if (mediaType != null && mediaType.equalsIgnoreCase("VIDEO")) {
            media.setThumbnailUrl(null);
        }
        
        return mapMediaToDto(stepMediaRepository.save(media));
    }
    
    @Transactional
    public void deleteMedia(Long mediaId) {
        stepMediaRepository.findById(mediaId).ifPresent(media -> {
            try {
                fileUploadService.deleteFile(media.getUrl());
            } catch (Exception e) {
                log.warn("Failed to delete media file: {}", media.getUrl(), e);
            }
            stepMediaRepository.delete(media);
        });
    }
    
    // ==================== Mappers ====================
    
    private StepCategoryDto mapCategoryToDto(StepCategory category) {
        StepCategoryDto dto = new StepCategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setIconName(category.getIconName());
        dto.setColorHex(category.getColorHex());
        dto.setCategoryOrder(category.getCategoryOrder());
        dto.setStepsCount(buildingStepRepository.findByCategoryIdAndIsActiveTrueOrderByStepOrderAsc(category.getId()).size());
        return dto;
    }
    
    private BuildingStepDto mapStepToDto(BuildingStep step) {
        BuildingStepDto dto = new BuildingStepDto();
        dto.setId(step.getId());
        dto.setTitle(step.getTitle());
        dto.setDescription(step.getDescription());
        dto.setStepOrder(step.getStepOrder());
        dto.setCategoryId(step.getCategoryId());
        dto.setIconName(step.getIconName());
        dto.setEstimatedDuration(step.getEstimatedDuration());
        dto.setEstimatedCostPercentage(step.getEstimatedCostPercentage());
        
        if (step.getCategoryId() != null) {
            stepCategoryRepository.findById(step.getCategoryId())
                    .ifPresent(cat -> dto.setCategoryName(cat.getName()));
        }
        
        return dto;
    }
    
    private SubStepDto mapSubStepToDto(SubStep subStep) {
        SubStepDto dto = new SubStepDto();
        dto.setId(subStep.getId());
        dto.setTitle(subStep.getTitle());
        dto.setDescription(subStep.getDescription());
        dto.setSubStepOrder(subStep.getSubStepOrder());
        dto.setTips(subStep.getTips());
        dto.setWarnings(subStep.getWarnings());
        dto.setMediaList(subStep.getMediaList().stream()
                .map(this::mapMediaToDto)
                .collect(Collectors.toList()));
        return dto;
    }
    
    private StepMediaDto mapMediaToDto(StepMedia media) {
        StepMediaDto dto = new StepMediaDto();
        dto.setId(media.getId());
        dto.setMediaType(media.getMediaType());
        dto.setUrl(media.getUrl());
        dto.setThumbnailUrl(media.getThumbnailUrl());
        dto.setTitle(media.getTitle());
        dto.setCaption(media.getCaption());
        dto.setMediaOrder(media.getMediaOrder());
        return dto;
    }
}
