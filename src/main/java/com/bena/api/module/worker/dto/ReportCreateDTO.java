package com.bena.api.module.worker.dto;

import com.bena.api.module.worker.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportCreateDTO {
    private Long workerId;
    private Report.ReportType reportType;
    private String description;
}
