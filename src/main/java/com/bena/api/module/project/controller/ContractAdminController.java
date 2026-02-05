package com.bena.api.module.project.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.project.dto.ContractResponse;
import com.bena.api.module.project.entity.Contract;
import com.bena.api.module.project.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller لإدارة العقود (Admin Only)
 */
@RestController
@RequestMapping("/v1/admin/contracts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ContractAdminController {

    private final ContractService contractService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ContractResponse>>> getAllContracts(
            @PageableDefault(sort = "createdAt") Pageable pageable
    ) {
        Page<ContractResponse> contracts = contractService.getAllContracts(pageable);
        return ResponseEntity.ok(ApiResponse.success(contracts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContractResponse>> getContractById(@PathVariable UUID id) {
        ContractResponse contract = contractService.getContractByIdForAdmin(id);
        return ResponseEntity.ok(ApiResponse.success(contract));
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ContractResponse>> updateContractStatus(
            @PathVariable UUID id,
            @RequestParam Contract.ContractStatus status
    ) {
        Contract contract = contractService.updateContractStatus(id, status);
        ContractResponse response = contractService.getContractByIdForAdmin(id);
        return ResponseEntity.ok(ApiResponse.success(response, "تم تحديث حالة العقد بنجاح"));
    }
}
