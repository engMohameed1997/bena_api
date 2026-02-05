package com.bena.api.module.worker.repository;

public interface ConsultationSpecialistProjection {

    Long getId();

    String getName();

    String getCategory();

    String getProfileImageUrl();

    byte[] getProfileImage();

    String getProfileImageType();

    Integer getExperienceYears();

    Integer getSpecializedExperienceYears();

    String getSpecialization();

    String getCity();

    String getArea();

    Double getAverageRating();

    Integer getReviewCount();

    Boolean getIsFeatured();

    Boolean getIsOnline();
}
