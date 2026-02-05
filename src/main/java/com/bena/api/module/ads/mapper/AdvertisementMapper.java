package com.bena.api.module.ads.mapper;

import com.bena.api.module.ads.dto.AdminAdvertisementResponse;
import com.bena.api.module.ads.dto.AdvertisementResponse;
import com.bena.api.module.ads.entity.Advertisement;

public class AdvertisementMapper {

    public AdvertisementResponse toPublicResponse(Advertisement ad) {
        return AdvertisementResponse.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .imageUrl(ad.getImageUrl())
                .targetType(ad.getTargetType())
                .targetValue(ad.getTargetValue())
                .sections(ad.getSections())
                .active(ad.getActive())
                .priority(ad.getPriority())
                .startAt(ad.getStartAt())
                .endAt(ad.getEndAt())
                .createdAt(ad.getCreatedAt())
                .build();
    }

    public AdminAdvertisementResponse toAdminResponse(Advertisement ad) {
        return AdminAdvertisementResponse.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .imageUrl(ad.getImageUrl())
                .targetType(ad.getTargetType())
                .targetValue(ad.getTargetValue())
                .sections(ad.getSections())
                .active(ad.getActive())
                .priority(ad.getPriority())
                .startAt(ad.getStartAt())
                .endAt(ad.getEndAt())
                .createdAt(ad.getCreatedAt())
                .updatedAt(ad.getUpdatedAt())
                .build();
    }
}
