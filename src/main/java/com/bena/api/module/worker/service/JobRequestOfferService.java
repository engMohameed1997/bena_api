package com.bena.api.module.worker.service;

import com.bena.api.module.worker.dto.JobRequestOfferDTO;
import com.bena.api.module.worker.entity.JobRequest;
import com.bena.api.module.worker.entity.JobRequestOffer;
import com.bena.api.module.worker.entity.Worker;
import com.bena.api.module.worker.repository.JobRequestOfferRepository;
import com.bena.api.module.worker.repository.JobRequestRepository;
import com.bena.api.module.worker.repository.WorkerRepository;
import com.bena.api.module.project.service.ContractCreationService;

import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * خدمة إدارة عروض الأسعار والتفاوض
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobRequestOfferService {

    private final JobRequestOfferRepository offerRepository;
    private final JobRequestRepository jobRequestRepository;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;

    /**
     * إرسال عرض من المختص
     */
    @Transactional
    public JobRequestOfferDTO sendWorkerOffer(Long jobRequestId, Long workerId, 
                                              BigDecimal offeredPrice, 
                                              Integer estimatedDurationDays,
                                              LocalDateTime proposedStartDate,
                                              String offerNotes,
                                              String paymentTerms,
                                              String warrantyTerms) {
        
        JobRequest jobRequest = jobRequestRepository.findById(jobRequestId)
                .orElseThrow(() -> new RuntimeException("الطلب غير موجود"));
        
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("المختص غير موجود"));
        
        // التحقق من أن المختص هو المستهدف بالطلب
        if (!jobRequest.getWorker().getId().equals(workerId)) {
            throw new RuntimeException("هذا الطلب ليس موجهاً لك");
        }
        
        // إلغاء العروض السابقة المعلقة من المختص
        List<JobRequestOffer> pendingOffers = offerRepository.findByJobRequestIdAndStatus(
            jobRequestId, JobRequestOffer.OfferStatus.PENDING);
        
        for (JobRequestOffer offer : pendingOffers) {
            if (offer.getOfferedBy() == JobRequestOffer.OfferedBy.WORKER) {
                offer.setStatus(JobRequestOffer.OfferStatus.EXPIRED);
                offerRepository.save(offer);
            }
        }
        
        // إنشاء عرض جديد
        JobRequestOffer offer = JobRequestOffer.builder()
                .jobRequest(jobRequest)
                .offeredBy(JobRequestOffer.OfferedBy.WORKER)
                .offeredPrice(offeredPrice)
                .estimatedDurationDays(estimatedDurationDays)
                .proposedStartDate(proposedStartDate)
                .offerNotes(offerNotes)
                .paymentTerms(paymentTerms)
                .warrantyTerms(warrantyTerms)
                .status(JobRequestOffer.OfferStatus.PENDING)
                .build();
        
        offer = offerRepository.save(offer);
        
        // تحديث حالة الطلب
        jobRequest.setStatus(JobRequest.JobStatus.OFFER_SENT);
        jobRequestRepository.save(jobRequest);
        
        log.info("Worker {} sent offer for job request {}", workerId, jobRequestId);
        
        return toDTO(offer, worker.getName());
    }

    /**
     * إرسال عرض مضاد من صاحب المنزل
     */
    @Transactional
    public JobRequestOfferDTO sendCounterOffer(Long jobRequestId, UUID userId,
                                               Long counterToOfferId,
                                               BigDecimal offeredPrice,
                                               Integer estimatedDurationDays,
                                               LocalDateTime proposedStartDate,
                                               String offerNotes,
                                               String paymentTerms) {
        
        JobRequest jobRequest = jobRequestRepository.findById(jobRequestId)
                .orElseThrow(() -> new RuntimeException("الطلب غير موجود"));
        
        // التحقق من الملكية
        if (!jobRequest.getUserId().equals(userId)) {
            throw new RuntimeException("غير مصرح لك بهذا الإجراء");
        }
        
        // التحقق من وجود العرض الأصلي
        JobRequestOffer originalOffer = offerRepository.findById(counterToOfferId)
                .orElseThrow(() -> new RuntimeException("العرض الأصلي غير موجود"));
        
        if (!originalOffer.getJobRequest().getId().equals(jobRequestId)) {
            throw new RuntimeException("العرض لا ينتمي لهذا الطلب");
        }
        
        // تحديث حالة العرض الأصلي
        originalOffer.setStatus(JobRequestOffer.OfferStatus.COUNTERED);
        offerRepository.save(originalOffer);
        
        // إنشاء عرض مضاد
        JobRequestOffer counterOffer = JobRequestOffer.builder()
                .jobRequest(jobRequest)
                .offeredBy(JobRequestOffer.OfferedBy.HOMEOWNER)
                .offeredPrice(offeredPrice)
                .estimatedDurationDays(estimatedDurationDays)
                .proposedStartDate(proposedStartDate)
                .offerNotes(offerNotes)
                .paymentTerms(paymentTerms)
                .status(JobRequestOffer.OfferStatus.PENDING)
                .counterToOffer(originalOffer)
                .build();
        
        counterOffer = offerRepository.save(counterOffer);
        
        log.info("Homeowner {} sent counter offer for job request {}", userId, jobRequestId);
        
        return toDTO(counterOffer, "صاحب المنزل");
    }

    /**
     * قبول عرض
     */
    @Transactional
    public JobRequestOfferDTO acceptOffer(Long offerId, UUID userId) {
        
        JobRequestOffer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("العرض غير موجود"));
        
        JobRequest jobRequest = offer.getJobRequest();
        
        // التحقق من الملكية
        if (!jobRequest.getUserId().equals(userId)) {
            throw new RuntimeException("غير مصرح لك بهذا الإجراء");
        }
        
        // التحقق من حالة العرض
        if (offer.getStatus() != JobRequestOffer.OfferStatus.PENDING) {
            throw new RuntimeException("هذا العرض غير متاح للقبول");
        }
        
        // قبول العرض
        offer.setStatus(JobRequestOffer.OfferStatus.ACCEPTED);
        offer = offerRepository.save(offer);
        
        // تحديث حالة الطلب
        jobRequest.setStatus(JobRequest.JobStatus.ACCEPTED);
        jobRequest.setAcceptedOfferId(offer.getId());
        jobRequestRepository.save(jobRequest);
        
        // إلغاء العروض الأخرى المعلقة
        List<JobRequestOffer> otherOffers = offerRepository.findByJobRequestIdAndStatus(
            jobRequest.getId(), JobRequestOffer.OfferStatus.PENDING);
        
        for (JobRequestOffer otherOffer : otherOffers) {
            if (!otherOffer.getId().equals(offerId)) {
                otherOffer.setStatus(JobRequestOffer.OfferStatus.EXPIRED);
                offerRepository.save(otherOffer);
            }
        }
        
        log.info("Offer {} accepted for job request {}", offerId, jobRequest.getId());
        
        // TODO: إرسال إشعار FCM للعميل بضرورة إنشاء عقد
        
        return toDTO(offer, getOfferOwnerName(offer));
    }

    /**
     * رفض عرض
     */
    @Transactional
    public JobRequestOfferDTO rejectOffer(Long offerId, UUID userId, String rejectionReason) {
        
        JobRequestOffer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("العرض غير موجود"));
        
        JobRequest jobRequest = offer.getJobRequest();
        
        // التحقق من الملكية
        if (!jobRequest.getUserId().equals(userId)) {
            throw new RuntimeException("غير مصرح لك بهذا الإجراء");
        }
        
        // رفض العرض
        offer.setStatus(JobRequestOffer.OfferStatus.REJECTED);
        offer.setRejectionReason(rejectionReason);
        offer = offerRepository.save(offer);
        
        // تحديث حالة الطلب إلى PENDING للسماح بعروض جديدة
        jobRequest.setStatus(JobRequest.JobStatus.PENDING);
        jobRequestRepository.save(jobRequest);
        
        log.info("Offer {} rejected for job request {}", offerId, jobRequest.getId());
        
        return toDTO(offer, getOfferOwnerName(offer));
    }

    /**
     * الحصول على جميع العروض لطلب معين
     */
    @Transactional(readOnly = true)
    public List<JobRequestOfferDTO> getOffersByJobRequest(Long jobRequestId, UUID userId) {
        
        JobRequest jobRequest = jobRequestRepository.findById(jobRequestId)
                .orElseThrow(() -> new RuntimeException("الطلب غير موجود"));
        
        // التحقق من الصلاحية (صاحب الطلب أو المختص)
        Worker worker = jobRequest.getWorker();
        if (!jobRequest.getUserId().equals(userId) && !worker.getUserId().equals(userId)) {
            throw new RuntimeException("غير مصرح لك بعرض هذه العروض");
        }
        
        List<JobRequestOffer> offers = offerRepository.findByJobRequestIdOrderByCreatedAtDesc(jobRequestId);
        
        return offers.stream()
                .map(offer -> toDTO(offer, getOfferOwnerName(offer)))
                .collect(Collectors.toList());
    }

    /**
     * الحصول على آخر عرض لطلب معين
     */
    @Transactional(readOnly = true)
    public JobRequestOfferDTO getLatestOffer(Long jobRequestId) {
        
        return offerRepository.findLatestOfferByJobRequestId(jobRequestId)
                .map(offer -> toDTO(offer, getOfferOwnerName(offer)))
                .orElse(null);
    }

    /**
     * تحويل Entity إلى DTO
     */
    private JobRequestOfferDTO toDTO(JobRequestOffer offer, String offeredByName) {
        
        // التحقق من أن هذا آخر عرض
        JobRequestOffer latestOffer = offerRepository.findLatestOfferByJobRequestId(
            offer.getJobRequest().getId()).orElse(null);
        
        boolean isLatest = latestOffer != null && latestOffer.getId().equals(offer.getId());
        boolean canRespond = offer.getStatus() == JobRequestOffer.OfferStatus.PENDING && isLatest;
        
        return JobRequestOfferDTO.builder()
                .id(offer.getId())
                .jobRequestId(offer.getJobRequest().getId())
                .offeredBy(offer.getOfferedBy().name())
                .offeredByName(offeredByName)
                .offeredPrice(offer.getOfferedPrice())
                .estimatedDurationDays(offer.getEstimatedDurationDays())
                .proposedStartDate(offer.getProposedStartDate())
                .offerNotes(offer.getOfferNotes())
                .paymentTerms(offer.getPaymentTerms())
                .warrantyTerms(offer.getWarrantyTerms())
                .status(offer.getStatus().name())
                .counterToOfferId(offer.getCounterToOffer() != null ? offer.getCounterToOffer().getId() : null)
                .rejectionReason(offer.getRejectionReason())
                .isLatestOffer(isLatest)
                .canRespond(canRespond)
                .createdAt(offer.getCreatedAt())
                .updatedAt(offer.getUpdatedAt())
                .build();
    }

    /**
     * الحصول على اسم مقدم العرض
     */
    private String getOfferOwnerName(JobRequestOffer offer) {
        if (offer.getOfferedBy() == JobRequestOffer.OfferedBy.WORKER) {
            return offer.getJobRequest().getWorker().getName();
        } else {
            return "صاحب المنزل";
        }
    }
}
