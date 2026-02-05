package com.bena.api.module.project.dto;

import com.bena.api.module.project.entity.Contract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponse {

    private UUID id;
    private UUID projectId;
    private String projectTitle;
    private UUID clientId;
    private String clientName;
    private UUID providerId;
    private String providerName;
    private String contractTerms;
    private String paymentTerms;
    private String deliveryTerms;
    private String cancellationPolicy;
    private Boolean clientSigned;
    private LocalDateTime clientSignedAt;
    private Boolean providerSigned;
    private LocalDateTime providerSignedAt;
    private Contract.ContractStatus status;
    private LocalDateTime contractStartDate;
    private LocalDateTime contractEndDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
