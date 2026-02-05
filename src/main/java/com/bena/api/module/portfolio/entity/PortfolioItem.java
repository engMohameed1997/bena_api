package com.bena.api.module.portfolio.entity;

import com.bena.api.module.worker.entity.Worker;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "portfolio_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String category;

    @Column(name = "project_type", length = 100)
    private String projectType;

    @Column(name = "location_city", length = 100)
    private String locationCity;

    @Column(name = "location_area", length = 100)
    private String locationArea;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @Column(precision = 15, scale = 2)
    private BigDecimal budget;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @OneToMany(mappedBy = "portfolioItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PortfolioImage> images = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
