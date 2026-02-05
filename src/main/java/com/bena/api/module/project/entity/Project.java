package com.bena.api.module.project.entity;

import com.bena.api.module.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * كيان المشروع - يربط العميل مع المختص (خلف/مقاول/مهندس/مصمم)
 */
@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

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
    @Column(name = "project_type", nullable = false, length = 50)
    private ProjectType projectType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.PENDING;

    @Column(name = "total_budget", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalBudget;

    @Column(name = "platform_commission_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal platformCommissionPercentage = new BigDecimal("10.00");

    @Column(name = "platform_commission_amount", precision = 12, scale = 2)
    private BigDecimal platformCommissionAmount;

    @Column(name = "provider_amount", precision = 12, scale = 2)
    private BigDecimal providerAmount;

    @Column(name = "location_city", length = 100)
    private String locationCity;

    @Column(name = "location_area", length = 100)
    private String locationArea;

    private Double latitude;

    private Double longitude;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "expected_end_date")
    private LocalDateTime expectedEndDate;

    @Column(name = "actual_end_date")
    private LocalDateTime actualEndDate;

    @Column(name = "client_rating")
    private Integer clientRating;

    @Column(name = "client_review", columnDefinition = "TEXT")
    private String clientReview;

    @Column(name = "provider_rating")
    private Integer providerRating;

    @Column(name = "provider_review", columnDefinition = "TEXT")
    private String providerReview;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectMilestone> milestones = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ProjectType {
        CONSTRUCTION,      // بناء
        RENOVATION,        // ترميم
        DESIGN,           // تصميم
        ENGINEERING,      // هندسة (خرائط وواجهات)
        CONSULTATION,     // استشارة
        MIXED             // مختلط
    }

    public enum ProjectStatus {
        PLANNING,         // مرحلة التخطيط/التجهيز
        PENDING,          // قيد الانتظار
        ACCEPTED,         // مقبول
        REJECTED,         // مرفوض
        IN_PROGRESS,      // قيد التنفيذ
        COMPLETED,        // مكتمل
        CANCELLED,        // ملغي
        DISPUTED          // متنازع عليه
    }

    public void calculateCommissionAndProviderAmount() {
        if (totalBudget != null && platformCommissionPercentage != null) {
            this.platformCommissionAmount = totalBudget
                    .multiply(platformCommissionPercentage)
                    .divide(new BigDecimal("100"));
            this.providerAmount = totalBudget.subtract(platformCommissionAmount);
        }
    }
}
