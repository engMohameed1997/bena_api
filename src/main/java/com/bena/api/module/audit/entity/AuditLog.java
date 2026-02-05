package com.bena.api.module.audit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * كيان تسجيل العمليات (Audit Log)
 * يسجل جميع العمليات الحساسة في النظام لأغراض:
 * - الأمان والتتبع
 * - Compliance
 * - تحليل سلوك المستخدمين
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_user_id", columnList = "user_id"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_target_type", columnList = "target_type"),
    @Index(name = "idx_audit_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * المستخدم الذي قام بالعملية (null للعمليات المجهولة)
     */
    @Column(name = "user_id")
    private UUID userId;

    /**
     * اسم المستخدم (للعرض السريع)
     */
    @Column(name = "user_email", length = 100)
    private String userEmail;

    /**
     * نوع العملية
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private AuditAction action;

    /**
     * نوع الكيان المستهدف
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 30)
    private AuditTargetType targetType;

    /**
     * معرف الكيان المستهدف
     */
    @Column(name = "target_id", length = 50)
    private String targetId;

    /**
     * وصف إضافي للعملية
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * القيمة القديمة (JSON)
     */
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    /**
     * القيمة الجديدة (JSON)
     */
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    /**
     * عنوان IP للطلب
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * User Agent
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * مسار الـ API المستدعى
     */
    @Column(name = "request_path", length = 200)
    private String requestPath;

    /**
     * HTTP Method
     */
    @Column(name = "request_method", length = 10)
    private String requestMethod;

    /**
     * حالة العملية
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private AuditStatus status = AuditStatus.SUCCESS;

    /**
     * رسالة الخطأ (في حالة الفشل)
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * وقت إنشاء السجل
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * تعداد أنواع العمليات
     */
    public enum AuditAction {
        // User Management
        USER_LOGIN,
        USER_LOGOUT,
        USER_REGISTER,
        USER_CREATE,
        USER_UPDATE,
        USER_DELETE,
        USER_ACTIVATE,
        USER_DEACTIVATE,
        PASSWORD_CHANGE,
        PASSWORD_RESET,
        
        // Worker Management
        WORKER_CREATE,
        WORKER_UPDATE,
        WORKER_DELETE,
        WORKER_VERIFY,
        WORKER_REJECT,
        WORKER_TOGGLE_ACTIVE,
        WORKER_TOGGLE_FEATURED,
        
        // Document Verification
        DOCUMENT_UPLOAD,
        DOCUMENT_VERIFY,
        DOCUMENT_REJECT,
        
        // Contract Management
        CONTRACT_CREATE,
        CONTRACT_UPDATE,
        CONTRACT_SIGN,
        CONTRACT_COMPLETE,
        CONTRACT_TERMINATE,
        
        // Project Management
        PROJECT_CREATE,
        PROJECT_UPDATE,
        PROJECT_DELETE,
        PROJECT_STATUS_CHANGE,
        
        // Bid Management
        BID_CREATE,
        BID_ACCEPT,
        BID_REJECT,
        
        // Payment Operations
        PAYMENT_CREATE,
        PAYMENT_PROCESS,
        PAYMENT_REFUND,
        ESCROW_RELEASE,
        
        // Admin Operations
        ADMIN_ACTION,
        SETTINGS_CHANGE,
        FEATURE_FLAG_TOGGLE,
        
        // Report Management
        REPORT_CREATE,
        REPORT_RESOLVE,
        REPORT_DISMISS,
        
        // Content Management
        DESIGN_CREATE,
        DESIGN_UPDATE,
        DESIGN_DELETE,
        BUILDING_STEP_CREATE,
        BUILDING_STEP_UPDATE,
        BUILDING_STEP_DELETE,
        
        // System Events
        SYSTEM_ERROR,
        SECURITY_ALERT,
        RATE_LIMIT_EXCEEDED
    }

    /**
     * تعداد أنواع الكيانات المستهدفة
     */
    public enum AuditTargetType {
        USER,
        WORKER,
        CONTRACT,
        PROJECT,
        BID,
        PAYMENT,
        ESCROW,
        REPORT,
        DESIGN,
        BUILDING_STEP,
        ADVERTISEMENT,
        CONSULTATION,
        CHAT,
        SYSTEM,
        SETTINGS
    }

    /**
     * تعداد حالات العملية
     */
    public enum AuditStatus {
        SUCCESS,
        FAILURE,
        PENDING,
        PARTIAL
    }
}
