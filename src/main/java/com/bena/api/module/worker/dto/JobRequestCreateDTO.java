package com.bena.api.module.worker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRequestCreateDTO {
    private Long workerId;
    private String jobType;
    private String description;
    private String locationCity;
    private String locationArea;
    private Double latitude;
    private Double longitude;
    private BigDecimal budget;
}
