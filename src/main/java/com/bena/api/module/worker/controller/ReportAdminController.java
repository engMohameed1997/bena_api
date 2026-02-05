package com.bena.api.module.worker.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.worker.dto.ReportDTO;
import com.bena.api.module.worker.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller لإدارة البلاغات (Admin Only)
 */
@RestController
@RequestMapping("/v1/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReportAdminController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getAllReports(
            @PageableDefault(sort = "createdAt") Pageable pageable
    ) {
        Page<ReportDTO> reports = reportService.getAllReports(pageable);
        return ResponseEntity.ok(ApiResponse.success(reports));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportDTO>> getReportById(@PathVariable Long id) {
        ReportDTO report = reportService.getReportById(id);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReportDTO>> updateReportStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        ReportDTO report = reportService.updateReportStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(report, "تم تحديث حالة البلاغ بنجاح"));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<ReportService.ReportStatsDTO>> getReportStats() {
        ReportService.ReportStatsDTO stats = reportService.getReportStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
