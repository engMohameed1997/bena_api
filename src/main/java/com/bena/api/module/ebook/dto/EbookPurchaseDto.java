package com.bena.api.module.ebook.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EbookPurchaseDto {

    private UUID id;
    private UUID ebookId;
    private String ebookTitle;
    private String ebookCoverUrl;
    private BigDecimal amountPaid;
    private String currency;
    private LocalDateTime purchasedAt;
    private LocalDateTime lastOpenedAt;
    private Integer lastPage;
}
