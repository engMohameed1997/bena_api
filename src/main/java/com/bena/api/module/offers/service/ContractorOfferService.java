package com.bena.api.module.offers.service;

import com.bena.api.module.offers.dto.*;
import com.bena.api.module.offers.entity.*;
import com.bena.api.module.offers.repository.*;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.worker.entity.Worker;
import com.bena.api.module.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContractorOfferService {

    private final ContractorOfferRepository offerRepository;
    private final OfferFeatureRepository featureRepository;
    private final OfferImageRepository imageRepository;
    private final WorkerRepository workerRepository;

    /**
     * جلب جميع العروض مع الفلترة
     */
    @Transactional(readOnly = true)
    public Page<OfferListResponse> getOffers(OfferFilterRequest filter, Pageable pageable) {
        Sort sort = buildSort(filter);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        
        Page<ContractorOffer> offers = offerRepository.findWithFilters(
                filter.getOfferType(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                filter.getCity(),
                filter.getProviderId(),
                filter.getVerifiedOnly() != null && filter.getVerifiedOnly(),
                sortedPageable
        );
        
        return offers.map(this::mapToListResponse);
    }

    /**
     * جلب العروض المميزة
     */
    @Transactional(readOnly = true)
    public List<OfferListResponse> getFeaturedOffers() {
        return offerRepository.findByIsFeaturedTrueAndIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToListResponse)
                .collect(Collectors.toList());
    }

    /**
     * البحث النصي
     */
    @Transactional(readOnly = true)
    public Page<OfferListResponse> searchOffers(String query, Pageable pageable) {
        return offerRepository.searchByQuery(query, pageable).map(this::mapToListResponse);
    }

    /**
     * جلب تفاصيل عرض
     */
    @Transactional
    public OfferResponse getOfferById(UUID offerId) {
        ContractorOffer offer = offerRepository.findByIdWithDetails(offerId)
                .orElseThrow(() -> new RuntimeException("العرض غير موجود"));
        
        // زيادة عداد المشاهدات
        offer.incrementViewCount();
        offerRepository.save(offer);
        
        return mapToFullResponse(offer);
    }

    /**
     * إنشاء عرض جديد
     */
    @Transactional
    public OfferResponse createOffer(OfferCreateRequest request, UUID userId) {
        Worker worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("يجب أن يكون لديك حساب مهني لإنشاء عرض"));
        
        ContractorOffer offer = ContractorOffer.builder()
                .worker(worker)
                .title(request.getTitle())
                .description(request.getDescription())
                .offerType(request.getOfferType())
                .basePrice(request.getBasePrice())
                .priceUnit(request.getPriceUnit() != null ? request.getPriceUnit() : PriceUnit.PROJECT)
                .minArea(request.getMinArea())
                .maxArea(request.getMaxArea())
                .executionDays(request.getExecutionDays())
                .coverImageData(request.getCoverImageData())
                .city(request.getCity() != null ? request.getCity() : worker.getCity())
                .area(request.getArea() != null ? request.getArea() : worker.getArea())
                .isActive(true)
                .isFeatured(false)
                .viewCount(0)
                .build();
        
        offer = offerRepository.save(offer);
        
        // إضافة المميزات
        if (request.getFeatures() != null && !request.getFeatures().isEmpty()) {
            int order = 0;
            for (OfferFeatureDto featureDto : request.getFeatures()) {
                OfferFeature feature = OfferFeature.builder()
                        .offer(offer)
                        .featureText(featureDto.getFeatureText())
                        .isIncluded(featureDto.getIsIncluded() != null ? featureDto.getIsIncluded() : true)
                        .displayOrder(order++)
                        .build();
                featureRepository.save(feature);
            }
        }
        
        // إضافة الصور
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            int order = 0;
            for (OfferImageDto imageDto : request.getImages()) {
                OfferImage image = OfferImage.builder()
                        .offer(offer)
                        .imageData(imageDto.getImageData())
                        .imageUrl(imageDto.getImageUrl())
                        .caption(imageDto.getCaption())
                        .displayOrder(order++)
                        .build();
                imageRepository.save(image);
            }
        }
        
        log.info("تم إنشاء عرض جديد: {} بواسطة العامل: {}", offer.getId(), worker.getId());
        
        return getOfferById(offer.getId());
    }

    /**
     * تحديث عرض
     */
    @Transactional
    public OfferResponse updateOffer(UUID offerId, OfferUpdateRequest request, UUID userId) {
        ContractorOffer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("العرض غير موجود"));
        
        Worker worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("غير مصرح لك"));
        
        if (!offer.getWorker().getId().equals(worker.getId())) {
            throw new RuntimeException("لا يمكنك تعديل عرض لا يخصك");
        }
        
        // تحديث الحقول
        if (request.getTitle() != null) offer.setTitle(request.getTitle());
        if (request.getDescription() != null) offer.setDescription(request.getDescription());
        if (request.getOfferType() != null) offer.setOfferType(request.getOfferType());
        if (request.getBasePrice() != null) offer.setBasePrice(request.getBasePrice());
        if (request.getPriceUnit() != null) offer.setPriceUnit(request.getPriceUnit());
        if (request.getMinArea() != null) offer.setMinArea(request.getMinArea());
        if (request.getMaxArea() != null) offer.setMaxArea(request.getMaxArea());
        if (request.getExecutionDays() != null) offer.setExecutionDays(request.getExecutionDays());
        if (request.getCoverImageData() != null) offer.setCoverImageData(request.getCoverImageData());
        if (request.getCity() != null) offer.setCity(request.getCity());
        if (request.getArea() != null) offer.setArea(request.getArea());
        if (request.getIsActive() != null) offer.setIsActive(request.getIsActive());
        
        offerRepository.save(offer);
        
        // تحديث المميزات إذا تم إرسالها
        if (request.getFeatures() != null) {
            featureRepository.deleteByOfferId(offerId);
            int order = 0;
            for (OfferFeatureDto featureDto : request.getFeatures()) {
                OfferFeature feature = OfferFeature.builder()
                        .offer(offer)
                        .featureText(featureDto.getFeatureText())
                        .isIncluded(featureDto.getIsIncluded() != null ? featureDto.getIsIncluded() : true)
                        .displayOrder(order++)
                        .build();
                featureRepository.save(feature);
            }
        }
        
        // تحديث الصور إذا تم إرسالها
        if (request.getImages() != null) {
            imageRepository.deleteByOfferId(offerId);
            int order = 0;
            for (OfferImageDto imageDto : request.getImages()) {
                OfferImage image = OfferImage.builder()
                        .offer(offer)
                        .imageData(imageDto.getImageData())
                        .imageUrl(imageDto.getImageUrl())
                        .caption(imageDto.getCaption())
                        .displayOrder(order++)
                        .build();
                imageRepository.save(image);
            }
        }
        
        log.info("تم تحديث العرض: {}", offerId);
        
        return getOfferById(offerId);
    }

    /**
     * حذف عرض
     */
    @Transactional
    public void deleteOffer(UUID offerId, UUID userId) {
        ContractorOffer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("العرض غير موجود"));
        
        Worker worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("غير مصرح لك"));
        
        if (!offer.getWorker().getId().equals(worker.getId())) {
            throw new RuntimeException("لا يمكنك حذف عرض لا يخصك");
        }
        
        offerRepository.delete(offer);
        log.info("تم حذف العرض: {}", offerId);
    }

    /**
     * جلب عروض المستخدم
     */
    @Transactional(readOnly = true)
    public List<OfferListResponse> getMyOffers(UUID userId) {
        Worker worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("لا يوجد حساب مهني"));
        
        return offerRepository.findByWorkerIdOrderByCreatedAtDesc(worker.getId())
                .stream()
                .map(this::mapToListResponse)
                .collect(Collectors.toList());
    }

    /**
     * تفعيل/إيقاف عرض
     */
    @Transactional
    public void toggleOfferStatus(UUID offerId, UUID userId) {
        ContractorOffer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("العرض غير موجود"));
        
        Worker worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("غير مصرح لك"));
        
        if (!offer.getWorker().getId().equals(worker.getId())) {
            throw new RuntimeException("لا يمكنك تعديل عرض لا يخصك");
        }
        
        offer.setIsActive(!offer.getIsActive());
        offerRepository.save(offer);
    }

    // ================ Helper Methods ================

    private Sort buildSort(OfferFilterRequest filter) {
        String sortBy = filter.getSortBy() != null ? filter.getSortBy() : "createdAt";
        String direction = filter.getSortDirection() != null ? filter.getSortDirection() : "desc";
        
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        
        return switch (sortBy.toLowerCase()) {
            case "price" -> Sort.by(sortDirection, "basePrice");
            case "views" -> Sort.by(sortDirection, "viewCount");
            case "date" -> Sort.by(sortDirection, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "isFeatured")
                    .and(Sort.by(sortDirection, "createdAt"));
        };
    }

    private OfferListResponse mapToListResponse(ContractorOffer offer) {
        Worker worker = offer.getWorker();
        long includedCount = offer.getFeatures() != null 
                ? offer.getFeatures().stream().filter(f -> Boolean.TRUE.equals(f.getIsIncluded())).count() 
                : 0;
        
        return OfferListResponse.builder()
                .id(offer.getId())
                .title(offer.getTitle())
                .offerType(offer.getOfferType())
                .offerTypeArabic(offer.getOfferType().getArabicName())
                .basePrice(offer.getBasePrice())
                .priceUnit(offer.getPriceUnit())
                .priceUnitArabic(offer.getPriceUnit() != null ? offer.getPriceUnit().getArabicName() : null)
                .executionDays(offer.getExecutionDays())
                .coverImageUrl(offer.getCoverImageUrl())
                .coverImageData(offer.getCoverImageData())
                .isFeatured(offer.getIsFeatured())
                .viewCount(offer.getViewCount())
                .city(offer.getCity())
                .area(offer.getArea())
                .createdAt(offer.getCreatedAt())
                .providerId(worker.getId())
                .providerName(worker.getName())
                .providerCategory(worker.getCategory() != null ? worker.getCategory().name() : null)
                .providerCategoryArabic(worker.getCategory() != null ? worker.getCategory().getArabicName() : null)
                .providerImageUrl(worker.getProfileImageUrl())
                .providerImageData(null) // لتقليل حجم الاستجابة في القائمة
                .providerRating(worker.getAverageRating())
                .providerReviewCount(worker.getReviewCount())
                .providerVerified(worker.getIsVerified())
                .includedFeaturesCount((int) includedCount)
                .build();
    }

    private OfferResponse mapToFullResponse(ContractorOffer offer) {
        Worker worker = offer.getWorker();
        
        List<OfferFeatureDto> features = offer.getFeatures() != null
                ? offer.getFeatures().stream()
                    .map(f -> OfferFeatureDto.builder()
                            .id(f.getId())
                            .featureText(f.getFeatureText())
                            .isIncluded(f.getIsIncluded())
                            .displayOrder(f.getDisplayOrder())
                            .build())
                    .collect(Collectors.toList())
                : List.of();
        
        List<OfferImageDto> images = offer.getImages() != null
                ? offer.getImages().stream()
                    .map(i -> OfferImageDto.builder()
                            .id(i.getId())
                            .imageUrl(i.getImageUrl())
                            .imageData(i.getImageData())
                            .caption(i.getCaption())
                            .displayOrder(i.getDisplayOrder())
                            .build())
                    .collect(Collectors.toList())
                : List.of();
        
        OfferResponse.ProviderInfo providerInfo = OfferResponse.ProviderInfo.builder()
                .id(worker.getId())
                .name(worker.getName())
                .category(worker.getCategory() != null ? worker.getCategory().name() : null)
                .categoryArabic(worker.getCategory() != null ? worker.getCategory().getArabicName() : null)
                .profileImageUrl(worker.getProfileImageUrl())
                .averageRating(worker.getAverageRating())
                .reviewCount(worker.getReviewCount())
                .phoneNumber(worker.getPhoneNumber())
                .whatsappNumber(worker.getWhatsappNumber())
                .isVerified(worker.getIsVerified())
                .city(worker.getCity())
                .area(worker.getArea())
                .experienceYears(worker.getExperienceYears())
                .completedProjectsCount(worker.getCompletedProjectsCount())
                .build();
        
        return OfferResponse.builder()
                .id(offer.getId())
                .title(offer.getTitle())
                .description(offer.getDescription())
                .offerType(offer.getOfferType())
                .offerTypeArabic(offer.getOfferType().getArabicName())
                .basePrice(offer.getBasePrice())
                .priceUnit(offer.getPriceUnit())
                .priceUnitArabic(offer.getPriceUnit() != null ? offer.getPriceUnit().getArabicName() : null)
                .minArea(offer.getMinArea())
                .maxArea(offer.getMaxArea())
                .executionDays(offer.getExecutionDays())
                .coverImageUrl(offer.getCoverImageUrl())
                .coverImageData(offer.getCoverImageData())
                .isActive(offer.getIsActive())
                .isFeatured(offer.getIsFeatured())
                .viewCount(offer.getViewCount())
                .city(offer.getCity())
                .area(offer.getArea())
                .createdAt(offer.getCreatedAt())
                .updatedAt(offer.getUpdatedAt())
                .provider(providerInfo)
                .features(features)
                .images(images)
                .build();
    }
}
