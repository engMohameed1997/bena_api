package com.bena.api.module.worker.controller;

import com.bena.api.module.worker.dto.JobRequestOfferDTO;
import com.bena.api.module.worker.service.JobRequestOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller لإدارة عروض الأسعار والتفاوض
 */
@RestController
@RequestMapping("/v1/job-requests/offers")
@RequiredArgsConstructor
public class JobRequestOfferController {

    private final JobRequestOfferService offerService;

    /**
     * إرسال عرض من المختص
     */
    @PostMapping("/worker")
    public ResponseEntity<?> sendWorkerOffer(
            @RequestParam Long jobRequestId,
            @RequestParam Long workerId,
            @RequestParam BigDecimal offeredPrice,
            @RequestParam(required = false) Integer estimatedDurationDays,
            @RequestParam(required = false) String proposedStartDate,
            @RequestParam(required = false) String offerNotes,
            @RequestParam(required = false) String paymentTerms,
            @RequestParam(required = false) String warrantyTerms) {
        try {
            LocalDateTime startDate = proposedStartDate != null ? 
                LocalDateTime.parse(proposedStartDate) : null;
            
            JobRequestOfferDTO offer = offerService.sendWorkerOffer(
                jobRequestId, workerId, offeredPrice, estimatedDurationDays,
                startDate, offerNotes, paymentTerms, warrantyTerms
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", offer,
                "message", "تم إرسال العرض بنجاح"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * إرسال عرض مضاد من صاحب المنزل
     */
    @PostMapping("/counter")
    public ResponseEntity<?> sendCounterOffer(
            @RequestParam Long jobRequestId,
            @RequestParam UUID userId,
            @RequestParam Long counterToOfferId,
            @RequestParam BigDecimal offeredPrice,
            @RequestParam(required = false) Integer estimatedDurationDays,
            @RequestParam(required = false) String proposedStartDate,
            @RequestParam(required = false) String offerNotes,
            @RequestParam(required = false) String paymentTerms) {
        try {
            LocalDateTime startDate = proposedStartDate != null ? 
                LocalDateTime.parse(proposedStartDate) : null;
            
            JobRequestOfferDTO offer = offerService.sendCounterOffer(
                jobRequestId, userId, counterToOfferId, offeredPrice,
                estimatedDurationDays, startDate, offerNotes, paymentTerms
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", offer,
                "message", "تم إرسال العرض المضاد بنجاح"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * قبول عرض
     */
    @PostMapping("/{offerId}/accept")
    public ResponseEntity<?> acceptOffer(
            @PathVariable Long offerId,
            @RequestParam UUID userId) {
        try {
            JobRequestOfferDTO offer = offerService.acceptOffer(offerId, userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", offer,
                "message", "تم قبول العرض بنجاح"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * رفض عرض
     */
    @PostMapping("/{offerId}/reject")
    public ResponseEntity<?> rejectOffer(
            @PathVariable Long offerId,
            @RequestParam UUID userId,
            @RequestParam(required = false) String rejectionReason) {
        try {
            JobRequestOfferDTO offer = offerService.rejectOffer(offerId, userId, rejectionReason);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", offer,
                "message", "تم رفض العرض"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * الحصول على جميع العروض لطلب معين
     */
    @GetMapping("/job-request/{jobRequestId}")
    public ResponseEntity<?> getOffersByJobRequest(
            @PathVariable Long jobRequestId,
            @RequestParam UUID userId) {
        try {
            List<JobRequestOfferDTO> offers = offerService.getOffersByJobRequest(jobRequestId, userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", offers
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * الحصول على آخر عرض لطلب معين
     */
    @GetMapping("/job-request/{jobRequestId}/latest")
    public ResponseEntity<?> getLatestOffer(@PathVariable Long jobRequestId) {
        try {
            JobRequestOfferDTO offer = offerService.getLatestOffer(jobRequestId);
            
            if (offer == null) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(),
                    "message", "لا توجد عروض"
                ));
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", offer
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
