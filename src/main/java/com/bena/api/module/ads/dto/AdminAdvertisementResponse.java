package com.bena.api.module.ads.dto;

import com.bena.api.module.ads.enums.AdSection;
import com.bena.api.module.ads.enums.AdTargetType;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class AdminAdvertisementResponse {
    private UUID id;
    private String title;
    private String imageUrl;
    private AdTargetType targetType;
    private String targetValue;
    private Set<AdSection> sections;
    private Boolean active;
    private Integer priority;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
