package com.bena.api.module.worker.dto;

import com.bena.api.module.worker.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Long id;
    private Long reporterId;
    private Long workerId;
    private String workerName;
    private Report.ReportType reportType;
    private String reportTypeArabic;
    private String description;
    private Report.ReportStatus status;
    private String statusArabic;
    private LocalDateTime createdAt;
    
    public String getReportTypeArabic() {
        if (reportType == null) return "";
        return switch (reportType) {
            case WRONG_NUMBER -> "رقم خاطئ";
            case FRAUD -> "نصب";
            case OFFENSIVE -> "محتوى مسيء";
            case UNPROFESSIONAL -> "عامل غير مهني";
        };
    }
    
    public String getStatusArabic() {
        if (status == null) return "";
        return switch (status) {
            case PENDING -> "قيد المراجعة";
            case REVIEWED -> "تمت المراجعة";
            case RESOLVED -> "تم الحل";
            case DISMISSED -> "مرفوض";
        };
    }
}
