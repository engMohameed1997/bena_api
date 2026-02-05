package com.bena.api.module.offers.service;

import com.bena.api.module.offers.dto.*;
import com.bena.api.module.offers.entity.*;
import com.bena.api.module.offers.repository.*;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.repository.UserRepository;
import com.bena.api.module.worker.entity.Worker;
import com.bena.api.module.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OfferRequestService {

    private final OfferRequestRepository requestRepository;
    private final ContractorOfferRepository offerRepository;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;
    // private final NotificationService notificationService; // للإشعارات لاحقاً

    /**
     * إنشاء طلب عرض جديد
     */
    @Transactional
    public OfferRequestResponse createRequest(OfferRequestCreateDto dto, UUID userId) {
        // التحقق من عدم وجود طلب سابق
        if (requestRepository.existsByOfferIdAndUserId(dto.getOfferId(), userId)) {
            throw new RuntimeException("لديك طلب سابق على هذا العرض");
        }
        
        ContractorOffer offer = offerRepository.findById(dto.getOfferId())
                .orElseThrow(() -> new RuntimeException("العرض غير موجود"));
        
        if (!offer.getIsActive()) {
            throw new RuntimeException("هذا العرض غير متاح حالياً");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
        
        OfferRequest request = OfferRequest.builder()
                .offer(offer)
                .user(user)
                .message(dto.getMessage())
                .phone(dto.getPhone() != null ? dto.getPhone() : user.getPhone())
                .projectArea(dto.getProjectArea())
                .status(OfferRequestStatus.PENDING)
                .build();
        
        request = requestRepository.save(request);
        
        log.info("تم إنشاء طلب عرض جديد: {} للعرض: {} من المستخدم: {}", 
                request.getId(), offer.getId(), userId);
        
        // إرسال إشعار للمقاول (يمكن تفعيله لاحقاً)
        // notificationService.sendOfferRequestNotification(offer.getWorker().getUserId(), request);
        
        return mapToResponse(request);
    }

    /**
     * جلب طلبات المستخدم
     */
    @Transactional(readOnly = true)
    public Page<OfferRequestResponse> getMyRequests(UUID userId, Pageable pageable) {
        return requestRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToResponse);
    }

    /**
     * جلب الطلبات الواردة للمهني
     */
    @Transactional(readOnly = true)
    public Page<OfferRequestResponse> getIncomingRequests(UUID userId, Pageable pageable) {
        Worker worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("لا يوجد حساب مهني"));
        
        return requestRepository.findByWorkerId(worker.getId(), pageable)
                .map(this::mapToResponse);
    }

    /**
     * جلب تفاصيل طلب
     */
    @Transactional(readOnly = true)
    public OfferRequestResponse getRequestById(UUID requestId, UUID userId) {
        OfferRequest request = requestRepository.findByIdWithDetails(requestId)
                .orElseThrow(() -> new RuntimeException("الطلب غير موجود"));
        
        // التحقق من الصلاحية
        Worker worker = workerRepository.findByUserId(userId).orElse(null);
        boolean isOwner = request.getUser().getId().equals(userId);
        boolean isProvider = worker != null && request.getOffer().getWorker().getId().equals(worker.getId());
        
        if (!isOwner && !isProvider) {
            throw new RuntimeException("لا يمكنك الوصول إلى هذا الطلب");
        }
        
        return mapToResponse(request);
    }

    /**
     * تحديث حالة الطلب (للمهني)
     */
    @Transactional
    public OfferRequestResponse updateRequestStatus(UUID requestId, OfferRequestUpdateDto dto, UUID userId) {
        OfferRequest request = requestRepository.findByIdWithDetails(requestId)
                .orElseThrow(() -> new RuntimeException("الطلب غير موجود"));
        
        Worker worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("غير مصرح لك"));
        
        if (!request.getOffer().getWorker().getId().equals(worker.getId())) {
            throw new RuntimeException("لا يمكنك تعديل هذا الطلب");
        }
        
        request.setStatus(dto.getStatus());
        if (dto.getProviderNotes() != null) {
            request.setProviderNotes(dto.getProviderNotes());
        }
        
        requestRepository.save(request);
        
        log.info("تم تحديث حالة الطلب: {} إلى: {}", requestId, dto.getStatus());
        
        // إرسال إشعار للمستخدم
        // notificationService.sendRequestStatusNotification(request.getUser().getId(), request);
        
        return mapToResponse(request);
    }

    /**
     * إلغاء طلب (للمستخدم)
     */
    @Transactional
    public void cancelRequest(UUID requestId, UUID userId) {
        OfferRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("الطلب غير موجود"));
        
        if (!request.getUser().getId().equals(userId)) {
            throw new RuntimeException("لا يمكنك إلغاء هذا الطلب");
        }
        
        if (request.getStatus() != OfferRequestStatus.PENDING) {
            throw new RuntimeException("لا يمكن إلغاء الطلب في هذه المرحلة");
        }
        
        request.setStatus(OfferRequestStatus.CANCELLED);
        requestRepository.save(request);
        
        log.info("تم إلغاء الطلب: {}", requestId);
    }

    /**
     * إحصائيات الطلبات للمهني
     */
    @Transactional(readOnly = true)
    public OfferRequestStats getRequestStats(UUID userId) {
        Worker worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("لا يوجد حساب مهني"));
        
        Long total = requestRepository.countByWorkerId(worker.getId());
        Long pending = requestRepository.countByWorkerIdAndStatus(worker.getId(), OfferRequestStatus.PENDING);
        Long accepted = requestRepository.countByWorkerIdAndStatus(worker.getId(), OfferRequestStatus.ACCEPTED);
        Long completed = requestRepository.countByWorkerIdAndStatus(worker.getId(), OfferRequestStatus.COMPLETED);
        
        return OfferRequestStats.builder()
                .totalRequests(total)
                .pendingRequests(pending)
                .acceptedRequests(accepted)
                .completedRequests(completed)
                .build();
    }

    // ================ Helper Methods ================

    private OfferRequestResponse mapToResponse(OfferRequest request) {
        ContractorOffer offer = request.getOffer();
        User user = request.getUser();
        
        return OfferRequestResponse.builder()
                .id(request.getId())
                .status(request.getStatus())
                .statusArabic(request.getStatus().getArabicName())
                .message(request.getMessage())
                .phone(request.getPhone())
                .projectArea(request.getProjectArea())
                .providerNotes(request.getProviderNotes())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .offerId(offer.getId())
                .offerTitle(offer.getTitle())
                .offerCoverImageData(offer.getCoverImageData())
                .userId(user.getId())
                .userName(user.getFullName())
                .userPhone(user.getPhone())
                .userEmail(user.getEmail())
                .build();
    }

    // DTO للإحصائيات
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class OfferRequestStats {
        private Long totalRequests;
        private Long pendingRequests;
        private Long acceptedRequests;
        private Long completedRequests;
    }
}
