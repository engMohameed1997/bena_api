package com.bena.api.module.worker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO لتقييم العامل
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerReviewDTO {
    private Long id;
    private Long workerId;
    private String reviewerName;
    private Integer rating;
    private String comment;
    private Boolean isApproved;
    private LocalDateTime createdAt;
}
