package com.bena.api.module.worker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO لإنشاء تقييم جديد
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerReviewCreateDTO {
    private String reviewerName;
    private Integer rating; // 1-5
    private String comment;
}
