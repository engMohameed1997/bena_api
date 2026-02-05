package com.bena.api.module.ads.dto;

import com.bena.api.module.ads.enums.AdSection;
import com.bena.api.module.ads.enums.AdTargetType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class UpdateAdvertisementRequest {

    @Size(max = 200, message = "العنوان طويل جداً")
    private String title;

    @Size(max = 500, message = "رابط الصورة طويل جداً")
    private String imageUrl;

    private AdTargetType targetType;

    @Size(max = 500, message = "قيمة الهدف طويلة جداً")
    private String targetValue;

    private Set<AdSection> sections;

    private Boolean active;

    @Min(value = 0, message = "الأولوية يجب أن تكون 0 أو أكثر")
    private Integer priority;

    private OffsetDateTime startAt;

    private OffsetDateTime endAt;
}
