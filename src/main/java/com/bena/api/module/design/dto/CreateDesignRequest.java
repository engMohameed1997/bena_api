package com.bena.api.module.design.dto;

import com.bena.api.module.design.entity.DesignCategory;
import com.bena.api.module.design.entity.DesignStyle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDesignRequest {

    @NotBlank(message = "العنوان مطلوب")
    private String title;

    private String description;

    private String imageUrl;

    private byte[] imageData;

    private String imageType;

    @NotNull(message = "نوع التصميم مطلوب")
    private DesignCategory category;

    @NotNull(message = "نمط التصميم مطلوب")
    private DesignStyle style;

    private Integer areaInSquareMeters;

    private Double estimatedCost;

    private List<String> materials;

    private List<String> features;

    private Boolean isFeatured;
}
