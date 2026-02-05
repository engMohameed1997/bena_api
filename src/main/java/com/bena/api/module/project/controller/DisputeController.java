package com.bena.api.module.project.controller;

import com.bena.api.module.project.dto.DisputeCreateRequest;
import com.bena.api.module.project.dto.DisputeResolveRequest;
import com.bena.api.module.project.dto.DisputeResponse;
import com.bena.api.module.project.entity.Dispute;
import com.bena.api.module.project.service.DisputeService;
import com.bena.api.module.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/disputes")
@RequiredArgsConstructor
public class DisputeController {

    private final DisputeService disputeService;

    @PostMapping
    public ResponseEntity<DisputeResponse> createDispute(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DisputeCreateRequest request) {
        Dispute dispute = disputeService.createDispute(
                request.getProjectId(),
                user.getId(),
                request.getTitle(),
                request.getDescription(),
                request.getDisputeType(),
                request.getEvidenceUrls()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(dispute));
    }

    @GetMapping("/{disputeId}")
    public ResponseEntity<DisputeResponse> getDispute(@PathVariable UUID disputeId) {
        Dispute dispute = disputeService.getDisputeById(disputeId);
        return ResponseEntity.ok(mapToResponse(dispute));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<DisputeResponse>> getProjectDisputes(@PathVariable UUID projectId) {
        List<Dispute> disputes = disputeService.getProjectDisputes(projectId);
        List<DisputeResponse> responses = disputes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/my-disputes")
    public ResponseEntity<Page<DisputeResponse>> getMyDisputes(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<DisputeResponse> disputes = disputeService.getUserDisputes(user.getId(), pageable)
                .map(this::mapToResponse);
        return ResponseEntity.ok(disputes);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Page<DisputeResponse>> getAllDisputes(Pageable pageable) {
        Page<DisputeResponse> disputes = disputeService.getAllDisputes(pageable)
                .map(this::mapToResponse);
        return ResponseEntity.ok(disputes);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Page<DisputeResponse>> getDisputesByStatus(
            @PathVariable Dispute.DisputeStatus status,
            Pageable pageable) {
        Page<DisputeResponse> disputes = disputeService.getDisputesByStatus(status, pageable)
                .map(this::mapToResponse);
        return ResponseEntity.ok(disputes);
    }

    @PostMapping("/{disputeId}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DisputeResponse> assignDisputeToAdmin(
            @PathVariable UUID disputeId,
            @AuthenticationPrincipal User admin) {
        Dispute dispute = disputeService.assignDisputeToAdmin(disputeId, admin.getId());
        return ResponseEntity.ok(mapToResponse(dispute));
    }

    @PatchMapping("/{disputeId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DisputeResponse> updateDisputeStatus(
            @PathVariable UUID disputeId,
            @RequestParam Dispute.DisputeStatus status,
            @RequestParam(required = false) String adminNotes) {
        Dispute dispute = disputeService.updateDisputeStatus(disputeId, status, adminNotes);
        return ResponseEntity.ok(mapToResponse(dispute));
    }

    @PostMapping("/{disputeId}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<DisputeResponse> resolveDispute(
            @PathVariable UUID disputeId,
            @Valid @RequestBody DisputeResolveRequest request) {
        Dispute dispute = disputeService.resolveDispute(
                disputeId,
                request.getOutcome(),
                request.getResolutionDetails()
        );
        return ResponseEntity.ok(mapToResponse(dispute));
    }

    @PostMapping("/{disputeId}/add-evidence")
    public ResponseEntity<DisputeResponse> addEvidence(
            @PathVariable UUID disputeId,
            @RequestParam String evidenceUrls) {
        Dispute dispute = disputeService.addEvidenceToDispute(disputeId, evidenceUrls);
        return ResponseEntity.ok(mapToResponse(dispute));
    }

    @GetMapping("/open-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Long> getOpenDisputesCount() {
        Long count = disputeService.getOpenDisputesCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/my-admin-disputes")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<DisputeResponse>> getMyAdminDisputes(@AuthenticationPrincipal User admin) {
        List<Dispute> disputes = disputeService.getAdminDisputes(admin.getId());
        List<DisputeResponse> responses = disputes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private DisputeResponse mapToResponse(Dispute dispute) {
        return DisputeResponse.builder()
                .id(dispute.getId())
                .projectId(dispute.getProject().getId())
                .projectTitle(dispute.getProject().getTitle())
                .raisedById(dispute.getRaisedBy().getId())
                .raisedByName(dispute.getRaisedBy().getFullName())
                .againstId(dispute.getAgainst().getId())
                .againstName(dispute.getAgainst().getFullName())
                .title(dispute.getTitle())
                .description(dispute.getDescription())
                .disputeType(dispute.getDisputeType())
                .status(dispute.getStatus())
                .evidenceUrls(dispute.getEvidenceUrls())
                .assignedAdminId(dispute.getAssignedAdmin() != null ? dispute.getAssignedAdmin().getId() : null)
                .assignedAdminName(dispute.getAssignedAdmin() != null ? dispute.getAssignedAdmin().getFullName() : null)
                .adminNotes(dispute.getAdminNotes())
                .resolutionDetails(dispute.getResolutionDetails())
                .resolutionOutcome(dispute.getResolutionOutcome())
                .paymentHeld(dispute.getPaymentHeld())
                .resolvedAt(dispute.getResolvedAt())
                .createdAt(dispute.getCreatedAt())
                .updatedAt(dispute.getUpdatedAt())
                .build();
    }
}
