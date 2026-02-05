package com.bena.api.module.project.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * كيان مرحلة المشروع - لتقسيم المشروع إلى مراحل
 */
@Entity
@Table(name = "project_milestones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMilestone {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "milestone_order", nullable = false)
    private Integer milestoneOrder;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private MilestoneStatus status = MilestoneStatus.PENDING;

    @Column(name = "expected_completion_date")
    private LocalDateTime expectedCompletionDate;

    @Column(name = "actual_completion_date")
    private LocalDateTime actualCompletionDate;

    @Column(name = "client_approved")
    @Builder.Default
    private Boolean clientApproved = false;

    @Column(name = "client_approval_date")
    private LocalDateTime clientApprovalDate;

    @Column(name = "payment_released")
    @Builder.Default
    private Boolean paymentReleased = false;

    @Column(name = "payment_release_date")
    private LocalDateTime paymentReleaseDate;

    @Column(name = "work_evidence_urls", columnDefinition = "TEXT")
    private String workEvidenceUrls;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum MilestoneStatus {
        PENDING,          // قيد الانتظار
        IN_PROGRESS,      // قيد التنفيذ
        COMPLETED,        // مكتمل
        APPROVED,         // موافق عليه من العميل
        PAID              // تم الدفع
    }
}
