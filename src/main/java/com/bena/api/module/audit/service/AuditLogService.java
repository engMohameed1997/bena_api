package com.bena.api.module.audit.service;

import com.bena.api.module.audit.entity.AuditLog;
import com.bena.api.module.audit.entity.AuditLog.AuditAction;
import com.bena.api.module.audit.entity.AuditLog.AuditStatus;
import com.bena.api.module.audit.entity.AuditLog.AuditTargetType;
import com.bena.api.module.audit.repository.AuditLogRepository;
import com.bena.api.module.user.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * خدمة تسجيل التدقيق (Audit Log Service)
 * تسجل جميع العمليات الحساسة في النظام بشكل غير متزامن
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    // ==================== Logging Methods ====================

    /**
     * تسجيل عملية بشكل غير متزامن (لا تؤثر على أداء العملية الأصلية)
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAsync(AuditAction action, AuditTargetType targetType, String targetId, String description) {
        try {
            AuditLog auditLog = buildAuditLog(action, targetType, targetId, description, null, null, AuditStatus.SUCCESS, null);
            auditLogRepository.save(auditLog);
            log.debug("📝 Audit logged: {} on {} ({})", action, targetType, targetId);
        } catch (Exception e) {
            log.error("❌ Failed to save audit log: {}", e.getMessage());
        }
    }

    /**
     * تسجيل عملية بشكل متزامن (لضمان الحفظ قبل الاستمرار)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSync(AuditAction action, AuditTargetType targetType, String targetId, String description) {
        try {
            AuditLog auditLog = buildAuditLog(action, targetType, targetId, description, null, null, AuditStatus.SUCCESS, null);
            auditLogRepository.save(auditLog);
            log.debug("📝 Audit logged (sync): {} on {} ({})", action, targetType, targetId);
        } catch (Exception e) {
            log.error("❌ Failed to save audit log: {}", e.getMessage());
        }
    }

    /**
     * تسجيل عملية مع القيم القديمة والجديدة
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logWithChanges(AuditAction action, AuditTargetType targetType, String targetId, 
                                String description, Object oldValue, Object newValue) {
        try {
            String oldJson = toJson(oldValue);
            String newJson = toJson(newValue);
            AuditLog auditLog = buildAuditLog(action, targetType, targetId, description, oldJson, newJson, AuditStatus.SUCCESS, null);
            auditLogRepository.save(auditLog);
            log.debug("📝 Audit logged with changes: {} on {} ({})", action, targetType, targetId);
        } catch (Exception e) {
            log.error("❌ Failed to save audit log: {}", e.getMessage());
        }
    }

    /**
     * تسجيل عملية فاشلة
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailure(AuditAction action, AuditTargetType targetType, String targetId, String errorMessage) {
        try {
            AuditLog auditLog = buildAuditLog(action, targetType, targetId, "Operation failed", null, null, AuditStatus.FAILURE, errorMessage);
            auditLogRepository.save(auditLog);
            log.debug("📝 Audit failure logged: {} on {} ({}): {}", action, targetType, targetId, errorMessage);
        } catch (Exception e) {
            log.error("❌ Failed to save audit log: {}", e.getMessage());
        }
    }

    /**
     * تسجيل تنبيه أمني
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSecurityAlert(String description, String ipAddress) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .action(AuditAction.SECURITY_ALERT)
                    .targetType(AuditTargetType.SYSTEM)
                    .description(description)
                    .ipAddress(ipAddress)
                    .status(AuditStatus.SUCCESS)
                    .build();
            populateRequestInfo(auditLog);
            auditLogRepository.save(auditLog);
            log.warn("🚨 Security alert: {}", description);
        } catch (Exception e) {
            log.error("❌ Failed to save security alert: {}", e.getMessage());
        }
    }

    // ==================== Convenience Methods ====================

    /**
     * تسجيل تسجيل دخول ناجح
     */
    public void logLogin(UUID userId, String email) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .userEmail(email)
                    .action(AuditAction.USER_LOGIN)
                    .targetType(AuditTargetType.USER)
                    .targetId(userId.toString())
                    .description("User logged in successfully")
                    .status(AuditStatus.SUCCESS)
                    .build();
            populateRequestInfo(auditLog);
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("❌ Failed to log login: {}", e.getMessage());
        }
    }

    /**
     * تسجيل محاولة تسجيل دخول فاشلة
     */
    public void logFailedLogin(String email, String reason) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .userEmail(email)
                    .action(AuditAction.USER_LOGIN)
                    .targetType(AuditTargetType.USER)
                    .description("Login failed: " + reason)
                    .status(AuditStatus.FAILURE)
                    .errorMessage(reason)
                    .build();
            populateRequestInfo(auditLog);
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("❌ Failed to log failed login: {}", e.getMessage());
        }
    }

    /**
     * تسجيل تسجيل خروج
     */
    public void logLogout(UUID userId, String email) {
        logAsync(AuditAction.USER_LOGOUT, AuditTargetType.USER, userId.toString(), "User logged out");
    }

    /**
     * تسجيل إنشاء مستخدم
     */
    public void logUserCreate(UUID userId, String email, String creatorContext) {
        logAsync(AuditAction.USER_CREATE, AuditTargetType.USER, userId.toString(), 
                "User created: " + email + " by " + creatorContext);
    }

    /**
     * تسجيل حذف/تعطيل مستخدم
     */
    public void logUserDelete(UUID userId, String email) {
        logSync(AuditAction.USER_DELETE, AuditTargetType.USER, userId.toString(), 
                "User deleted/deactivated: " + email);
    }

    /**
     * تسجيل توثيق عامل
     */
    public void logWorkerVerify(Long workerId, String workerName) {
        logSync(AuditAction.WORKER_VERIFY, AuditTargetType.WORKER, workerId.toString(), 
                "Worker verified: " + workerName);
    }

    /**
     * تسجيل رفض توثيق عامل
     */
    public void logWorkerReject(Long workerId, String workerName, String reason) {
        logSync(AuditAction.WORKER_REJECT, AuditTargetType.WORKER, workerId.toString(), 
                "Worker rejected: " + workerName + " - Reason: " + reason);
    }

    // ==================== Query Methods ====================

    /**
     * جلب سجلات التدقيق مع فلاتر
     */
    public Page<AuditLog> getAuditLogs(UUID userId, AuditAction action, AuditTargetType targetType,
                                        String targetId, OffsetDateTime startDate, OffsetDateTime endDate,
                                        String search, Pageable pageable) {
        return auditLogRepository.searchAuditLogs(userId, action, targetType, targetId, startDate, endDate, search, pageable);
    }

    /**
     * جلب سجلات مستخدم معين
     */
    public Page<AuditLog> getUserAuditLogs(UUID userId, Pageable pageable) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * جلب سجلات كيان معين
     */
    public Page<AuditLog> getEntityAuditLogs(AuditTargetType targetType, String targetId, Pageable pageable) {
        return auditLogRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc(targetType, targetId, pageable);
    }

    /**
     * إحصائيات العمليات
     */
    public Map<String, Object> getAuditStats(int days) {
        OffsetDateTime since = OffsetDateTime.now().minusDays(days);
        
        Map<String, Object> stats = new HashMap<>();
        
        // إحصائيات حسب نوع العملية
        List<Object[]> actionStats = auditLogRepository.getActionStatsSince(since);
        Map<String, Long> actionCounts = new HashMap<>();
        for (Object[] row : actionStats) {
            actionCounts.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("actionCounts", actionCounts);
        
        // إحصائيات نشاط المستخدمين
        List<Object[]> userActivity = auditLogRepository.getUserActivitySince(since, Pageable.ofSize(10));
        stats.put("topActiveUsers", userActivity);
        
        // إجمالي العمليات
        stats.put("totalLogs", auditLogRepository.count());
        stats.put("period", days + " days");
        
        return stats;
    }

    /**
     * التحقق من محاولات الدخول الفاشلة (للأمان)
     */
    public boolean hasExcessiveFailedLogins(String ipAddress, int maxAttempts, int withinMinutes) {
        OffsetDateTime since = OffsetDateTime.now().minusMinutes(withinMinutes);
        long failedAttempts = auditLogRepository.countFailedLoginAttempts(ipAddress, since);
        return failedAttempts >= maxAttempts;
    }

    // ==================== Helper Methods ====================

    private AuditLog buildAuditLog(AuditAction action, AuditTargetType targetType, String targetId,
                                    String description, String oldValue, String newValue,
                                    AuditStatus status, String errorMessage) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .description(description)
                .oldValue(oldValue)
                .newValue(newValue)
                .status(status)
                .errorMessage(errorMessage)
                .build();

        // استخراج معلومات المستخدم الحالي
        populateCurrentUser(auditLog);
        
        // استخراج معلومات الطلب
        populateRequestInfo(auditLog);

        return auditLog;
    }

    private void populateCurrentUser(AuditLog auditLog) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User user) {
                auditLog.setUserId(user.getId());
                auditLog.setUserEmail(user.getEmail());
            }
        } catch (Exception e) {
            // User info not available
        }
    }

    private void populateRequestInfo(AuditLog auditLog) {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                auditLog.setIpAddress(getClientIP(request));
                auditLog.setUserAgent(request.getHeader("User-Agent"));
                auditLog.setRequestPath(request.getRequestURI());
                auditLog.setRequestMethod(request.getMethod());
            }
        } catch (Exception e) {
            // Request info not available
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return obj.toString();
        }
    }

    // ==================== Cleanup ====================

    /**
     * حذف السجلات القديمة (يُنفذ بشكل دوري)
     */
    @Transactional
    public void cleanupOldLogs(int retentionDays) {
        OffsetDateTime cutoff = OffsetDateTime.now().minusDays(retentionDays);
        auditLogRepository.deleteByCreatedAtBefore(cutoff);
        log.info("🧹 Cleaned up audit logs older than {} days", retentionDays);
    }
}
