package com.bena.api.module.worker.dto;

import com.bena.api.module.worker.entity.WorkerMedia.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO لوسائط العامل
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerMediaDTO {
    private Long id;
    private MediaType mediaType;
    private String mediaUrl;
    private String thumbnailUrl;
    private String externalUrl;
    private String caption;
    private Integer displayOrder;
    private LocalDateTime createdAt;
}
