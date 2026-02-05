package com.bena.api.module.worker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * كيان عرض السعر - يحتوي على تفاصيل كاملة للعرض
 */
@Entity
@Table(name = "job_request_offers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRequestOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_request_id", nullable = false)
    private JobRequest jobRequest;

    // من قدم العرض (worker أو homeowner)
    @Enumerated(EnumType.STRING)
    @Column(name = "offered_by", nullable = false, length = 20)
    private OfferedBy offeredBy;

    // السعر المقترح
    @Column(name = "offered_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal offeredPrice;

    // مدة التنفيذ المقترحة (بالأيام)
    @Column(name = "estimated_duration_days")
    private Integer estimatedDurationDays;

    // تاريخ البدء المقترح
    @Column(name = "proposed_start_date")
    private LocalDateTime proposedStartDate;

    // ملاحظات على العرض
    @Column(name = "offer_notes", columnDefinition = "TEXT")
    private String offerNotes;

    // شروط الدفع المقترحة
    @Column(name = "payment_terms", columnDefinition = "TEXT")
    private String paymentTerms;

    // الضمانات المقدمة
    @Column(name = "warranty_terms", columnDefinition = "TEXT")
    private String warrantyTerms;

    // حالة العرض
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OfferStatus status = OfferStatus.PENDING;

    // رد على عرض سابق (للتفاوض)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counter_to_offer_id")
    private JobRequestOffer counterToOffer;

    // سبب الرفض (إن وجد)
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum OfferedBy {
        WORKER,      // من المختص
        HOMEOWNER    // من صاحب المنزل (عرض مضاد)
    }

    public enum OfferStatus {
        PENDING,     // قيد الانتظار
        ACCEPTED,    // مقبول
        REJECTED,    // مرفوض
        COUNTERED,   // تم الرد بعرض مضاد
        EXPIRED      // منتهي
    }
}
