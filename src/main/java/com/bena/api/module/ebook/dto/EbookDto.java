package com.bena.api.module.ebook.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EbookDto {

    private UUID id;
    private String title;
    private String description;
    private String coverUrl;
    private String category;
    private BigDecimal price;
    private String currency;
    private Boolean isFeatured;
    private Integer totalPurchases;
    private LocalDateTime publishDate;
    
    // معلومات الناشر
    private UUID publisherId;
    private String publisherName;
    private String publisherAvatar;
    
    // هل المستخدم الحالي اشترى الكتاب
    private Boolean isPurchased;
    
    // هل المستخدم الحالي هو ناشر الكتاب
    private Boolean isOwner;
}
