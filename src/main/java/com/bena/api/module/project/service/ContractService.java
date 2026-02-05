package com.bena.api.module.project.service;

import com.bena.api.module.project.dto.ContractResponse;
import com.bena.api.module.project.entity.Contract;
import com.bena.api.module.project.entity.Project;
import com.bena.api.module.project.repository.ContractRepository;
import com.bena.api.module.project.repository.ProjectRepository;
import com.bena.api.module.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public Page<ContractResponse> getAllContracts(Pageable pageable) {
        return contractRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ContractResponse getContractByIdForAdmin(UUID id) { // Renamed to avoid conflict
        return contractRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("العقد غير موجود"));
    }

    private ContractResponse toResponse(Contract contract) {
        return ContractResponse.builder()
                .id(contract.getId())
                .projectId(contract.getProject() != null ? contract.getProject().getId() : null)
                .clientId(contract.getClient() != null ? contract.getClient().getId() : null)
                .providerId(contract.getProvider() != null ? contract.getProvider().getId() : null)
                .clientName(contract.getClient() != null ? contract.getClient().getFullName() : "")
                .providerName(contract.getProvider() != null ? contract.getProvider().getFullName() : "")
                .contractTerms(contract.getContractTerms())
                .status(contract.getStatus())
                .createdAt(contract.getCreatedAt())
                .build();
    }

    @Transactional
    public Contract createContract(UUID projectId, String contractTerms, String paymentTerms, 
                                   String deliveryTerms, String cancellationPolicy) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("المشروع غير موجود"));

        Contract contract = Contract.builder()
                .project(project)
                .client(project.getClient())
                .provider(project.getProvider())
                .contractTerms(contractTerms)
                .paymentTerms(paymentTerms)
                .deliveryTerms(deliveryTerms)
                .cancellationPolicy(cancellationPolicy)
                .status(Contract.ContractStatus.DRAFT)
                .build();

        return contractRepository.save(contract);
    }

    @Transactional(readOnly = true)
    public Contract getContractById(UUID contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("العقد غير موجود"));
    }

    @Transactional(readOnly = true)
    public Contract getContractByProjectId(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("المشروع غير موجود"));
        return contractRepository.findByProject(project)
                .orElseThrow(() -> new RuntimeException("العقد غير موجود"));
    }

    @Transactional(readOnly = true)
    public List<Contract> getUserContracts(User user) {
        return contractRepository.findByClientOrProvider(user);
    }

    @Transactional
    public Contract signContract(UUID contractId, User user, String ipAddress) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("العقد غير موجود"));

        if (contract.getClient().getId().equals(user.getId())) {
            contract.setClientSigned(true);
            contract.setClientSignedAt(LocalDateTime.now());
            contract.setClientIpAddress(ipAddress);
        } else if (contract.getProvider().getId().equals(user.getId())) {
            contract.setProviderSigned(true);
            contract.setProviderSignedAt(LocalDateTime.now());
            contract.setProviderIpAddress(ipAddress);
        } else {
            throw new RuntimeException("ليس لديك صلاحية التوقيع على هذا العقد");
        }

        if (contract.isFullySigned()) {
            contract.setStatus(Contract.ContractStatus.ACTIVE);
            contract.setContractStartDate(LocalDateTime.now());
        } else {
            contract.setStatus(Contract.ContractStatus.PENDING_SIGNATURE);
        }

        return contractRepository.save(contract);
    }

    @Transactional
    public Contract updateContractStatus(UUID contractId, Contract.ContractStatus newStatus) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("العقد غير موجود"));

        contract.setStatus(newStatus);
        
        if (newStatus == Contract.ContractStatus.COMPLETED || 
            newStatus == Contract.ContractStatus.TERMINATED) {
            contract.setContractEndDate(LocalDateTime.now());
        }

        return contractRepository.save(contract);
    }

    @Transactional
    public Contract updateContract(UUID contractId, String contractTerms, String paymentTerms,
                                   String deliveryTerms, String cancellationPolicy) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("العقد غير موجود"));

        if (contract.getStatus() != Contract.ContractStatus.DRAFT) {
            throw new RuntimeException("لا يمكن تعديل عقد موقع");
        }

        if (contractTerms != null) contract.setContractTerms(contractTerms);
        if (paymentTerms != null) contract.setPaymentTerms(paymentTerms);
        if (deliveryTerms != null) contract.setDeliveryTerms(deliveryTerms);
        if (cancellationPolicy != null) contract.setCancellationPolicy(cancellationPolicy);

        return contractRepository.save(contract);
    }

    /**
     * إنشاء عقد من طلب عمل - بدون الحاجة لمشروع موجود مسبقاً
     */
    @Transactional
    public Contract createContractFromJobRequest(
            User client,
            User provider,
            String title,
            String projectDescription,
            String projectLocation,
            String contractTerms,
            String paymentTerms,
            String deliveryTerms,
            String cancellationPolicy,
            String startDateStr,
            String endDateStr,
            BigDecimal totalAmount,
            Integer warrantyMonths,
            Long jobRequestId
    ) {
        // إنشاء مشروع جديد تلقائياً
        Project project = Project.builder()
                .title(title != null ? title : "عقد عمل")
                .description(projectDescription)
                .client(client)
                .provider(provider)
                .projectType(Project.ProjectType.CONSTRUCTION)
                .status(Project.ProjectStatus.ACCEPTED)
                .totalBudget(totalAmount != null ? totalAmount : java.math.BigDecimal.ZERO)
                .build();
        project = projectRepository.save(project);

        // تحويل التواريخ
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if (startDateStr != null && !startDateStr.isEmpty()) {
            try {
                startDate = LocalDate.parse(startDateStr.substring(0, 10)).atStartOfDay();
            } catch (Exception e) {
                // تجاهل الخطأ
            }
        }
        if (endDateStr != null && !endDateStr.isEmpty()) {
            try {
                endDate = LocalDate.parse(endDateStr.substring(0, 10)).atStartOfDay();
            } catch (Exception e) {
                // تجاهل الخطأ
            }
        }

        // إنشاء العقد
        Contract contract = Contract.builder()
                .project(project)
                .client(client)
                .provider(provider)
                .contractTerms(contractTerms)
                .paymentTerms(paymentTerms)
                .deliveryTerms(deliveryTerms)
                .cancellationPolicy(cancellationPolicy)
                .contractStartDate(startDate)
                .contractEndDate(endDate)
                .clientSigned(true)  // صاحب المنزل وقّع تلقائياً لأنه من أنشأ العقد
                .clientSignedAt(LocalDateTime.now())
                .providerSigned(false)
                .status(Contract.ContractStatus.PENDING_SIGNATURE)
                .notes("عقد منشأ من طلب عمل رقم: " + jobRequestId + (warrantyMonths != null ? "\nفترة الضمان: " + warrantyMonths + " شهر" : ""))
                .build();

        return contractRepository.save(contract);
    }
}
