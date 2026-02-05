package com.bena.api.module.worker.dto;

import com.bena.api.module.worker.entity.WorkerCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO لعرض بيانات العامل
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerDTO {
    private Long id;
    private String name;
    private WorkerCategory category;
    private String categoryArabicName;
    private String description;
    private String phoneNumber;
    private String whatsappNumber;
    private String profileImageUrl;
    private Double averageRating;
    private Integer reviewCount;
    private Boolean isFeatured;
    private Boolean isActive;
    private Integer experienceYears;
    private String location;
    
    // الموقع الجغرافي
    private String city;
    private String area;
    private Double latitude;
    private Double longitude;
    
    // الأسعار
    private BigDecimal pricePerMeter;
    private BigDecimal pricePerDay;
    private BigDecimal pricePerVisit;
    
    // معلومات إضافية
    private Boolean worksAtNight;
    private Integer estimatedCompletionDays;
    private String email;
    
    private List<WorkerMediaDTO> mediaGallery;
    private LocalDateTime createdAt;
}
