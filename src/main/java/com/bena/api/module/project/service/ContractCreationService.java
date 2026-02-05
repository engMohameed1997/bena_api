package com.bena.api.module.project.service;

import com.bena.api.module.project.entity.Contract;
import com.bena.api.module.project.entity.Project;
import com.bena.api.module.project.repository.ContractRepository;
import com.bena.api.module.project.repository.ProjectRepository;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.repository.UserRepository;
import com.bena.api.module.worker.entity.JobRequest;
import com.bena.api.module.worker.entity.JobRequestOffer;
import com.bena.api.module.worker.entity.Worker;
import com.bena.api.module.worker.repository.JobRequestOfferRepository;
import com.bena.api.module.worker.repository.JobRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * خدمة إنشاء العقود والمشاريع تلقائياً بعد قبول العرض
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContractCreationService {

    private final ProjectRepository projectRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final JobRequestRepository jobRequestRepository;
    private final JobRequestOfferRepository offerRepository;

    /**
     * إنشاء مشروع وعقد من طلب عمل مقبول
     */
    @Transactional
    public Contract createContractFromJobRequest(Long jobRequestId, Long acceptedOfferId) {
        
        // الحصول على الطلب والعرض المقبول
        JobRequest jobRequest = jobRequestRepository.findById(jobRequestId)
                .orElseThrow(() -> new RuntimeException("الطلب غير موجود"));
        
        JobRequestOffer acceptedOffer = offerRepository.findById(acceptedOfferId)
                .orElseThrow(() -> new RuntimeException("العرض غير موجود"));
        
        // التحقق من أن العرض مقبول
        if (acceptedOffer.getStatus() != JobRequestOffer.OfferStatus.ACCEPTED) {
            throw new RuntimeException("العرض غير مقبول");
        }
        
        // الحصول على المستخدمين
        User client = userRepository.findById(jobRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("العميل غير موجود"));
        
        Worker worker = jobRequest.getWorker();
        User provider = userRepository.findById(worker.getUserId())
                .orElseThrow(() -> new RuntimeException("المختص غير موجود"));
        
        // إنشاء المشروع
        Project project = createProject(jobRequest, acceptedOffer, client, provider);
        
        // إنشاء العقد
        Contract contract = createContract(project, jobRequest, acceptedOffer, client, provider);
        
        // تحديث حالة الطلب
        jobRequest.setStatus(JobRequest.JobStatus.IN_PROGRESS);
        jobRequestRepository.save(jobRequest);
        
        log.info("Created contract {} and project {} from job request {}", 
                contract.getId(), project.getId(), jobRequestId);
        
        return contract;
    }

    /**
     * إنشاء المشروع
     */
    private Project createProject(JobRequest jobRequest, JobRequestOffer acceptedOffer, 
                                  User client, User provider) {
        
        BigDecimal totalBudget = acceptedOffer.getOfferedPrice();
        BigDecimal commissionPercentage = new BigDecimal("10.00");
        BigDecimal commissionAmount = totalBudget.multiply(commissionPercentage)
                .divide(new BigDecimal("100"));
        BigDecimal providerAmount = totalBudget.subtract(commissionAmount);
        
        Project project = Project.builder()
                .client(client)
                .provider(provider)
                .title(jobRequest.getJobType())
                .description(buildProjectDescription(jobRequest, acceptedOffer))
                .projectType(Project.ProjectType.CONSTRUCTION)
                .status(Project.ProjectStatus.PENDING)
                .totalBudget(totalBudget)
                .platformCommissionPercentage(commissionPercentage)
                .platformCommissionAmount(commissionAmount)
                .providerAmount(providerAmount)
                .locationCity(jobRequest.getLocationCity())
                .locationArea(jobRequest.getLocationArea())
                .latitude(jobRequest.getLatitude())
                .longitude(jobRequest.getLongitude())
                .startDate(acceptedOffer.getProposedStartDate())
                .expectedEndDate(calculateExpectedEndDate(acceptedOffer))
                .build();
        
        return projectRepository.save(project);
    }

    /**
     * إنشاء العقد
     */
    private Contract createContract(Project project, JobRequest jobRequest, 
                                    JobRequestOffer acceptedOffer, User client, User provider) {
        
        String contractTerms = buildContractTerms(jobRequest, acceptedOffer);
        String paymentTerms = acceptedOffer.getPaymentTerms() != null ? 
                acceptedOffer.getPaymentTerms() : buildDefaultPaymentTerms(acceptedOffer);
        String deliveryTerms = buildDeliveryTerms(acceptedOffer);
        String cancellationPolicy = buildCancellationPolicy();
        
        Contract contract = Contract.builder()
                .project(project)
                .client(client)
                .provider(provider)
                .contractTerms(contractTerms)
                .paymentTerms(paymentTerms)
                .deliveryTerms(deliveryTerms)
                .cancellationPolicy(cancellationPolicy)
                .status(Contract.ContractStatus.PENDING_SIGNATURE)
                .contractStartDate(acceptedOffer.getProposedStartDate())
                .contractEndDate(calculateExpectedEndDate(acceptedOffer))
                .notes(acceptedOffer.getOfferNotes())
                .build();
        
        return contractRepository.save(contract);
    }

    /**
     * بناء وصف المشروع
     */
    private String buildProjectDescription(JobRequest jobRequest, JobRequestOffer acceptedOffer) {
        StringBuilder desc = new StringBuilder();
        desc.append("نوع العمل: ").append(jobRequest.getJobType()).append("\n\n");
        
        if (jobRequest.getDescription() != null) {
            desc.append("الوصف: ").append(jobRequest.getDescription()).append("\n\n");
        }
        
        if (acceptedOffer.getOfferNotes() != null) {
            desc.append("ملاحظات المختص: ").append(acceptedOffer.getOfferNotes()).append("\n\n");
        }
        
        desc.append("المبلغ المتفق عليه: ").append(acceptedOffer.getOfferedPrice()).append(" دينار\n");
        
        if (acceptedOffer.getEstimatedDurationDays() != null) {
            desc.append("مدة التنفيذ المتوقعة: ").append(acceptedOffer.getEstimatedDurationDays()).append(" يوم\n");
        }
        
        return desc.toString();
    }

    /**
     * بناء شروط العقد
     */
    private String buildContractTerms(JobRequest jobRequest, JobRequestOffer acceptedOffer) {
        StringBuilder terms = new StringBuilder();
        
        terms.append("شروط العقد\n");
        terms.append("==========\n\n");
        
        terms.append("1. نطاق العمل:\n");
        terms.append("   - نوع العمل: ").append(jobRequest.getJobType()).append("\n");
        terms.append("   - الموقع: ").append(jobRequest.getLocationCity())
             .append(" - ").append(jobRequest.getLocationArea()).append("\n\n");
        
        terms.append("2. المبلغ المالي:\n");
        terms.append("   - المبلغ الإجمالي: ").append(acceptedOffer.getOfferedPrice()).append(" دينار\n");
        terms.append("   - عمولة المنصة: 10%\n\n");
        
        if (acceptedOffer.getEstimatedDurationDays() != null) {
            terms.append("3. مدة التنفيذ:\n");
            terms.append("   - المدة المتوقعة: ").append(acceptedOffer.getEstimatedDurationDays()).append(" يوم\n\n");
        }
        
        if (acceptedOffer.getWarrantyTerms() != null) {
            terms.append("4. الضمانات:\n");
            terms.append("   ").append(acceptedOffer.getWarrantyTerms()).append("\n\n");
        }
        
        terms.append("5. الالتزامات:\n");
        terms.append("   - يلتزم المختص بإنجاز العمل وفق المواصفات المتفق عليها\n");
        terms.append("   - يلتزم العميل بالدفع وفق الشروط المحددة\n");
        terms.append("   - يتم حل أي نزاع من خلال نظام المنصة\n\n");
        
        return terms.toString();
    }

    /**
     * بناء شروط الدفع الافتراضية
     */
    private String buildDefaultPaymentTerms(JobRequestOffer acceptedOffer) {
        return "شروط الدفع:\n" +
               "- دفعة مقدمة: 30% عند بدء العمل\n" +
               "- دفعة ثانية: 40% عند إنجاز 50% من العمل\n" +
               "- الدفعة النهائية: 30% عند إتمام العمل\n" +
               "- يتم الدفع من خلال نظام الضمان (Escrow) في المنصة";
    }

    /**
     * بناء شروط التسليم
     */
    private String buildDeliveryTerms(JobRequestOffer acceptedOffer) {
        StringBuilder terms = new StringBuilder();
        terms.append("شروط التسليم:\n");
        
        if (acceptedOffer.getProposedStartDate() != null) {
            terms.append("- تاريخ البدء: ").append(acceptedOffer.getProposedStartDate()).append("\n");
        }
        
        if (acceptedOffer.getEstimatedDurationDays() != null) {
            terms.append("- مدة التنفيذ: ").append(acceptedOffer.getEstimatedDurationDays()).append(" يوم\n");
        }
        
        terms.append("- يتم التسليم على مراحل حسب الاتفاق\n");
        terms.append("- يحق للعميل طلب تعديلات معقولة\n");
        
        return terms.toString();
    }

    /**
     * بناء سياسة الإلغاء
     */
    private String buildCancellationPolicy() {
        return "سياسة الإلغاء:\n" +
               "- يمكن إلغاء العقد من قبل أي طرف بإشعار مسبق 7 أيام\n" +
               "- في حالة الإلغاء، يتم احتساب قيمة العمل المنجز\n" +
               "- يتم استرداد المبالغ المدفوعة بعد خصم قيمة العمل المنجز\n" +
               "- في حالة النزاع، يتم الرجوع لنظام فض الإبلاغات في المنصة";
    }

    /**
     * حساب تاريخ الانتهاء المتوقع
     */
    private LocalDateTime calculateExpectedEndDate(JobRequestOffer acceptedOffer) {
        LocalDateTime startDate = acceptedOffer.getProposedStartDate();
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
        
        Integer durationDays = acceptedOffer.getEstimatedDurationDays();
        if (durationDays == null) {
            durationDays = 30; // افتراضي 30 يوم
        }
        
        return startDate.plusDays(durationDays);
    }
}
