package com.bena.api.module.design.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * كيان التصميم - يمثل تصميم في المتجر
 */
@Entity
@Table(name = "designs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Design {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // صورة التصميم - URL (للصور الجديدة)
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    // الأعمدة القديمة - للتوافق مع البيانات الموجودة
    @Column(name = "image_data")
    private byte[] imageData;

    @Column(name = "image_type", length = 50)
    private String imageType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DesignCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DesignStyle style;

    @Column(name = "area_sqm")
    private Integer areaInSquareMeters;

    @Column(name = "estimated_cost")
    private Double estimatedCost;

    // المواد
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "design_materials", joinColumns = @JoinColumn(name = "design_id"))
    @Column(name = "material")
    private List<String> materials;

    // الميزات
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "design_features", joinColumns = @JoinColumn(name = "design_id"))
    @Column(name = "feature")
    private List<String> features;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
