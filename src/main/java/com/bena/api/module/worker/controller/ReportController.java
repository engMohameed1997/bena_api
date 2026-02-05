package com.bena.api.module.worker.controller;

import com.bena.api.module.worker.dto.ReportCreateDTO;
import com.bena.api.module.worker.dto.ReportDTO;
import com.bena.api.module.worker.entity.Report;
import com.bena.api.module.worker.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * إنشاء بلاغ جديد
     */
    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestParam Long reporterId,
            @RequestParam Long workerId,
            @RequestParam String reportType,
            @RequestParam(required = false) String description) {
        try {
            ReportCreateDTO dto = ReportCreateDTO.builder()
                    .workerId(workerId)
                    .reportType(Report.ReportType.valueOf(reportType.toUpperCase()))
                    .description(description)
                    .build();

            ReportDTO report = reportService.createReport(reporterId, dto);
            return ResponseEntity.ok(Map.of("success", true, "data", report, "message", "تم إرسال البلاغ بنجاح"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * جلب أنواع البلاغات
     */
    @GetMapping("/types")
    public ResponseEntity<?> getReportTypes() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of(
                        "WRONG_NUMBER", "رقم خاطئ",
                        "FRAUD", "نصب",
                        "OFFENSIVE", "محتوى مسيء",
                        "UNPROFESSIONAL", "عامل غير مهني"
                )
        ));
    }
}
