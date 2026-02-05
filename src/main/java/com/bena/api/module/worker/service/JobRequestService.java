package com.bena.api.module.worker.service;


import com.bena.api.module.worker.dto.JobRequestCreateDTO;
import com.bena.api.module.worker.dto.JobRequestDTO;
import com.bena.api.module.worker.entity.JobRequest;
import com.bena.api.module.worker.entity.JobRequestImage;
import com.bena.api.module.worker.entity.Worker;
import com.bena.api.module.worker.repository.JobRequestRepository;
import com.bena.api.module.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobRequestService {

    private final JobRequestRepository jobRequestRepository;
    private final WorkerRepository workerRepository;


    /**
     * إنشاء طلب عمل جديد
     */
    @Transactional
    public JobRequestDTO createJobRequest(UUID userId, JobRequestCreateDTO dto, List<MultipartFile> images) throws IOException {
        Worker worker = workerRepository.findById(dto.getWorkerId())
                .orElseThrow(() -> new RuntimeException("العامل غير موجود"));

        JobRequest jobRequest = JobRequest.builder()
                .userId(userId)
                .worker(worker)
                .jobType(dto.getJobType())
                .description(dto.getDescription())
                .locationCity(dto.getLocationCity())
                .locationArea(dto.getLocationArea())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .budget(dto.getBudget())
                .status(JobRequest.JobStatus.PENDING)
                .build();

        // إضافة الصور
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    JobRequestImage img = JobRequestImage.builder()
                            .jobRequest(jobRequest)
                            .imageData(image.getBytes())
                            .contentType(image.getContentType())
                            .build();
                    jobRequest.getImages().add(img);
                }
            }
        }

        JobRequest saved = jobRequestRepository.save(jobRequest);

        // TODO: إضافة إشعارات FCM للعامل عند إنشاء طلب جديد

        return toDTO(saved);
    }

    /**
     * جلب طلبات المستخدم
     */
    @Transactional(readOnly = true)
    public Page<JobRequestDTO> getUserRequests(UUID userId, Pageable pageable) {
        return jobRequestRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toDTO);
    }

    /**
     * جلب طلبات العامل
     */
    @Transactional(readOnly = true)
    public Page<JobRequestDTO> getWorkerRequests(Long workerId, Pageable pageable) {
        return jobRequestRepository.findByWorkerIdOrderByCreatedAtDesc(workerId, pageable)
                .map(this::toDTO);
    }

    /**
     * جلب طلبات العامل حسب الحالة
     */
    @Transactional(readOnly = true)
    public Page<JobRequestDTO> getWorkerRequestsByStatus(Long workerId, JobRequest.JobStatus status, Pageable pageable) {
        return jobRequestRepository.findByWorkerIdAndStatusOrderByCreatedAtDesc(workerId, status, pageable)
                .map(this::toDTO);
    }

    /**
     * قبول الطلب
     */
    @Transactional
    public JobRequestDTO acceptRequest(Long requestId, Long workerId) {
        JobRequest request = getRequestForWorker(requestId, workerId);
        request.setStatus(JobRequest.JobStatus.ACCEPTED);
        JobRequest saved = jobRequestRepository.save(request);
        
        // TODO: إضافة الإشعارات بعد إصلاح user_id في job_requests
        
        return toDTO(saved);
    }

    /**
     * رفض الطلب
     */
    @Transactional
    public JobRequestDTO rejectRequest(Long requestId, Long workerId, String reason) {
        JobRequest request = getRequestForWorker(requestId, workerId);
        request.setStatus(JobRequest.JobStatus.REJECTED);
        request.setWorkerResponse(reason);
        JobRequest saved = jobRequestRepository.save(request);
        
        // TODO: إضافة الإشعارات بعد إصلاح user_id في job_requests
        
        return toDTO(saved);
    }

    /**
     * إرسال عرض سعر
     */
    @Transactional
    public JobRequestDTO sendPriceOffer(Long requestId, Long workerId, BigDecimal price, String message) {
        JobRequest request = getRequestForWorker(requestId, workerId);
        request.setStatus(JobRequest.JobStatus.OFFER_SENT);
        request.setWorkerPriceOffer(price);
        request.setWorkerResponse(message);
        JobRequest saved = jobRequestRepository.save(request);
        
        // TODO: إضافة الإشعارات بعد إصلاح user_id في job_requests
        
        return toDTO(saved);
    }

    /**
     * بدء العمل
     */
    @Transactional
    public JobRequestDTO startWork(Long requestId, Long workerId) {
        JobRequest request = getRequestForWorker(requestId, workerId);
        request.setStatus(JobRequest.JobStatus.IN_PROGRESS);
        return toDTO(jobRequestRepository.save(request));
    }

    /**
     * إكمال العمل
     */
    @Transactional
    public JobRequestDTO completeWork(Long requestId, Long workerId) {
        JobRequest request = getRequestForWorker(requestId, workerId);
        request.setStatus(JobRequest.JobStatus.COMPLETED);
        return toDTO(jobRequestRepository.save(request));
    }

    /**
     * إلغاء الطلب (من المستخدم)
     */
    @Transactional
    public JobRequestDTO cancelRequest(Long requestId, UUID userId) {
        JobRequest request = jobRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("الطلب غير موجود"));
        
        if (!request.getUserId().equals(userId)) {
            throw new RuntimeException("غير مصرح");
        }
        
        request.setStatus(JobRequest.JobStatus.CANCELLED);
        return toDTO(jobRequestRepository.save(request));
    }

    /**
     * قبول عرض السعر من جانب العميل
     */
    @Transactional
    public JobRequestDTO acceptOffer(Long requestId, UUID userId) {
        JobRequest request = jobRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("الطلب غير موجود"));
        
        if (!request.getUserId().equals(userId)) {
            throw new RuntimeException("غير مصرح");
        }
        
        if (request.getStatus() != JobRequest.JobStatus.OFFER_SENT) {
            throw new RuntimeException("لا يوجد عرض سعر للقبول");
        }
        
        request.setStatus(JobRequest.JobStatus.ACCEPTED);
        return toDTO(jobRequestRepository.save(request));
    }

    /**
     * رفض عرض السعر من جانب العميل
     */
    @Transactional
    public JobRequestDTO rejectOffer(Long requestId, UUID userId) {
        JobRequest request = jobRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("الطلب غير موجود"));
        
        if (!request.getUserId().equals(userId)) {
            throw new RuntimeException("غير مصرح");
        }
        
        if (request.getStatus() != JobRequest.JobStatus.OFFER_SENT) {
            throw new RuntimeException("لا يوجد عرض سعر للرفض");
        }
        
        request.setStatus(JobRequest.JobStatus.PENDING);
        request.setWorkerPriceOffer(null);
        request.setWorkerResponse(null);
        return toDTO(jobRequestRepository.save(request));
    }

    /**
     * عدد الطلبات قيد التنفيذ للعامل
     */
    @Transactional(readOnly = true)
    public long getInProgressCount(Long workerId) {
        return jobRequestRepository.countByWorkerIdAndStatus(workerId, JobRequest.JobStatus.IN_PROGRESS);
    }

    /**
     * عدد الطلبات المكتملة للعامل
     */
    @Transactional(readOnly = true)
    public long getCompletedCount(Long workerId) {
        return jobRequestRepository.countByWorkerIdAndStatus(workerId, JobRequest.JobStatus.COMPLETED);
    }

    /**
     * عدد الطلبات قيد الانتظار للعامل
     */
    @Transactional(readOnly = true)
    public long getPendingCount(Long workerId) {
        return jobRequestRepository.countByWorkerIdAndStatus(workerId, JobRequest.JobStatus.PENDING);
    }

    // ==================== Admin Methods ====================

    @Transactional(readOnly = true)
    public Page<JobRequestDTO> getAllJobRequests(Pageable pageable) {
        return jobRequestRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public JobRequestDTO getJobRequestById(Long id) {
        JobRequest jobRequest = jobRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("الطلب غير موجود"));
        return toDTO(jobRequest);
    }

    // ==================== Helper Methods ====================

    private JobRequest getRequestForWorker(Long requestId, Long workerId) {
        JobRequest request = jobRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("الطلب غير موجود"));
        
        if (!request.getWorker().getId().equals(workerId)) {
            throw new RuntimeException("غير مصرح");
        }
        
        return request;
    }

    private JobRequestDTO toDTO(JobRequest request) {
        return JobRequestDTO.builder()
                .id(request.getId())
                .userId(request.getUserId())
                .workerId(request.getWorker().getId())
                .workerName(request.getWorker().getName())
                .jobType(request.getJobType())
                .description(request.getDescription())
                .locationCity(request.getLocationCity())
                .locationArea(request.getLocationArea())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .budget(request.getBudget())
                .status(request.getStatus())
                .workerResponse(request.getWorkerResponse())
                .workerPriceOffer(request.getWorkerPriceOffer())
                .imagesBase64(request.getImages().stream()
                        .map(img -> Base64.getEncoder().encodeToString(img.getImageData()))
                        .collect(Collectors.toList()))
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
