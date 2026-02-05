package com.bena.api.module.ebook.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EbookFilterRequest {

    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String search;
    private String sortBy; // price, date, purchases
    private String sortDirection; // asc, desc
    
    @Builder.Default
    private Integer page = 0;
    
    @Builder.Default
    private Integer size = 20;
}
