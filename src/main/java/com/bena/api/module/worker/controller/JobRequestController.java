package com.bena.api.module.worker.controller;

import com.bena.api.module.worker.dto.JobRequestCreateDTO;
import com.bena.api.module.worker.dto.JobRequestDTO;
import com.bena.api.module.worker.entity.JobRequest;
import com.bena.api.module.worker.entity.Worker;
import com.bena.api.module.worker.service.JobRequestService;
import com.bena.api.module.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1/job-requests")
@RequiredArgsConstructor
public class JobRequestController {

    private final JobRequestService jobRequestService;
    private final WorkerRepository workerRepository;

    /**
     * إنشاء طلب عمل جديد
     */
    @PostMapping
    public ResponseEntity<?> createJobRequest(
            @RequestParam UUID userId,
            @RequestParam Long workerId,
            @RequestParam String jobType,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String locationCity,
            @RequestParam(required = false) String locationArea,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) BigDecimal budget,
            @RequestParam(required = false) List<MultipartFile> images) {
        try {
            JobRequestCreateDTO dto = JobRequestCreateDTO.builder()
                    .workerId(workerId)
                    .jobType(jobType)
                    .description(description)
                    .locationCity(locationCity)
                    .locationArea(locationArea)
                    .latitude(latitude)
                    .longitude(longitude)
                    .budget(budget)
                    .build();

            JobRequestDTO request = jobRequestService.createJobRequest(userId, dto, images);
            return ResponseEntity.ok(Map.of("success", true, "data", request, "message", "تم إرسال الطلب بنجاح"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * جلب طلبات المستخدم
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserRequests(
            @PathVariable UUID userId,
            Pageable pageable) {
        try {
            Page<JobRequestDTO> requests = jobRequestService.getUserRequests(userId, pageable);
            return ResponseEntity.ok(Map.of("success", true, "data", requests));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * جلب طلبات العامل
     */
    @GetMapping("/worker/{workerId}")
    public ResponseEntity<?> getWorkerRequests(
            @PathVariable Long workerId,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        try {
            Page<JobRequestDTO> requests;
            if (status != null && !status.isEmpty()) {
                JobRequest.JobStatus jobStatus = JobRequest.JobStatus.valueOf(status.toUpperCase());
                requests = jobRequestService.getWorkerRequestsByStatus(workerId, jobStatus, pageable);
            } else {
                requests = jobRequestService.getWorkerRequests(workerId, pageable);
            }
            return ResponseEntity.ok(Map.of("success", true, "data", requests));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * جلب طلبات العامل باستخدام userId (UUID)
     */
    @GetMapping("/worker/user/{userId}")
    public ResponseEntity<?> getWorkerRequestsByUserId(
            @PathVariable UUID userId,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        try {
            // أولاً نبحث عن العامل باستخدام userId
            Optional<Worker> worker = workerRepository.findByUserId(userId);
            if (worker.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "العامل غير موجود"));
            }

            Page<JobRequestDTO> requests;
            Long workerId = worker.get().getId();
            if (status != null && !status.isEmpty()) {
                JobRequest.JobStatus jobStatus = JobRequest.JobStatus.valueOf(status.toUpperCase());
                requests = jobRequestService.getWorkerRequestsByStatus(workerId, jobStatus, pageable);
            } else {
                requests = jobRequestService.getWorkerRequests(workerId, pageable);
            }
            return ResponseEntity.ok(Map.of("success", true, "data", requests));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * قبول الطلب
     */
    @PostMapping("/{requestId}/accept")
    public ResponseEntity<?> acceptRequest(
            @PathVariable Long requestId,
            @RequestParam Long workerId) {
        try {
            JobRequestDTO request = jobRequestService.acceptRequest(requestId, workerId);
            return ResponseEntity.ok(Map.of("success", true, "data", request, "message", "تم قبول الطلب"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * رفض الطلب
     */
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<?> rejectRequest(
            @PathVariable Long requestId,
            @RequestParam Long workerId,
            @RequestParam(required = false) String reason) {
        try {
            JobRequestDTO request = jobRequestService.rejectRequest(requestId, workerId, reason);
            return ResponseEntity.ok(Map.of("success", true, "data", request, "message", "تم رفض الطلب"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * إرسال عرض سعر
     */
    @PostMapping("/{requestId}/offer")
    public ResponseEntity<?> sendPriceOffer(
            @PathVariable Long requestId,
            @RequestParam Long workerId,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) String message) {
        try {
            JobRequestDTO request = jobRequestService.sendPriceOffer(requestId, workerId, price, message);
            return ResponseEntity.ok(Map.of("success", true, "data", request, "message", "تم إرسال عرض السعر"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * بدء العمل
     */
    @PostMapping("/{requestId}/start")
    public ResponseEntity<?> startWork(
            @PathVariable Long requestId,
            @RequestParam Long workerId) {
        try {
            JobRequestDTO request = jobRequestService.startWork(requestId, workerId);
            return ResponseEntity.ok(Map.of("success", true, "data", request, "message", "تم بدء العمل"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * إكمال العمل
     */
    @PostMapping("/{requestId}/complete")
    public ResponseEntity<?> completeWork(
            @PathVariable Long requestId,
            @RequestParam Long workerId) {
        try {
            JobRequestDTO request = jobRequestService.completeWork(requestId, workerId);
            return ResponseEntity.ok(Map.of("success", true, "data", request, "message", "تم إكمال العمل"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * إلغاء الطلب
     */
    @PostMapping("/{requestId}/cancel")
    public ResponseEntity<?> cancelRequest(
            @PathVariable Long requestId,
            @RequestParam UUID userId) {
        try {
            JobRequestDTO request = jobRequestService.cancelRequest(requestId, userId);
            return ResponseEntity.ok(Map.of("success", true, "data", request, "message", "تم إلغاء الطلب"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * عدد الطلبات قيد الانتظار
     */
    @GetMapping("/worker/{workerId}/pending-count")
    public ResponseEntity<?> getPendingCount(@PathVariable Long workerId) {
        long count = jobRequestService.getPendingCount(workerId);
        return ResponseEntity.ok(Map.of("success", true, "count", count));
    }

    /**
     * عدد الطلبات قيد التنفيذ
     */
    @GetMapping("/worker/{workerId}/in-progress-count")
    public ResponseEntity<?> getInProgressCount(@PathVariable Long workerId) {
        long count = jobRequestService.getInProgressCount(workerId);
        return ResponseEntity.ok(Map.of("success", true, "count", count));
    }

    /**
     * عدد الطلبات المكتملة
     */
    @GetMapping("/worker/{workerId}/completed-count")
    public ResponseEntity<?> getCompletedCount(@PathVariable Long workerId) {
        long count = jobRequestService.getCompletedCount(workerId);
        return ResponseEntity.ok(Map.of("success", true, "count", count));
    }

    /**
     * قبول عرض السعر من جانب العميل
     */
    @PostMapping("/{requestId}/accept-offer")
    public ResponseEntity<?> acceptOffer(
            @PathVariable Long requestId,
            @RequestParam UUID userId) {
        try {
            JobRequestDTO request = jobRequestService.acceptOffer(requestId, userId);
            return ResponseEntity.ok(Map.of("success", true, "data", request, "message", "تم قبول العرض"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * رفض عرض السعر من جانب العميل
     */
    @PostMapping("/{requestId}/reject-offer")
    public ResponseEntity<?> rejectOffer(
            @PathVariable Long requestId,
            @RequestParam UUID userId) {
        try {
            JobRequestDTO request = jobRequestService.rejectOffer(requestId, userId);
            return ResponseEntity.ok(Map.of("success", true, "data", request, "message", "تم رفض العرض"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
