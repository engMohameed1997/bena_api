package com.bena.api.module.userprogress.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StepProgressRequest {
    private Long stepId;
    private String stepTitle;
    private Boolean isCompleted;
    private OffsetDateTime completedAt;
    private String notes;
    private BigDecimal actualCost;
}
