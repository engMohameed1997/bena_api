package com.bena.api.module.project.entity;

import com.bena.api.module.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * كيان الإشعار - لإرسال إشعارات لجميع الأطراف
 */
@Entity
@Table(name = "project_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "reference_type", length = 50)
    private String referenceType;

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private NotificationPriority priority;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum NotificationType {
        NEW_BID,              // عرض جديد
        BID_ACCEPTED,         // عرض مقبول
        BID_REJECTED,         // عرض مرفوض
        PROJECT_CREATED,      // مشروع جديد
        PROJECT_STARTED,      // بدء المشروع
        PROJECT_COMPLETED,    // اكتمال المشروع
        MILESTONE_COMPLETED,  // اكتمال مرحلة
        MILESTONE_APPROVED,   // موافقة على مرحلة
        PAYMENT_RECEIVED,     // استلام دفعة
        PAYMENT_RELEASED,     // إطلاق دفعة
        CONTRACT_SIGNED,      // توقيع عقد
        DISPUTE_RAISED,       // نزاع جديد
        DISPUTE_RESOLVED,     // حل نزاع
        REVIEW_RECEIVED,      // تقييم جديد
        MESSAGE_RECEIVED,     // رسالة جديدة
        SYSTEM_ALERT          // تنبيه نظام
    }

    public enum NotificationPriority {
        LOW,                  // منخفض
        MEDIUM,               // متوسط
        HIGH,                 // عالي
        URGENT                // عاجل
    }
}
