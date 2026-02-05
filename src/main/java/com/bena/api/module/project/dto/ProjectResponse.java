package com.bena.api.module.project.dto;

import com.bena.api.module.project.entity.Project;
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
public class ProjectResponse {

    private UUID id;
    private UUID clientId;
    private String clientName;
    private UUID providerId;
    private String providerName;
    private String title;
    private String description;
    private Project.ProjectType projectType;
    private Project.ProjectStatus status;
    private BigDecimal totalBudget;
    private BigDecimal platformCommissionPercentage;
    private BigDecimal platformCommissionAmount;
    private BigDecimal providerAmount;
    private String locationCity;
    private String locationArea;
    private Double latitude;
    private Double longitude;
    private LocalDateTime startDate;
    private LocalDateTime expectedEndDate;
    private LocalDateTime actualEndDate;
    private Integer clientRating;
    private String clientReview;
    private Integer providerRating;
    private String providerReview;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
