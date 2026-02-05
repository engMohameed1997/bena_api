package com.bena.api.module.worker.dto;

import com.bena.api.module.worker.entity.WorkerCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO لعرض الفئات مع عدد العمال
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCountDTO {
    private WorkerCategory category;
    private String arabicName;
    private Long count;
}
