package com.bena.api.module.settings.dto;

import com.bena.api.module.settings.entity.FeatureFlag;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO لـ Feature Flag
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeatureFlagDto {

    private Long id;

    @NotBlank(message = "مفتاح الميزة مطلوب")
    @Size(max = 100, message = "مفتاح الميزة يجب أن لا يتجاوز 100 حرف")
    @Pattern(regexp = "^[a-z][a-z0-9.]*[a-z0-9]$", message = "مفتاح الميزة يجب أن يكون بصيغة: feature.sub.name")
    private String featureKey;

    @NotBlank(message = "اسم الميزة مطلوب")
    @Size(max = 200, message = "اسم الميزة يجب أن لا يتجاوز 200 حرف")
    private String name;

    private String description;

    private Boolean isEnabled;

    private Integer rolloutPercentage;

    private String category;

    private String metadata;

    private UUID updatedBy;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    public static FeatureFlagDto from(FeatureFlag flag) {
        return FeatureFlagDto.builder()
                .id(flag.getId())
                .featureKey(flag.getFeatureKey())
                .name(flag.getName())
                .description(flag.getDescription())
                .isEnabled(flag.getIsEnabled())
                .rolloutPercentage(flag.getRolloutPercentage())
                .category(flag.getCategory())
                .metadata(flag.getMetadata())
                .updatedBy(flag.getUpdatedBy())
                .createdAt(flag.getCreatedAt())
                .updatedAt(flag.getUpdatedAt())
                .build();
    }
}
