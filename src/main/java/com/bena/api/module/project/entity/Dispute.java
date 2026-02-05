package com.bena.api.module.project.entity;

import com.bena.api.module.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * كيان النزاع - لإدارة الإبلاغات بين العميل والمختص
 */
@Entity
@Table(name = "disputes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raised_by_id", nullable = false)
    private User raisedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "against_id", nullable = false)
    private User against;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "dispute_type", nullable = false, length = 50)
    private DisputeType disputeType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private DisputeStatus status = DisputeStatus.OPEN;

    @Column(name = "evidence_urls", columnDefinition = "TEXT")
    private String evidenceUrls;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_admin_id")
    private User assignedAdmin;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "resolution_details", columnDefinition = "TEXT")
    private String resolutionDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "resolution_outcome", length = 50)
    private ResolutionOutcome resolutionOutcome;

    @Column(name = "payment_held")
    @Builder.Default
    private Boolean paymentHeld = false;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum DisputeType {
        QUALITY_ISSUE,        // مشكلة في الجودة
        PAYMENT_ISSUE,        // مشكلة في الدفع
        DELAY,                // تأخير
        SCOPE_CHANGE,         // تغيير في النطاق
        COMMUNICATION,        // مشكلة تواصل
        CONTRACT_VIOLATION,   // انتهاك العقد
        OTHER                 // أخرى
    }

    public enum DisputeStatus {
        OPEN,                 // مفتوح
        UNDER_REVIEW,         // قيد المراجعة
        AWAITING_EVIDENCE,    // في انتظار الأدلة
        RESOLVED,             // محلول
        CLOSED                // مغلق
    }

    public enum ResolutionOutcome {
        FAVOR_CLIENT,         // لصالح العميل
        FAVOR_PROVIDER,       // لصالح المختص
        COMPROMISE,           // حل وسط
        NO_FAULT,             // لا يوجد خطأ
        CANCELLED             // ملغي
    }
}
