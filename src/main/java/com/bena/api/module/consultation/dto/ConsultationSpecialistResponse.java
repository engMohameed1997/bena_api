package com.bena.api.module.consultation.dto;

import com.bena.api.module.worker.entity.WorkerCategory;
import com.bena.api.module.worker.repository.ConsultationSpecialistProjection;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsultationSpecialistResponse {

    private Long id;
    private String name;

    private String category;
    private String categoryArabicName;

    private String specialization;
    private Integer experienceYears;
    private Integer specializedExperienceYears;

    private String profileImageUrl;

    private String city;
    private String area;

    private Double averageRating;
    private Integer reviewCount;

    private Boolean isFeatured;
    private Boolean isOnline;

    public static ConsultationSpecialistResponse from(ConsultationSpecialistProjection p) {
        String imageUrl = p.getProfileImageUrl();
        if (imageUrl == null && p.getProfileImage() != null && p.getProfileImage().length > 0) {
            String contentType = p.getProfileImageType() != null ? p.getProfileImageType() : "image/jpeg";
            imageUrl = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(p.getProfileImage());
        }

        String categoryArabicName = null;
        if (p.getCategory() != null) {
            try {
                categoryArabicName = WorkerCategory.valueOf(p.getCategory()).getArabicName();
            } catch (Exception ignored) {
                categoryArabicName = null;
            }
        }

        return ConsultationSpecialistResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .category(p.getCategory())
                .categoryArabicName(categoryArabicName)
                .specialization(p.getSpecialization())
                .experienceYears(p.getExperienceYears())
                .specializedExperienceYears(p.getSpecializedExperienceYears())
                .profileImageUrl(imageUrl)
                .city(p.getCity())
                .area(p.getArea())
                .averageRating(p.getAverageRating())
                .reviewCount(p.getReviewCount())
                .isFeatured(p.getIsFeatured())
                .isOnline(p.getIsOnline())
                .build();
    }
}
