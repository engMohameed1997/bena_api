package com.bena.api.module.ebook.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublisherStatsDto {

    private Long totalBooks;
    private Long totalSales;
    private BigDecimal totalEarnings;
    private String currency;
}
