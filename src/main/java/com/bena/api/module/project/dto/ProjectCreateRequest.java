package com.bena.api.module.project.dto;

import com.bena.api.module.project.entity.Project;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ProjectCreateRequest {

    @NotNull(message = "معرف المختص مطلوب")
    private UUID providerId;

    @NotBlank(message = "عنوان المشروع مطلوب")
    @Size(max = 200, message = "عنوان المشروع يجب أن لا يتجاوز 200 حرف")
    private String title;

    @Size(max = 5000, message = "الوصف يجب أن لا يتجاوز 5000 حرف")
    private String description;

    @NotNull(message = "نوع المشروع مطلوب")
    private Project.ProjectType projectType;

    @NotNull(message = "الميزانية الإجمالية مطلوبة")
    @DecimalMin(value = "0.01", message = "الميزانية يجب أن تكون أكبر من صفر")
    private BigDecimal totalBudget;

    @DecimalMin(value = "0.00", message = "نسبة العمولة يجب أن تكون صفر أو أكبر")
    @DecimalMax(value = "100.00", message = "نسبة العمولة يجب أن لا تتجاوز 100")
    private BigDecimal platformCommissionPercentage;

    private String locationCity;

    private String locationArea;

    private Double latitude;

    private Double longitude;

    private LocalDateTime expectedEndDate;
}
