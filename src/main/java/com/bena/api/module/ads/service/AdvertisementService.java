package com.bena.api.module.ads.service;

import com.bena.api.common.exception.InvalidOperationException;
import com.bena.api.common.exception.ResourceNotFoundException;
import com.bena.api.common.service.FileUploadService;
import com.bena.api.module.ads.dto.CreateAdvertisementRequest;
import com.bena.api.module.ads.dto.UpdateAdvertisementRequest;
import com.bena.api.module.ads.entity.Advertisement;
import com.bena.api.module.ads.enums.AdSection;
import com.bena.api.module.ads.repository.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final FileUploadService fileUploadService;

    @Value("${app.ads.upload-folder:ads}")
    private String adsUploadFolder;

    @Transactional(readOnly = true)
    public List<Advertisement> getActiveAdsBySection(AdSection section, OffsetDateTime now) {
        return advertisementRepository.findActiveAdsBySection(section, now);
    }

    @Transactional(readOnly = true)
    public Page<Advertisement> getAllAds(Pageable pageable) {
        return advertisementRepository.findAll(pageable);
    }

    @Transactional
    public Advertisement create(CreateAdvertisementRequest request) {
        validateTimeWindow(request.getStartAt(), request.getEndAt());

        Advertisement ad = Advertisement.builder()
                .title(request.getTitle())
                .imageUrl(request.getImageUrl())
                .targetType(request.getTargetType())
                .targetValue(request.getTargetValue())
                .sections(request.getSections())
                .active(request.getActive() == null || request.getActive())
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .build();

        return advertisementRepository.save(ad);
    }

    @Transactional
    public Advertisement update(UUID id, UpdateAdvertisementRequest request) {
        Advertisement ad = advertisementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("الإعلان غير موجود"));

        validateTimeWindow(
                request.getStartAt() != null ? request.getStartAt() : ad.getStartAt(),
                request.getEndAt() != null ? request.getEndAt() : ad.getEndAt()
        );

        if (request.getTitle() != null) ad.setTitle(request.getTitle());
        if (request.getImageUrl() != null) ad.setImageUrl(request.getImageUrl());
        if (request.getTargetType() != null) ad.setTargetType(request.getTargetType());
        if (request.getTargetValue() != null) ad.setTargetValue(request.getTargetValue());
        if (request.getSections() != null) ad.setSections(request.getSections());
        if (request.getActive() != null) ad.setActive(request.getActive());
        if (request.getPriority() != null) ad.setPriority(request.getPriority());
        if (request.getStartAt() != null) ad.setStartAt(request.getStartAt());
        if (request.getEndAt() != null) ad.setEndAt(request.getEndAt());

        return advertisementRepository.save(ad);
    }

    @Transactional
    public void delete(UUID id) {
        if (!advertisementRepository.existsById(id)) {
            throw new ResourceNotFoundException("الإعلان غير موجود");
        }
        advertisementRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Advertisement getById(UUID id) {
        return advertisementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("الإعلان غير موجود"));
    }

    @Transactional
    public UploadResult uploadImage(MultipartFile file) throws IOException {
        String relativePath = fileUploadService.uploadImage(file, adsUploadFolder);
        String url = fileUploadService.getFileUrl(relativePath);
        log.info("Ad image uploaded: {}", relativePath);
        return new UploadResult(relativePath, url);
    }

    private void validateTimeWindow(OffsetDateTime startAt, OffsetDateTime endAt) {
        if (startAt != null && endAt != null && endAt.isBefore(startAt)) {
            throw new InvalidOperationException("تاريخ النهاية يجب أن يكون بعد تاريخ البداية");
        }
    }

    public record UploadResult(String path, String url) {
    }
}
