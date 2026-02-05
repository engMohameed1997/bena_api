package com.bena.api.module.worker.service;

import com.bena.api.common.service.FileStorageService;
import com.bena.api.module.worker.dto.*;
import com.bena.api.module.worker.entity.*;
import com.bena.api.module.worker.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final WorkerMediaRepository mediaRepository;
    private final WorkerReviewRepository reviewRepository;
    private final FileStorageService fileStorageService;

    // ==================== عمليات القراءة ====================

    /**
     * جلب جميع العمال مع الفلترة
     */
    @Transactional(readOnly = true)
    public Page<WorkerDTO> getWorkers(
            WorkerCategory category,
            Double minRating,
            String location,
            Pageable pageable
    ) {
        Page<Worker> workers;
        
        // جلب المختصين المعتمدين فقط (isActive + isVerified)
        if (category != null) {
            workers = workerRepository.findVisibleWorkersWithCategory(category.name(), pageable);
        } else {
            workers = workerRepository.findVisibleWorkers(pageable);
        }
        
        return workers.map(this::toDTO);
    }

    /**
     * جلب جميع العمال للأدمن (بما فيهم المعلقين)
     */
    @Transactional(readOnly = true)
    public Page<WorkerDTO> getAllWorkersForAdmin(Pageable pageable) {
        return workerRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * جلب عامل واحد
     */
    @Transactional(readOnly = true)
    public WorkerDTO getWorkerById(Long id) {
        Worker worker = workerRepository.findByIdWithMedia(id)
                .orElseThrow(() -> new RuntimeException("العامل غير موجود"));
        return toDTOWithMedia(worker);
    }

    /**
     * البحث عن عمال
     */
    public Page<WorkerDTO> searchWorkers(String query, Pageable pageable) {
        return workerRepository.searchWorkers(query, pageable).map(this::toDTO);
    }

    /**
     * البحث المتقدم
     */
    public Page<WorkerDTO> advancedSearch(
            String name, WorkerCategory category, String city, String area,
            Double minRating, java.math.BigDecimal maxPricePerMeter, java.math.BigDecimal maxPricePerDay,
            Boolean featuredOnly, Boolean worksAtNight, Integer maxCompletionDays,
            Double latitude, Double longitude, Double distanceKm,
            Pageable pageable
    ) {
        // جلب المختصين المعتمدين فقط ثم فلترة
        Page<Worker> allWorkers = workerRepository.findVisibleWorkers(pageable);
        
        List<Worker> filtered = allWorkers.getContent().stream()
                .filter(w -> name == null || w.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(w -> category == null || w.getCategory() == category)
                .filter(w -> city == null || (w.getCity() != null && w.getCity().toLowerCase().contains(city.toLowerCase())))
                .filter(w -> area == null || (w.getArea() != null && w.getArea().toLowerCase().contains(area.toLowerCase())))
                .filter(w -> minRating == null || (w.getAverageRating() != null && w.getAverageRating() >= minRating))
                .filter(w -> maxPricePerMeter == null || (w.getPricePerMeter() != null && w.getPricePerMeter().compareTo(maxPricePerMeter) <= 0))
                .filter(w -> maxPricePerDay == null || (w.getPricePerDay() != null && w.getPricePerDay().compareTo(maxPricePerDay) <= 0))
                .filter(w -> featuredOnly == null || !featuredOnly || Boolean.TRUE.equals(w.getIsFeatured()))
                .filter(w -> worksAtNight == null || !worksAtNight || Boolean.TRUE.equals(w.getWorksAtNight()))
                .filter(w -> maxCompletionDays == null || (w.getEstimatedCompletionDays() != null && w.getEstimatedCompletionDays() <= maxCompletionDays))
                .filter(w -> {
                    if (latitude == null || longitude == null || distanceKm == null) return true;
                    if (w.getLatitude() == null || w.getLongitude() == null) return false;
                    double distance = calculateDistance(latitude, longitude, w.getLatitude(), w.getLongitude());
                    return distance <= distanceKm;
                })
                .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(
                filtered.stream().map(this::toDTO).collect(Collectors.toList()),
                pageable,
                filtered.size()
        );
    }

    /**
     * جلب العمال القريبين
     */
    public Page<WorkerDTO> getNearbyWorkers(Double latitude, Double longitude, Double distanceKm, Pageable pageable) {
        Page<Worker> allWorkers = workerRepository.findVisibleWorkers(pageable);
        
        List<Worker> nearby = allWorkers.getContent().stream()
                .filter(w -> w.getLatitude() != null && w.getLongitude() != null)
                .filter(w -> calculateDistance(latitude, longitude, w.getLatitude(), w.getLongitude()) <= distanceKm)
                .sorted((w1, w2) -> {
                    double d1 = calculateDistance(latitude, longitude, w1.getLatitude(), w1.getLongitude());
                    double d2 = calculateDistance(latitude, longitude, w2.getLatitude(), w2.getLongitude());
                    return Double.compare(d1, d2);
                })
                .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(
                nearby.stream().map(this::toDTO).collect(Collectors.toList()),
                pageable,
                nearby.size()
        );
    }

    /**
     * حساب المسافة بين نقطتين (بالكيلومتر) - Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // نصف قطر الأرض بالكيلومتر
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * جلب العمال المميزين
     */
    public List<WorkerDTO> getFeaturedWorkers() {
        return workerRepository.findVisibleFeaturedWorkers()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * جلب الفئات مع عدد العمال
     */
    public List<CategoryCountDTO> getCategoriesWithCount() {
        return java.util.Arrays.stream(WorkerCategory.values())
                .map(cat -> CategoryCountDTO.builder()
                        .category(cat)
                        .arabicName(cat.getArabicName())
                        .count(workerRepository.countVisibleByCategory(cat.name()))
                        .build())
                .filter(c -> c.getCount() > 0)
                .collect(Collectors.toList());
    }

    // ==================== عمليات الأدمن ====================

    /**
     * إنشاء عامل جديد
     */
    @Transactional
    public WorkerDTO createWorker(WorkerCreateDTO dto, MultipartFile profileImage) throws IOException {
        Worker worker = Worker.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .phoneNumber(dto.getPhoneNumber())
                .whatsappNumber(dto.getWhatsappNumber())
                .experienceYears(dto.getExperienceYears())
                .location(dto.getLocation())
                .isFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false)
                .isActive(true)
                .build();

        if (profileImage != null && !profileImage.isEmpty()) {
            String imageUrl = fileStorageService.store(profileImage, "workers/profiles");
            worker.setProfileImageUrl(imageUrl);
        }

        Worker saved = workerRepository.save(worker);
        return toDTO(saved);
    }

    /**
     * تحديث عامل
     */
    @Transactional
    public WorkerDTO updateWorker(Long id, WorkerCreateDTO dto, MultipartFile profileImage) throws IOException {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("العامل غير موجود"));

        worker.setName(dto.getName());
        worker.setCategory(dto.getCategory());
        worker.setDescription(dto.getDescription());
        worker.setPhoneNumber(dto.getPhoneNumber());
        worker.setWhatsappNumber(dto.getWhatsappNumber());
        worker.setExperienceYears(dto.getExperienceYears());
        worker.setLocation(dto.getLocation());
        if (dto.getIsFeatured() != null) {
            worker.setIsFeatured(dto.getIsFeatured());
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            // حذف الصورة القديمة
            if (worker.getProfileImageUrl() != null) {
                fileStorageService.delete(worker.getProfileImageUrl());
            }
            
            String imageUrl = fileStorageService.store(profileImage, "workers/profiles");
            worker.setProfileImageUrl(imageUrl);
        }

        Worker saved = workerRepository.save(worker);
        return toDTO(saved);
    }

    /**
     * حذف عامل
     */
    @Transactional
    public void deleteWorker(Long id) {
        workerRepository.deleteById(id);
    }

    /**
     * تفعيل/إلغاء تفعيل عامل
     */
    @Transactional
    public WorkerDTO toggleWorkerActive(Long id) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("العامل غير موجود"));
        worker.setIsActive(!worker.getIsActive());
        return toDTO(workerRepository.save(worker));
    }

    /**
     * تمييز/إلغاء تمييز عامل
     */
    @Transactional
    public WorkerDTO toggleWorkerFeatured(Long id) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("العامل غير موجود"));
        worker.setIsFeatured(!worker.getIsFeatured());
        return toDTO(workerRepository.save(worker));
    }

    /**
     * توثيق حساب عامل
     */
    @Transactional
    public WorkerDTO verifyWorker(Long id) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("العامل غير موجود"));
        worker.setVerificationStatus(VerificationStatus.VERIFIED);
        return toDTO(workerRepository.save(worker));
    }

    // ==================== إدارة الوسائط ====================

    /**
     * إضافة صورة/فيديو لمعرض العامل
     */
    @Transactional
    public WorkerMediaDTO addMedia(Long workerId, MultipartFile file, String caption) throws IOException {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("العامل غير موجود"));

        String contentType = file.getContentType();
        WorkerMedia.MediaType mediaType = contentType != null && contentType.startsWith("video")
                ? WorkerMedia.MediaType.VIDEO
                : WorkerMedia.MediaType.IMAGE;

        String mediaUrl = fileStorageService.store(file, "workers/media");
        
        WorkerMedia media = WorkerMedia.builder()
                .worker(worker)
                .mediaType(mediaType)
                .mediaUrl(mediaUrl)
                .caption(caption)
                .displayOrder(worker.getMediaGallery().size())
                .build();

        WorkerMedia saved = mediaRepository.save(media);
        return toMediaDTO(saved);
    }

    /**
     * إضافة رابط فيديو خارجي
     */
    @Transactional
    public WorkerMediaDTO addExternalVideo(Long workerId, String url, String caption) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("العامل غير موجود"));

        WorkerMedia media = WorkerMedia.builder()
                .worker(worker)
                .mediaType(WorkerMedia.MediaType.VIDEO)
                .externalUrl(url)
                .caption(caption)
                .displayOrder(worker.getMediaGallery().size())
                .build();

        WorkerMedia saved = mediaRepository.save(media);
        return toMediaDTO(saved);
    }

    /**
     * حذف وسائط
     */
    @Transactional
    public void deleteMedia(Long mediaId) {
        WorkerMedia media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("الوسائط غير موجودة"));
        
        // حذف الملف من filesystem
        if (media.getMediaUrl() != null) {
            fileStorageService.delete(media.getMediaUrl());
        }
        
        mediaRepository.deleteById(mediaId);
    }

    // ==================== التقييمات ====================

    /**
     * إضافة تقييم
     */
    @Transactional
    public WorkerReviewDTO addReview(Long workerId, WorkerReviewCreateDTO dto) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("العامل غير موجود"));

        // التحقق من التقييم (1-5)
        int rating = Math.max(1, Math.min(5, dto.getRating()));

        WorkerReview review = WorkerReview.builder()
                .worker(worker)
                .reviewerName(dto.getReviewerName())
                .rating(rating)
                .comment(dto.getComment())
                .isApproved(true)
                .build();

        WorkerReview saved = reviewRepository.save(review);

        // تحديث متوسط التقييم
        worker.getReviews().add(saved);
        worker.updateAverageRating();
        workerRepository.save(worker);

        return toReviewDTO(saved);
    }

    /**
     * جلب تقييمات عامل
     */
    public Page<WorkerReviewDTO> getWorkerReviews(Long workerId, Pageable pageable) {
        return reviewRepository.findByWorkerIdAndIsApprovedTrue(workerId, pageable)
                .map(this::toReviewDTO);
    }

    // ==================== تحويل DTOs ====================

    private WorkerDTO toDTO(Worker worker) {
        // دعم الصور القديمة (byte[]) والجديدة (URL)
        String imageUrl = worker.getProfileImageUrl();
        
        log.debug("Worker {}: profileImageUrl={}, profileImage={}, profileImageType={}", 
                 worker.getId(), imageUrl, 
                 worker.getProfileImage() != null ? worker.getProfileImage().length + " bytes" : "null",
                 worker.getProfileImageType());
        
        // إذا لم يكن هناك URL، حول byte[] إلى Base64 Data URL
        if (imageUrl == null && worker.getProfileImage() != null && worker.getProfileImage().length > 0) {
            String contentType = worker.getProfileImageType() != null ? worker.getProfileImageType() : "image/jpeg";
            imageUrl = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(worker.getProfileImage());
            log.info("Converted byte[] to Base64 for worker {}: {} bytes -> data URL", worker.getId(), worker.getProfileImage().length);
        } else if (imageUrl == null) {
            log.warn("No image data found for worker {}: profileImageUrl=null, profileImage=null", worker.getId());
            // صورة تجريبية مؤقتة - مربع أزرق مع كلمة WORKER
            imageUrl = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iIzMzNzNkYyIvPjx0ZXh0IHg9IjUwIiB5PSI1NSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjE0IiBmaWxsPSJ3aGl0ZSIgdGV4dC1hbmNob3I9Im1pZGRsZSI+V09SS0VSPC90ZXh0Pjwvc3ZnPg==";
        }
        
        return WorkerDTO.builder()
                .id(worker.getId())
                .name(worker.getName())
                .category(worker.getCategory())
                .categoryArabicName(worker.getCategory().getArabicName())
                .description(worker.getDescription())
                .phoneNumber(worker.getPhoneNumber())
                .whatsappNumber(worker.getWhatsappNumber())
                .profileImageUrl(imageUrl)
                .averageRating(worker.getAverageRating())
                .reviewCount(worker.getReviewCount())
                .isFeatured(worker.getIsFeatured())
                .isActive(worker.getIsActive())
                .experienceYears(worker.getExperienceYears())
                .location(worker.getLocation())
                // الموقع الجغرافي
                .city(worker.getCity())
                .area(worker.getArea())
                .latitude(worker.getLatitude())
                .longitude(worker.getLongitude())
                // الأسعار
                .pricePerMeter(worker.getPricePerMeter())
                .pricePerDay(worker.getPricePerDay())
                .pricePerVisit(worker.getPricePerVisit())
                // معلومات إضافية
                .worksAtNight(worker.getWorksAtNight())
                .estimatedCompletionDays(worker.getEstimatedCompletionDays())
                .email(worker.getEmail())
                .mediaGallery(null)  // لا نرجع mediaGallery في list view لتجنب Lazy Loading
                .createdAt(worker.getCreatedAt())
                .build();
    }
    
    // تحويل مع mediaGallery (للـ detail view)
    private WorkerDTO toDTOWithMedia(Worker worker) {
        String imageUrl = worker.getProfileImageUrl();
        
        if (imageUrl == null && worker.getProfileImage() != null && worker.getProfileImage().length > 0) {
            String contentType = worker.getProfileImageType() != null ? worker.getProfileImageType() : "image/jpeg";
            imageUrl = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(worker.getProfileImage());
        }
        
        return WorkerDTO.builder()
                .id(worker.getId())
                .name(worker.getName())
                .category(worker.getCategory())
                .categoryArabicName(worker.getCategory().getArabicName())
                .description(worker.getDescription())
                .phoneNumber(worker.getPhoneNumber())
                .whatsappNumber(worker.getWhatsappNumber())
                .profileImageUrl(imageUrl)
                .averageRating(worker.getAverageRating())
                .reviewCount(worker.getReviewCount())
                .isFeatured(worker.getIsFeatured())
                .isActive(worker.getIsActive())
                .experienceYears(worker.getExperienceYears())
                .location(worker.getLocation())
                .city(worker.getCity())
                .area(worker.getArea())
                .latitude(worker.getLatitude())
                .longitude(worker.getLongitude())
                .pricePerMeter(worker.getPricePerMeter())
                .pricePerDay(worker.getPricePerDay())
                .pricePerVisit(worker.getPricePerVisit())
                .worksAtNight(worker.getWorksAtNight())
                .estimatedCompletionDays(worker.getEstimatedCompletionDays())
                .email(worker.getEmail())
                .mediaGallery(worker.getMediaGallery() != null
                        ? worker.getMediaGallery().stream().map(this::toMediaDTO).collect(Collectors.toList())
                        : null)
                .createdAt(worker.getCreatedAt())
                .build();
    }

    private WorkerMediaDTO toMediaDTO(WorkerMedia media) {
        // دعم الوسائط القديمة (byte[]) والجديدة (URL)
        String mediaUrl = media.getMediaUrl();
        
        // إذا لم يكن هناك URL، حول byte[] إلى Base64 Data URL
        if (mediaUrl == null && media.getMediaData() != null && media.getMediaData().length > 0) {
            String contentType = media.getContentType() != null ? media.getContentType() : "image/jpeg";
            mediaUrl = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(media.getMediaData());
        } else if (mediaUrl == null) {
            // صورة تجريبية مؤقتة للوسائط
            mediaUrl = "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0iIzY0NzQ4YiIvPjx0ZXh0IHg9IjUwIiB5PSI1NSIgZm9udC1mYW1pbHk9IkFyaWFsIiBmb250LXNpemU9IjEyIiBmaWxsPSJ3aGl0ZSIgdGV4dC1hbmNob3I9Im1pZGRsZSI+TUVESUE8L3RleHQ+PC9zdmc+";
        }
        
        return WorkerMediaDTO.builder()
                .id(media.getId())
                .mediaType(media.getMediaType())
                .mediaUrl(mediaUrl)
                .thumbnailUrl(media.getThumbnailUrl())
                .externalUrl(media.getExternalUrl())
                .caption(media.getCaption())
                .displayOrder(media.getDisplayOrder())
                .createdAt(media.getCreatedAt())
                .build();
    }

    private WorkerReviewDTO toReviewDTO(WorkerReview review) {
        return WorkerReviewDTO.builder()
                .id(review.getId())
                .workerId(review.getWorker().getId())
                .reviewerName(review.getReviewerName())
                .rating(review.getRating())
                .comment(review.getComment())
                .isApproved(review.getIsApproved())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
