package com.bena.api.module.project.dto;

import com.bena.api.module.project.entity.Dispute;
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
public class DisputeResponse {

    private UUID id;
    private UUID projectId;
    private String projectTitle;
    private UUID raisedById;
    private String raisedByName;
    private UUID againstId;
    private String againstName;
    private String title;
    private String description;
    private Dispute.DisputeType disputeType;
    private Dispute.DisputeStatus status;
    private String evidenceUrls;
    private UUID assignedAdminId;
    private String assignedAdminName;
    private String adminNotes;
    private String resolutionDetails;
    private Dispute.ResolutionOutcome resolutionOutcome;
    private Boolean paymentHeld;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
