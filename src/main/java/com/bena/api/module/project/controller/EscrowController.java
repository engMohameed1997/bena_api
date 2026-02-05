package com.bena.api.module.project.controller;

import com.bena.api.module.project.dto.EscrowCreateRequest;
import com.bena.api.module.project.dto.EscrowResponse;
import com.bena.api.module.project.entity.Escrow;
import com.bena.api.module.project.service.EscrowService;
import com.bena.api.module.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/escrow")
@RequiredArgsConstructor
public class EscrowController {

    private final EscrowService escrowService;

    @PostMapping
    public ResponseEntity<EscrowResponse> createEscrow(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody EscrowCreateRequest request) {
        Escrow escrow = escrowService.createEscrow(
                request.getProjectId(),
                request.getMilestoneId(),
                user.getId(),
                request.getPayeeId(),
                request.getAmount(),
                request.getAutoReleaseDays()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(escrow));
    }

    @GetMapping("/{escrowId}")
    public ResponseEntity<EscrowResponse> getEscrow(@PathVariable UUID escrowId) {
        Escrow escrow = escrowService.getEscrowById(escrowId);
        return ResponseEntity.ok(mapToResponse(escrow));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<EscrowResponse>> getProjectEscrows(@PathVariable UUID projectId) {
        List<Escrow> escrows = escrowService.getProjectEscrows(projectId);
        List<EscrowResponse> responses = escrows.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{escrowId}/release")
    public ResponseEntity<EscrowResponse> releaseEscrow(
            @PathVariable UUID escrowId,
            @RequestParam BigDecimal releaseAmount,
            @RequestParam(required = false) String releaseReason) {
        Escrow escrow = escrowService.releaseEscrow(escrowId, releaseAmount, releaseReason);
        return ResponseEntity.ok(mapToResponse(escrow));
    }

    @PostMapping("/{escrowId}/refund")
    public ResponseEntity<EscrowResponse> refundEscrow(
            @PathVariable UUID escrowId,
            @RequestParam BigDecimal refundAmount,
            @RequestParam String refundReason) {
        Escrow escrow = escrowService.refundEscrow(escrowId, refundAmount, refundReason);
        return ResponseEntity.ok(mapToResponse(escrow));
    }

    @PostMapping("/{escrowId}/hold-for-dispute")
    public ResponseEntity<EscrowResponse> holdEscrowForDispute(@PathVariable UUID escrowId) {
        Escrow escrow = escrowService.holdEscrowForDispute(escrowId);
        return ResponseEntity.ok(mapToResponse(escrow));
    }

    @GetMapping("/total-held")
    public ResponseEntity<BigDecimal> getTotalHeldAmount() {
        BigDecimal total = escrowService.getTotalHeldAmount();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/project/{projectId}/released")
    public ResponseEntity<BigDecimal> getProjectReleasedAmount(@PathVariable UUID projectId) {
        BigDecimal total = escrowService.getProjectReleasedAmount(projectId);
        return ResponseEntity.ok(total);
    }

    private EscrowResponse mapToResponse(Escrow escrow) {
        return EscrowResponse.builder()
                .id(escrow.getId())
                .projectId(escrow.getProject().getId())
                .projectTitle(escrow.getProject().getTitle())
                .milestoneId(escrow.getMilestone() != null ? escrow.getMilestone().getId() : null)
                .milestoneTitle(escrow.getMilestone() != null ? escrow.getMilestone().getTitle() : null)
                .payerId(escrow.getPayer().getId())
                .payerName(escrow.getPayer().getFullName())
                .payeeId(escrow.getPayee().getId())
                .payeeName(escrow.getPayee().getFullName())
                .amount(escrow.getAmount())
                .heldAmount(escrow.getHeldAmount())
                .releasedAmount(escrow.getReleasedAmount())
                .refundedAmount(escrow.getRefundedAmount())
                .remainingAmount(escrow.getRemainingAmount())
                .status(escrow.getStatus())
                .heldAt(escrow.getHeldAt())
                .releaseScheduledAt(escrow.getReleaseScheduledAt())
                .releasedAt(escrow.getReleasedAt())
                .refundedAt(escrow.getRefundedAt())
                .releaseReason(escrow.getReleaseReason())
                .refundReason(escrow.getRefundReason())
                .autoReleaseEnabled(escrow.getAutoReleaseEnabled())
                .autoReleaseDays(escrow.getAutoReleaseDays())
                .notes(escrow.getNotes())
                .createdAt(escrow.getCreatedAt())
                .updatedAt(escrow.getUpdatedAt())
                .build();
    }
}
