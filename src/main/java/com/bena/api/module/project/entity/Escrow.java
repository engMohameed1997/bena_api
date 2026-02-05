package com.bena.api.module.project.entity;

import com.bena.api.module.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * كيان حجز الأموال - لحجز الأموال مؤقتاً وإطلاقها على مراحل
 */
@Entity
@Table(name = "escrow_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Escrow {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id")
    private ProjectMilestone milestone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", nullable = false)
    private User payer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payee_id", nullable = false)
    private User payee;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "held_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal heldAmount;

    @Column(name = "released_amount", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal releasedAmount = BigDecimal.ZERO;

    @Column(name = "refunded_amount", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal refundedAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private EscrowStatus status = EscrowStatus.HELD;

    @Column(name = "held_at")
    private LocalDateTime heldAt;

    @Column(name = "release_scheduled_at")
    private LocalDateTime releaseScheduledAt;

    @Column(name = "released_at")
    private LocalDateTime releasedAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "release_reason", columnDefinition = "TEXT")
    private String releaseReason;

    @Column(name = "refund_reason", columnDefinition = "TEXT")
    private String refundReason;

    @Column(name = "auto_release_enabled")
    @Builder.Default
    private Boolean autoReleaseEnabled = true;

    @Column(name = "auto_release_days")
    @Builder.Default
    private Integer autoReleaseDays = 7;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum EscrowStatus {
        HELD,              // محجوز
        PARTIALLY_RELEASED,// مطلق جزئياً
        RELEASED,          // مطلق
        REFUNDED,          // مسترجع
        DISPUTED,          // متنازع عليه
        CANCELLED          // ملغي
    }

    public BigDecimal getRemainingAmount() {
        return heldAmount.subtract(releasedAmount).subtract(refundedAmount);
    }

    public boolean canRelease(BigDecimal requestedAmount) {
        return getRemainingAmount().compareTo(requestedAmount) >= 0;
    }
}
