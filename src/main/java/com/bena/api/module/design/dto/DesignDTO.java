package com.bena.api.module.design.dto;

import com.bena.api.module.design.entity.DesignCategory;
import com.bena.api.module.design.entity.DesignStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesignDTO {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;  // URL الصورة فقط
    private DesignCategory category;
    private DesignStyle style;
    private Integer areaInSquareMeters;
    private Double estimatedCost;
    private List<String> materials;
    private List<String> features;
    private Integer viewCount;
    private Boolean isFeatured;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
