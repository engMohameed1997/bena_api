package com.bena.api.module.project.controller;

import com.bena.api.module.project.dto.ContractCreateRequest;
import com.bena.api.module.project.dto.ContractResponse;
import com.bena.api.module.project.dto.ContractFromJobRequestDTO;
import com.bena.api.module.project.entity.Contract;
import com.bena.api.module.project.service.ContractService;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.repository.UserRepository;
import com.bena.api.module.worker.entity.Worker;
import com.bena.api.module.worker.repository.WorkerRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/contracts")
@RequiredArgsConstructor
public class ContractController {


    private final ContractService contractService;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createContract(
            @Valid @RequestBody ContractCreateRequest request,
            @AuthenticationPrincipal User user) {
        try {
            Contract contract = contractService.createContract(
                    request.getProjectId(),
                    request.getContractTerms(),
                    request.getPaymentTerms(),
                    request.getDeliveryTerms(),
                    request.getCancellationPolicy()
            );
            
            // TODO: إرسال إشعار FCM للمقاول بضرورة مراجعة العقد
            
            return ResponseEntity.status(HttpStatus.CREATED).body(java.util.Map.of(
                "success", true,
                "data", mapToResponse(contract),
                "message", "تم إنشاء العقد بنجاح وإرساله للطرف الثاني للموافقة"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/{contractId}")
    public ResponseEntity<?> getContract(@PathVariable UUID contractId) {
        try {
            Contract contract = contractService.getContractById(contractId);
            return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "data", mapToResponse(contract)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> getContractByProject(@PathVariable UUID projectId) {
        try {
            Contract contract = contractService.getContractByProjectId(projectId);
            return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "data", mapToResponse(contract)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/my-contracts")
    public ResponseEntity<?> getMyContracts(@AuthenticationPrincipal User user) {
        try {
            List<Contract> contracts = contractService.getUserContracts(user);
            List<ContractResponse> responses = contracts.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "data", responses
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/{contractId}/sign")
    public ResponseEntity<?> signContract(
            @PathVariable UUID contractId,
            @AuthenticationPrincipal User user,
            HttpServletRequest request) {
        try {
            String ipAddress = request.getRemoteAddr();
            Contract contract = contractService.signContract(contractId, user, ipAddress);
            
            
            // TODO: إرسال إشعار FCM للطرف الآخر بالتوقيع
            
            return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "data", mapToResponse(contract),
                "message", contract.isFullySigned() ? "تم توقيع العقد بنجاح. العقد الآن نشط" : "تم التوقيع بنجاح. في انتظار توقيع الطرف الآخر"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{contractId}")
    public ResponseEntity<ContractResponse> updateContract(
            @PathVariable UUID contractId,
            @Valid @RequestBody ContractCreateRequest request) {
        Contract contract = contractService.updateContract(
                contractId,
                request.getContractTerms(),
                request.getPaymentTerms(),
                request.getDeliveryTerms(),
                request.getCancellationPolicy()
        );
        return ResponseEntity.ok(mapToResponse(contract));
    }

    @PatchMapping("/{contractId}/status")
    public ResponseEntity<ContractResponse> updateContractStatus(
            @PathVariable UUID contractId,
            @RequestParam Contract.ContractStatus status) {
        Contract contract = contractService.updateContractStatus(contractId, status);
        return ResponseEntity.ok(mapToResponse(contract));
    }

    /**
     * إنشاء عقد من طلب عمل - بعد قبول العرض
     */
    @PostMapping("/from-job-request")
    public ResponseEntity<?> createContractFromJobRequest(
            @RequestBody ContractFromJobRequestDTO request,
            @AuthenticationPrincipal User user) {
        try {
            // جلب بيانات العامل
            Worker worker = workerRepository.findById(request.getWorkerId())
                    .orElseThrow(() -> new RuntimeException("العامل غير موجود"));
            
            // جلب المستخدم المرتبط بالعامل
            User providerUser = userRepository.findById(worker.getUserId())
                    .orElseThrow(() -> new RuntimeException("مستخدم العامل غير موجود"));
            
            // إنشاء العقد
            Contract contract = contractService.createContractFromJobRequest(
                    user,
                    providerUser,
                    request.getTitle(),
                    request.getProjectDescription(),
                    request.getProjectLocation(),
                    request.getContractTerms(),
                    request.getPaymentTerms(),
                    request.getDeliveryTerms(),
                    request.getCancellationPolicy(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getTotalAmount(),
                    request.getWarrantyMonths(),
                    request.getJobRequestId()
            );
            
            // TODO: إرسال إشعار FCM للمختص بإنشاء عقد جديد
            
            return ResponseEntity.status(HttpStatus.CREATED).body(java.util.Map.of(
                "success", true,
                "data", mapToSimpleResponse(contract),
                "message", "تم إنشاء العقد وإرساله للمختص للموافقة"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    private ContractResponse mapToResponse(Contract contract) {
        return ContractResponse.builder()
                .id(contract.getId())
                .projectId(contract.getProject().getId())
                .projectTitle(contract.getProject().getTitle())
                .clientId(contract.getClient().getId())
                .clientName(contract.getClient().getFullName())
                .providerId(contract.getProvider().getId())
                .providerName(contract.getProvider().getFullName())
                .contractTerms(contract.getContractTerms())
                .paymentTerms(contract.getPaymentTerms())
                .deliveryTerms(contract.getDeliveryTerms())
                .cancellationPolicy(contract.getCancellationPolicy())
                .clientSigned(contract.getClientSigned())
                .clientSignedAt(contract.getClientSignedAt())
                .providerSigned(contract.getProviderSigned())
                .providerSignedAt(contract.getProviderSignedAt())
                .status(contract.getStatus())
                .contractStartDate(contract.getContractStartDate())
                .contractEndDate(contract.getContractEndDate())
                .notes(contract.getNotes())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .build();
    }

    private java.util.Map<String, Object> mapToSimpleResponse(Contract contract) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("id", contract.getId().toString());
        response.put("title", contract.getProject() != null ? contract.getProject().getTitle() : "عقد عمل");
        response.put("clientName", contract.getClient().getFullName());
        response.put("providerName", contract.getProvider().getFullName());
        response.put("contractTerms", contract.getContractTerms());
        response.put("paymentTerms", contract.getPaymentTerms());
        response.put("deliveryTerms", contract.getDeliveryTerms());
        response.put("status", contract.getStatus().name());
        response.put("clientSigned", contract.getClientSigned());
        response.put("providerSigned", contract.getProviderSigned());
        response.put("createdAt", contract.getCreatedAt() != null ? contract.getCreatedAt().toString() : null);
        return response;
    }
}
