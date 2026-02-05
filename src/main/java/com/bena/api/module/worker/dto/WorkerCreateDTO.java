package com.bena.api.module.worker.dto;

import com.bena.api.module.worker.entity.WorkerCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO لإنشاء عامل جديد
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerCreateDTO {
    private String name;
    private WorkerCategory category;
    private String description;
    private String phoneNumber;
    private String whatsappNumber;
    private Integer experienceYears;
    private String location;
    private Boolean isFeatured;
}
