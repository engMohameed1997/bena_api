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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * كيان طلب العمل
 */
@Entity
@Table(name = "job_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @Column(name = "job_type", nullable = false, length = 100)
    private String jobType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "location_city", length = 100)
    private String locationCity;

    @Column(name = "location_area", length = 100)
    private String locationArea;

    private Double latitude;

    private Double longitude;

    @Column(precision = 10, scale = 2)
    private BigDecimal budget;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    @Builder.Default
    private JobStatus status = JobStatus.PENDING;

    @Column(name = "worker_response", columnDefinition = "TEXT")
    private String workerResponse;

    @Column(name = "worker_price_offer", precision = 10, scale = 2)
    private BigDecimal workerPriceOffer;

    @OneToMany(mappedBy = "jobRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<JobRequestImage> images = new ArrayList<>();

    // العرض المقبول (بعد التفاوض)
    @Column(name = "accepted_offer_id")
    private Long acceptedOfferId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum JobStatus {
        PENDING,      // قيد الانتظار
        ACCEPTED,     // مقبول
        REJECTED,     // مرفوض
        OFFER_SENT,   // تم إرسال عرض سعر
        NEGOTIATING,  // قيد التفاوض
        IN_PROGRESS,  // قيد التنفيذ
        COMPLETED,    // مكتمل
        CANCELLED     // ملغي
    }
}
