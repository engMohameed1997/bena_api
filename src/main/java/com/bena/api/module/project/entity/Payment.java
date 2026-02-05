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
 * كيان الدفع - سجل المدفوعات
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

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

    @Column(name = "platform_fee", precision = 12, scale = 2)
    private BigDecimal platformFee;

    @Column(name = "net_amount", precision = 12, scale = 2)
    private BigDecimal netAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 50)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 50)
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_id", length = 200)
    private String transactionId;

    @Column(name = "payment_gateway", length = 100)
    private String paymentGateway;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "refund_amount", precision = 12, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_date")
    private LocalDateTime refundDate;

    @Column(name = "refund_reason", columnDefinition = "TEXT")
    private String refundReason;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PaymentType {
        INITIAL_DEPOSIT,   // دفعة أولية
        MILESTONE,         // دفعة مرحلة
        FINAL,             // دفعة نهائية
        REFUND,            // استرجاع
        PLATFORM_FEE       // عمولة المنصة
    }

    public enum PaymentStatus {
        PENDING,           // قيد الانتظار
        PROCESSING,        // قيد المعالجة
        COMPLETED,         // مكتمل
        FAILED,            // فشل
        REFUNDED,          // مسترجع
        CANCELLED          // ملغي
    }

    public enum PaymentMethod {
        CREDIT_CARD,       // بطاقة ائتمان
        DEBIT_CARD,        // بطاقة خصم
        BANK_TRANSFER,     // تحويل بنكي
        WALLET,            // محفظة
        CASH,              // نقدي
        OTHER              // أخرى
    }

    public void calculateNetAmount() {
        if (amount != null && platformFee != null) {
            this.netAmount = amount.subtract(platformFee);
        }
    }
}
