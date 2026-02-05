package com.bena.api.module.ads.dto;

import com.bena.api.module.ads.enums.AdSection;
import com.bena.api.module.ads.enums.AdTargetType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class CreateAdvertisementRequest {

    @NotBlank(message = "العنوان مطلوب")
    @Size(max = 200, message = "العنوان طويل جداً")
    private String title;

    @NotBlank(message = "رابط الصورة مطلوب")
    @Size(max = 500, message = "رابط الصورة طويل جداً")
    private String imageUrl;

    @NotNull(message = "نوع الهدف مطلوب")
    private AdTargetType targetType;

    @NotBlank(message = "قيمة الهدف مطلوبة")
    @Size(max = 500, message = "قيمة الهدف طويلة جداً")
    private String targetValue;

    @NotNull(message = "الأقسام مطلوبة")
    @Size(min = 1, message = "يجب تحديد قسم واحد على الأقل")
    private Set<AdSection> sections;

    private Boolean active = true;

    @NotNull(message = "الأولوية مطلوبة")
    @Min(value = 0, message = "الأولوية يجب أن تكون 0 أو أكثر")
    private Integer priority = 0;

    private OffsetDateTime startAt;

    private OffsetDateTime endAt;
}
