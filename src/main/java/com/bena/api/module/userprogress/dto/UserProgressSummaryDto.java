package com.bena.api.module.userprogress.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgressSummaryDto {
    private int totalSteps;
    private int completedSteps;
    private double progressPercentage;
    private BigDecimal totalCost;
    private int stepsWithNotes;
    private int stepsWithCosts;
}
