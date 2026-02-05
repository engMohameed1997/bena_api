package com.bena.api.module.consultation.service;

import com.bena.api.common.exception.ResourceNotFoundException;
import com.bena.api.module.consultation.dto.ConsultationCategoryResponse;
import com.bena.api.module.consultation.dto.ConsultationItemResponse;
import com.bena.api.module.consultation.dto.ConsultationSpecialistResponse;
import com.bena.api.module.consultation.entity.ConsultationCategory;
import com.bena.api.module.consultation.entity.ConsultationItem;
import com.bena.api.module.consultation.repository.ConsultationCategoryRepository;
import com.bena.api.module.consultation.repository.ConsultationItemRepository;
import com.bena.api.module.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ConsultationService {

    private final ConsultationCategoryRepository categoryRepository;
    private final ConsultationItemRepository itemRepository;
    private final WorkerRepository workerRepository;

    // ==================== Categories ====================

    public List<ConsultationCategoryResponse> getAllCategories() {
        return categoryRepository.findAllActiveOrderByDisplayOrder()
                .stream()
                .map(cat -> {
                    long count = itemRepository.countByCategoryCode(cat.getCode());
                    return ConsultationCategoryResponse.from(cat, count);
                })
                .collect(Collectors.toList());
    }

    public ConsultationCategoryResponse getCategoryByCode(String code) {
        ConsultationCategory category = categoryRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("القسم غير موجود: " + code));
        long count = itemRepository.countByCategoryCode(code);
        return ConsultationCategoryResponse.from(category, count);
    }

    // ==================== Items ====================

    public List<ConsultationItemResponse> getItemsByCategory(String categoryCode) {
        return itemRepository.findByCategoryCodeOrderByDisplayOrder(categoryCode)
                .stream()
                .map(ConsultationItemResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ConsultationItemResponse getItemByCode(String categoryCode, String itemCode) {
        ConsultationItem item = itemRepository.findByCategoryCodeAndCode(categoryCode, itemCode)
                .orElseThrow(() -> new ResourceNotFoundException("العنصر غير موجود"));
        
        // زيادة عداد المشاهدات
        itemRepository.incrementViewCount(item.getId());
        
        return ConsultationItemResponse.from(item);
    }

    @Transactional
    public ConsultationItemResponse getItemById(UUID id) {
        ConsultationItem item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("العنصر غير موجود"));
        
        // زيادة عداد المشاهدات
        itemRepository.incrementViewCount(id);
        
        return ConsultationItemResponse.from(item);
    }

    public List<ConsultationItemResponse> getFeaturedItems() {
        return itemRepository.findFeaturedItems()
                .stream()
                .map(ConsultationItemResponse::from)
                .collect(Collectors.toList());
    }

    public List<ConsultationItemResponse> getMostViewedItems(int limit) {
        return itemRepository.findMostViewed(PageRequest.of(0, limit))
                .stream()
                .map(ConsultationItemResponse::from)
                .collect(Collectors.toList());
    }

    public List<ConsultationItemResponse> getTopRatedItems(int limit) {
        return itemRepository.findTopRated(PageRequest.of(0, limit))
                .stream()
                .map(ConsultationItemResponse::from)
                .collect(Collectors.toList());
    }

    public Page<ConsultationItemResponse> searchItems(String query, Pageable pageable) {
        return itemRepository.searchItems(query, pageable)
                .map(ConsultationItemResponse::from);
    }

    // ==================== Specialists ====================

    public Page<ConsultationSpecialistResponse> getSpecialists(
            String category,
            String q,
            String city,
            String area,
            Boolean availableNow,
            Pageable pageable
    ) {
        return workerRepository.findConsultationSpecialists(category, q, city, area, availableNow, pageable)
                .map(ConsultationSpecialistResponse::from);
    }
}
