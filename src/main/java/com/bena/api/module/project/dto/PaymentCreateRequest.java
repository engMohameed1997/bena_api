package com.bena.api.module.project.dto;

import com.bena.api.module.project.entity.Payment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentCreateRequest {

    @NotNull(message = "معرف المشروع مطلوب")
    private UUID projectId;

    private UUID milestoneId;

    @NotNull(message = "معرف المستلم مطلوب")
    private UUID payeeId;

    @NotNull(message = "المبلغ مطلوب")
    @DecimalMin(value = "0.01", message = "المبلغ يجب أن يكون أكبر من صفر")
    private BigDecimal amount;

    @NotNull(message = "نوع الدفعة مطلوب")
    private Payment.PaymentType paymentType;

    private Payment.PaymentMethod paymentMethod;

    private String notes;
}
