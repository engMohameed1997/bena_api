package com.bena.api.module.consultation.dto;

import com.bena.api.module.consultation.entity.ConsultationItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsultationItemResponse {

    private UUID id;
    private String code;
    private String categoryCode;
    private String categoryNameAr;
    
    private String nameAr;
    private String nameEn;
    private String descriptionAr;
    private String descriptionEn;

    // الأسعار
    private BigDecimal priceFrom;
    private BigDecimal priceTo;
    private String priceUnit;
    private String currency;
    private String priceRange; // نص مجمع للسعر

    // المميزات والعيوب
    private List<String> advantages;
    private List<String> disadvantages;
    private List<String> suitableUses;
    private List<String> tips;

    // المواصفات
    private Map<String, Object> specifications;

    // الوسائط
    private List<String> imageUrls;
    private String videoUrl;

    // الإحصائيات
    private BigDecimal rating;
    private Long viewCount;
    private Boolean isFeatured;

    public static ConsultationItemResponse from(ConsultationItem item) {
        String priceRange = null;
        if (item.getPriceFrom() != null || item.getPriceTo() != null) {
            if (item.getPriceFrom() != null && item.getPriceTo() != null) {
                priceRange = String.format("%s - %s %s %s",
                        formatNumber(item.getPriceFrom()),
                        formatNumber(item.getPriceTo()),
                        item.getCurrency(),
                        item.getPriceUnit() != null ? item.getPriceUnit() : "");
            } else if (item.getPriceFrom() != null) {
                priceRange = String.format("من %s %s %s",
                        formatNumber(item.getPriceFrom()),
                        item.getCurrency(),
                        item.getPriceUnit() != null ? item.getPriceUnit() : "");
            }
        }

        return ConsultationItemResponse.builder()
                .id(item.getId())
                .code(item.getCode())
                .categoryCode(item.getCategory() != null ? item.getCategory().getCode() : null)
                .categoryNameAr(item.getCategory() != null ? item.getCategory().getNameAr() : null)
                .nameAr(item.getNameAr())
                .nameEn(item.getNameEn())
                .descriptionAr(item.getDescriptionAr())
                .descriptionEn(item.getDescriptionEn())
                .priceFrom(item.getPriceFrom())
                .priceTo(item.getPriceTo())
                .priceUnit(item.getPriceUnit())
                .currency(item.getCurrency())
                .priceRange(priceRange)
                .advantages(item.getAdvantages())
                .disadvantages(item.getDisadvantages())
                .suitableUses(item.getSuitableUses())
                .tips(item.getTips())
                .specifications(item.getSpecifications())
                .imageUrls(item.getImageUrls())
                .videoUrl(item.getVideoUrl())
                .rating(item.getRating())
                .viewCount(item.getViewCount())
                .isFeatured(item.getIsFeatured())
                .build();
    }

    private static String formatNumber(BigDecimal number) {
        if (number == null) return "";
        return String.format("%,.0f", number);
    }
}
