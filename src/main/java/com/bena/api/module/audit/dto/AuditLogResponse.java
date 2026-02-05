package com.bena.api.module.audit.dto;

import com.bena.api.module.audit.entity.AuditLog;
import com.bena.api.module.audit.entity.AuditLog.AuditAction;
import com.bena.api.module.audit.entity.AuditLog.AuditStatus;
import com.bena.api.module.audit.entity.AuditLog.AuditTargetType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO لعرض سجلات التدقيق
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditLogResponse {

    private Long id;
    private UUID userId;
    private String userEmail;
    private AuditAction action;
    private String actionDisplayName;
    private AuditTargetType targetType;
    private String targetId;
    private String description;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private String userAgent;
    private String requestPath;
    private String requestMethod;
    private AuditStatus status;
    private String errorMessage;
    private OffsetDateTime createdAt;

    public static AuditLogResponse from(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .userEmail(log.getUserEmail())
                .action(log.getAction())
                .actionDisplayName(translateAction(log.getAction()))
                .targetType(log.getTargetType())
                .targetId(log.getTargetId())
                .description(log.getDescription())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .requestPath(log.getRequestPath())
                .requestMethod(log.getRequestMethod())
                .status(log.getStatus())
                .errorMessage(log.getErrorMessage())
                .createdAt(log.getCreatedAt())
                .build();
    }

    private static String translateAction(AuditAction action) {
        if (action == null) return null;
        return switch (action) {
            case USER_LOGIN -> "تسجيل دخول";
            case USER_LOGOUT -> "تسجيل خروج";
            case USER_REGISTER -> "تسجيل حساب جديد";
            case USER_CREATE -> "إنشاء مستخدم";
            case USER_UPDATE -> "تحديث مستخدم";
            case USER_DELETE -> "حذف مستخدم";
            case USER_ACTIVATE -> "تفعيل مستخدم";
            case USER_DEACTIVATE -> "تعطيل مستخدم";
            case PASSWORD_CHANGE -> "تغيير كلمة المرور";
            case PASSWORD_RESET -> "إعادة تعيين كلمة المرور";
            case WORKER_CREATE -> "إنشاء عامل";
            case WORKER_UPDATE -> "تحديث عامل";
            case WORKER_DELETE -> "حذف عامل";
            case WORKER_VERIFY -> "توثيق عامل";
            case WORKER_REJECT -> "رفض توثيق عامل";
            case WORKER_TOGGLE_ACTIVE -> "تغيير حالة تفعيل عامل";
            case WORKER_TOGGLE_FEATURED -> "تغيير حالة تمييز عامل";
            case DOCUMENT_UPLOAD -> "رفع وثيقة";
            case DOCUMENT_VERIFY -> "توثيق وثيقة";
            case DOCUMENT_REJECT -> "رفض وثيقة";
            case CONTRACT_CREATE -> "إنشاء عقد";
            case CONTRACT_UPDATE -> "تحديث عقد";
            case CONTRACT_SIGN -> "توقيع عقد";
            case CONTRACT_COMPLETE -> "إكمال عقد";
            case CONTRACT_TERMINATE -> "إنهاء عقد";
            case PROJECT_CREATE -> "إنشاء مشروع";
            case PROJECT_UPDATE -> "تحديث مشروع";
            case PROJECT_DELETE -> "حذف مشروع";
            case PROJECT_STATUS_CHANGE -> "تغيير حالة مشروع";
            case BID_CREATE -> "تقديم عرض";
            case BID_ACCEPT -> "قبول عرض";
            case BID_REJECT -> "رفض عرض";
            case PAYMENT_CREATE -> "إنشاء دفعة";
            case PAYMENT_PROCESS -> "معالجة دفعة";
            case PAYMENT_REFUND -> "استرداد دفعة";
            case ESCROW_RELEASE -> "إطلاق الضمان";
            case ADMIN_ACTION -> "إجراء إداري";
            case SETTINGS_CHANGE -> "تغيير الإعدادات";
            case FEATURE_FLAG_TOGGLE -> "تغيير Feature Flag";
            case REPORT_CREATE -> "إنشاء بلاغ";
            case REPORT_RESOLVE -> "حل بلاغ";
            case REPORT_DISMISS -> "رفض بلاغ";
            case DESIGN_CREATE -> "إنشاء تصميم";
            case DESIGN_UPDATE -> "تحديث تصميم";
            case DESIGN_DELETE -> "حذف تصميم";
            case BUILDING_STEP_CREATE -> "إنشاء خطوة بناء";
            case BUILDING_STEP_UPDATE -> "تحديث خطوة بناء";
            case BUILDING_STEP_DELETE -> "حذف خطوة بناء";
            case SYSTEM_ERROR -> "خطأ في النظام";
            case SECURITY_ALERT -> "تنبيه أمني";
            case RATE_LIMIT_EXCEEDED -> "تجاوز حد الطلبات";
        };
    }
}
