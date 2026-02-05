package com.bena.api.module.admin.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.admin.dto.SystemHealthDto;
import com.bena.api.module.admin.service.SystemHealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller لمراقبة صحة النظام (Admin Only)
 */
@RestController
@RequestMapping("/v1/admin/system")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "System Health", description = "مراقبة صحة النظام - للأدمن فقط")
public class SystemHealthController {

    private final SystemHealthService systemHealthService;

    @GetMapping("/health")
    @Operation(summary = "حالة صحة النظام الكاملة")
    public ResponseEntity<ApiResponse<SystemHealthDto>> getSystemHealth() {
        SystemHealthDto health = systemHealthService.getSystemHealth();
        return ResponseEntity.ok(ApiResponse.success(health));
    }

    @GetMapping("/health/database")
    @Operation(summary = "حالة قاعدة البيانات")
    public ResponseEntity<ApiResponse<SystemHealthDto.ComponentHealth>> getDatabaseHealth() {
        return ResponseEntity.ok(ApiResponse.success(systemHealthService.getDatabaseHealth()));
    }

    @GetMapping("/health/memory")
    @Operation(summary = "استخدام الذاكرة")
    public ResponseEntity<ApiResponse<SystemHealthDto.MemoryInfo>> getMemoryInfo() {
        return ResponseEntity.ok(ApiResponse.success(systemHealthService.getMemoryInfo()));
    }

    @GetMapping("/health/disk")
    @Operation(summary = "استخدام التخزين")
    public ResponseEntity<ApiResponse<SystemHealthDto.DiskInfo>> getDiskInfo() {
        return ResponseEntity.ok(ApiResponse.success(systemHealthService.getDiskInfo()));
    }

    @GetMapping("/info")
    @Operation(summary = "معلومات النظام")
    public ResponseEntity<ApiResponse<SystemHealthDto.SystemInfo>> getSystemInfo() {
        return ResponseEntity.ok(ApiResponse.success(systemHealthService.getSystemInfo()));
    }
}
