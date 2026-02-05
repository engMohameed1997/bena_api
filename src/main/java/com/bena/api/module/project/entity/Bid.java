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
 * كيان العرض - عروض متعددة من مختلف المختصين على طلب واحد
 */
@Entity
@Table(name = "bids")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 50)
    private ServiceType serviceType;

    @Column(name = "offered_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal offeredPrice;

    @Column(name = "estimated_duration_days")
    private Integer estimatedDurationDays;

    @Column(name = "proposal_details", columnDefinition = "TEXT")
    private String proposalDetails;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private BidStatus status = BidStatus.PENDING;

    @Column(name = "client_response", columnDefinition = "TEXT")
    private String clientResponse;

    @Column(name = "response_date")
    private LocalDateTime responseDate;

    @Column(name = "converted_to_project_id")
    private UUID convertedToProjectId;

    @Column(name = "location_city", length = 100)
    private String locationCity;

    @Column(name = "location_area", length = 100)
    private String locationArea;

    private Double latitude;

    private Double longitude;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ServiceType {
        CONSTRUCTION,      // بناء
        RENOVATION,        // ترميم
        DESIGN,           // تصميم
        ENGINEERING,      // هندسة
        CONSULTATION,     // استشارة
        LABOR             // عمالة
    }

    public enum BidStatus {
        PENDING,          // قيد الانتظار
        ACCEPTED,         // مقبول
        REJECTED,         // مرفوض
        WITHDRAWN,        // منسحب
        EXPIRED,          // منتهي الصلاحية
        CONVERTED         // تم تحويله لمشروع
    }
}
