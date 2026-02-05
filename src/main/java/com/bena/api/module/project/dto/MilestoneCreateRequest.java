package com.bena.api.module.project.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MilestoneCreateRequest {

    @NotBlank(message = "عنوان المرحلة مطلوب")
    @Size(max = 200, message = "عنوان المرحلة يجب أن لا يتجاوز 200 حرف")
    private String title;

    @Size(max = 2000, message = "الوصف يجب أن لا يتجاوز 2000 حرف")
    private String description;

    @NotNull(message = "ترتيب المرحلة مطلوب")
    @Min(value = 1, message = "ترتيب المرحلة يجب أن يكون 1 على الأقل")
    private Integer milestoneOrder;

    @NotNull(message = "مبلغ المرحلة مطلوب")
    @DecimalMin(value = "0.01", message = "المبلغ يجب أن يكون أكبر من صفر")
    private BigDecimal amount;

    private LocalDateTime expectedCompletionDate;

    private String notes;
}
