package com.bena.api.module.project.dto;

import com.bena.api.module.project.entity.Bid;
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
public class BidResponse {

    private UUID id;
    private UUID clientId;
    private String clientName;
    private UUID providerId;
    private String providerName;
    private String title;
    private String description;
    private Bid.ServiceType serviceType;
    private BigDecimal offeredPrice;
    private Integer estimatedDurationDays;
    private String proposalDetails;
    private Bid.BidStatus status;
    private String clientResponse;
    private LocalDateTime responseDate;
    private UUID convertedToProjectId;
    private String locationCity;
    private String locationArea;
    private Double latitude;
    private Double longitude;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
