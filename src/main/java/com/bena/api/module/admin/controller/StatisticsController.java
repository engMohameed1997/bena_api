package com.bena.api.module.admin.controller;

import com.bena.api.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller للإحصائيات (Dashboard Analytics) 
 */
@RestController
@RequestMapping("/v1/admin/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {

    private final com.bena.api.module.user.repository.UserRepository userRepository;
    private final com.bena.api.module.project.repository.ContractRepository contractRepository;
    private final com.bena.api.module.worker.repository.JobRequestRepository jobRequestRepository;
    private final com.bena.api.module.worker.repository.ReportRepository reportRepository;
    private final com.bena.api.module.design.repository.DesignRepository designRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // إحصائيات المستخدمين
        stats.put("totalUsers", userRepository.count());
        stats.put("activeUsers", userRepository.countByIsActiveTrue());

        // إحصائيات العقود
        stats.put("totalContracts", contractRepository.count());
        stats.put("activeContracts", contractRepository.countByStatus(
            com.bena.api.module.project.entity.Contract.ContractStatus.ACTIVE));
        stats.put("pendingContracts", contractRepository.countByStatus(
            com.bena.api.module.project.entity.Contract.ContractStatus.PENDING_SIGNATURE));

        // إحصائيات طلبات العمل
        stats.put("totalJobRequests", jobRequestRepository.count());
        stats.put("pendingRequests", jobRequestRepository.countByStatus(
            com.bena.api.module.worker.entity.JobRequest.JobStatus.PENDING));
        stats.put("completedRequests", jobRequestRepository.countByStatus(
            com.bena.api.module.worker.entity.JobRequest.JobStatus.COMPLETED));

        // إحصائيات البلاغات
        stats.put("totalReports", reportRepository.count());
        stats.put("pendingReports", reportRepository.countByStatus(
            com.bena.api.module.worker.entity.Report.ReportStatus.PENDING));

        // إحصائيات التصاميم
        stats.put("totalDesigns", designRepository.count());

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/users/growth")
    public ResponseEntity<ApiResponse<java.util.List<Map<String, Object>>>> getUserGrowth(
            @RequestParam(defaultValue = "7") int days
    ) {
        java.util.List<Map<String, Object>> growth = new java.util.ArrayList<>();
        
        java.time.LocalDate today = java.time.LocalDate.now();
        for (int i = days - 1; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            java.time.OffsetDateTime startOfDay = date.atStartOfDay().atOffset(java.time.ZoneOffset.UTC);
            java.time.OffsetDateTime endOfDay = date.plusDays(1).atStartOfDay().atOffset(java.time.ZoneOffset.UTC);
            
            long count = userRepository.countByCreatedAtBetween(startOfDay, endOfDay);
            
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("date", date.toString());
            dataPoint.put("count", count);
            growth.add(dataPoint);
        }
        
        return ResponseEntity.ok(ApiResponse.success(growth));
    }

    @GetMapping("/contracts/status-distribution")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getContractStatusDistribution() {
        Map<String, Long> distribution = new HashMap<>();
        
        for (com.bena.api.module.project.entity.Contract.ContractStatus status : 
             com.bena.api.module.project.entity.Contract.ContractStatus.values()) {
            distribution.put(status.name(), contractRepository.countByStatus(status));
        }
        
        return ResponseEntity.ok(ApiResponse.success(distribution));
    }

    @GetMapping("/job-requests/status-distribution")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getJobRequestStatusDistribution() {
        Map<String, Long> distribution = new HashMap<>();
        
        for (com.bena.api.module.worker.entity.JobRequest.JobStatus status : 
             com.bena.api.module.worker.entity.JobRequest.JobStatus.values()) {
            distribution.put(status.name(), jobRequestRepository.countByStatus(status));
        }
        
        return ResponseEntity.ok(ApiResponse.success(distribution));
    }
}
