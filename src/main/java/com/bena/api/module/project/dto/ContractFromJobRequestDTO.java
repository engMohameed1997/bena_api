package com.bena.api.module.project.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractFromJobRequestDTO {
    private Long jobRequestId;
    private Long workerId;
    private String title;
    private String projectDescription;
    private String projectLocation;
    private String contractTerms;
    private String paymentTerms;
    private String deliveryTerms;
    private String cancellationPolicy;
    private String startDate;
    private String endDate;
    private BigDecimal totalAmount;
    private Integer warrantyMonths;
}
