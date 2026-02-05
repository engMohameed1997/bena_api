package com.bena.api.module.project.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class EscrowCreateRequest {

    @NotNull(message = "معرف المشروع مطلوب")
    private UUID projectId;

    private UUID milestoneId;

    @NotNull(message = "معرف المستلم مطلوب")
    private UUID payeeId;

    @NotNull(message = "المبلغ مطلوب")
    @DecimalMin(value = "0.01", message = "المبلغ يجب أن يكون أكبر من صفر")
    private BigDecimal amount;

    @Min(value = 1, message = "عدد أيام الإطلاق التلقائي يجب أن يكون يوم واحد على الأقل")
    private Integer autoReleaseDays;

    private String notes;
}
