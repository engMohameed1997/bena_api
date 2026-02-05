package com.bena.api.module.worker.dto;

import com.bena.api.module.worker.entity.JobRequestOffer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO لعرض السعر
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRequestOfferDTO {
    
    private Long id;
    private Long jobRequestId;
    private String offeredBy;
    private BigDecimal offeredPrice;
    private Integer estimatedDurationDays;
    private LocalDateTime proposedStartDate;
    private String offerNotes;
    private String paymentTerms;
    private String warrantyTerms;
    private String status;
    private Long counterToOfferId;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // معلومات إضافية للعرض
    private String offeredByName; // اسم من قدم العرض
    private Boolean isLatestOffer; // هل هذا آخر عرض
    private Boolean canRespond; // هل يمكن الرد على هذا العرض
}
