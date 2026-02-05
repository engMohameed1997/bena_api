package com.bena.api.module.project.dto;

import com.bena.api.module.project.entity.Payment;
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
public class PaymentResponse {

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
    private BigDecimal platformFee;
    private BigDecimal netAmount;
    private Payment.PaymentType paymentType;
    private Payment.PaymentStatus status;
    private Payment.PaymentMethod paymentMethod;
    private String transactionId;
    private String paymentGateway;
    private LocalDateTime paymentDate;
    private BigDecimal refundAmount;
    private LocalDateTime refundDate;
    private String refundReason;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
