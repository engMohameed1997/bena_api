package com.bena.api.module.consultation.dto;

import com.bena.api.module.consultation.entity.ConsultationCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsultationCategoryResponse {

    private UUID id;
    private String code;
    private String nameAr;
    private String nameEn;
    private String descriptionAr;
    private String descriptionEn;
    private String iconUrl;
    private Integer displayOrder;
    private Long itemCount;

    public static ConsultationCategoryResponse from(ConsultationCategory category) {
        return ConsultationCategoryResponse.builder()
                .id(category.getId())
                .code(category.getCode())
                .nameAr(category.getNameAr())
                .nameEn(category.getNameEn())
                .descriptionAr(category.getDescriptionAr())
                .descriptionEn(category.getDescriptionEn())
                .iconUrl(category.getIconUrl())
                .displayOrder(category.getDisplayOrder())
                .build();
    }

    public static ConsultationCategoryResponse from(ConsultationCategory category, Long itemCount) {
        ConsultationCategoryResponse response = from(category);
        response.setItemCount(itemCount);
        return response;
    }
}
