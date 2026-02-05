package com.bena.api.module.audit.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.audit.dto.AuditLogResponse;
import com.bena.api.module.audit.entity.AuditLog;
import com.bena.api.module.audit.entity.AuditLog.AuditAction;
import com.bena.api.module.audit.entity.AuditLog.AuditTargetType;
import com.bena.api.module.audit.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Controller لإدارة سجلات التدقيق (Admin Only)
 */
@RestController
@RequestMapping("/v1/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Audit Logs", description = "سجلات التدقيق - للأدمن فقط")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @Operation(summary = "جلب سجلات التدقيق", description = "جلب سجلات التدقيق مع إمكانية الفلترة والبحث")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogs(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) AuditTargetType targetType,
            @RequestParam(required = false) String targetId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
            @RequestParam(required = false) String search
    ) {
        Page<AuditLog> logs = auditLogService.getAuditLogs(userId, action, targetType, targetId, startDate, endDate, search, pageable);
        Page<AuditLogResponse> response = logs.map(AuditLogResponse::from);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "جلب سجلات مستخدم معين")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getUserAuditLogs(
            @PathVariable UUID userId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<AuditLog> logs = auditLogService.getUserAuditLogs(userId, pageable);
        Page<AuditLogResponse> response = logs.map(AuditLogResponse::from);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/entity/{targetType}/{targetId}")
    @Operation(summary = "جلب سجلات كيان معين", description = "مثال: /entity/WORKER/123")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getEntityAuditLogs(
            @PathVariable AuditTargetType targetType,
            @PathVariable String targetId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<AuditLog> logs = auditLogService.getEntityAuditLogs(targetType, targetId, pageable);
        Page<AuditLogResponse> response = logs.map(AuditLogResponse::from);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/stats")
    @Operation(summary = "إحصائيات سجلات التدقيق")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAuditStats(
            @RequestParam(defaultValue = "7") int days
    ) {
        Map<String, Object> stats = auditLogService.getAuditStats(days);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/actions")
    @Operation(summary = "قائمة أنواع العمليات المتاحة")
    public ResponseEntity<ApiResponse<AuditAction[]>> getAvailableActions() {
        return ResponseEntity.ok(ApiResponse.success(AuditAction.values()));
    }

    @GetMapping("/target-types")
    @Operation(summary = "قائمة أنواع الكيانات المتاحة")
    public ResponseEntity<ApiResponse<AuditTargetType[]>> getAvailableTargetTypes() {
        return ResponseEntity.ok(ApiResponse.success(AuditTargetType.values()));
    }
}
