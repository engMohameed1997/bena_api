package com.bena.api.module.project.dto;

import com.bena.api.module.project.entity.Escrow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscrowResponse {

    private UUID id;
    private UUID projectId;
    private String projectTitle;
    private UUID milestoneId;
    private String milestoneTitle;
    private UUID payerId;
    private String payerName;
    private UUID payeeId;
    private String payeeName;
    private BigDecimal amount;
    private BigDecimal heldAmount;
    private BigDecimal releasedAmount;
    private BigDecimal refundedAmount;
    private BigDecimal remainingAmount;
    private Escrow.EscrowStatus status;
    private LocalDateTime heldAt;
    private LocalDateTime releaseScheduledAt;
    private LocalDateTime releasedAt;
    private LocalDateTime refundedAt;
    private String releaseReason;
    private String refundReason;
    private Boolean autoReleaseEnabled;
    private Integer autoReleaseDays;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
