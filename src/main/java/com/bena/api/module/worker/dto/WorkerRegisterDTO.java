package com.bena.api.module.worker.dto;

import com.bena.api.module.worker.entity.WorkerCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerRegisterDTO {
    private String name;
    private String email;
    private String password;
    private WorkerCategory category;
    private String description;
    private String phoneNumber;
    private String whatsappNumber;
    private Integer experienceYears;
    
    // الموقع
    private String city;
    private String area;
    private Double latitude;
    private Double longitude;
    
    // الأسعار
    private BigDecimal pricePerMeter;
    private BigDecimal pricePerDay;
    private BigDecimal pricePerVisit;
    
    // إضافي
    private Boolean worksAtNight;
    private Integer estimatedCompletionDays;
}
