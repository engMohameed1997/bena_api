package com.bena.api.module.project.controller;

import com.bena.api.module.project.dto.PaymentCreateRequest;
import com.bena.api.module.project.dto.PaymentResponse;
import com.bena.api.module.project.entity.Payment;
import com.bena.api.module.project.service.ProjectPaymentService;
import com.bena.api.module.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class ProjectPaymentController {

    private final ProjectPaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PaymentCreateRequest request) {
        Payment payment = paymentService.createPayment(
                request.getProjectId(),
                request.getMilestoneId(),
                user.getId(),
                request.getPayeeId(),
                request.getAmount(),
                request.getPaymentType()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(payment));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID paymentId) {
        Payment payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(mapToResponse(payment));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<PaymentResponse>> getProjectPayments(@PathVariable UUID projectId) {
        List<Payment> payments = paymentService.getProjectPayments(projectId);
        List<PaymentResponse> responses = payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/my-payments")
    public ResponseEntity<Page<PaymentResponse>> getMyPayments(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<PaymentResponse> payments = paymentService.getUserPayments(user.getId(), pageable)
                .map(this::mapToResponse);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{paymentId}/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable UUID paymentId,
            @RequestParam String transactionId,
            @RequestParam String paymentGateway,
            @RequestParam Payment.PaymentMethod paymentMethod) {
        Payment payment = paymentService.processPayment(paymentId, transactionId, paymentGateway, paymentMethod);
        return ResponseEntity.ok(mapToResponse(payment));
    }

    @PostMapping("/{paymentId}/complete")
    public ResponseEntity<PaymentResponse> completePayment(@PathVariable UUID paymentId) {
        Payment payment = paymentService.completePayment(paymentId);
        return ResponseEntity.ok(mapToResponse(payment));
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable UUID paymentId,
            @RequestParam BigDecimal refundAmount,
            @RequestParam String refundReason) {
        Payment payment = paymentService.refundPayment(paymentId, refundAmount, refundReason);
        return ResponseEntity.ok(mapToResponse(payment));
    }

    @PostMapping("/{paymentId}/fail")
    public ResponseEntity<PaymentResponse> failPayment(
            @PathVariable UUID paymentId,
            @RequestParam String reason) {
        Payment payment = paymentService.failPayment(paymentId, reason);
        return ResponseEntity.ok(mapToResponse(payment));
    }

    @GetMapping("/project/{projectId}/total")
    public ResponseEntity<BigDecimal> getTotalPaidAmount(@PathVariable UUID projectId) {
        BigDecimal total = paymentService.getTotalPaidAmount(projectId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/platform-fees")
    public ResponseEntity<BigDecimal> getTotalPlatformFees() {
        BigDecimal total = paymentService.getTotalPlatformFees();
        return ResponseEntity.ok(total);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .projectId(payment.getProject().getId())
                .projectTitle(payment.getProject().getTitle())
                .milestoneId(payment.getMilestone() != null ? payment.getMilestone().getId() : null)
                .milestoneTitle(payment.getMilestone() != null ? payment.getMilestone().getTitle() : null)
                .payerId(payment.getPayer().getId())
                .payerName(payment.getPayer().getFullName())
                .payeeId(payment.getPayee().getId())
                .payeeName(payment.getPayee().getFullName())
                .amount(payment.getAmount())
                .platformFee(payment.getPlatformFee())
                .netAmount(payment.getNetAmount())
                .paymentType(payment.getPaymentType())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .paymentGateway(payment.getPaymentGateway())
                .paymentDate(payment.getPaymentDate())
                .refundAmount(payment.getRefundAmount())
                .refundDate(payment.getRefundDate())
                .refundReason(payment.getRefundReason())
                .notes(payment.getNotes())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
