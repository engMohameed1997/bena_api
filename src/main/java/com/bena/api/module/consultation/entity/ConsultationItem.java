package com.bena.api.module.consultation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * عناصر الاستشارات (أنواع الطابوق، أنواع الصب، إلخ)
 * Consultation Items (Brick types, Concrete types, etc.)
 */
@Entity
@Table(name = "consultation_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ConsultationCategory category;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(name = "name_ar", nullable = false, length = 150)
    private String nameAr;

    @Column(name = "name_en", length = 150)
    private String nameEn;

    @Column(name = "description_ar", columnDefinition = "TEXT")
    private String descriptionAr;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    /**
     * السعر التقريبي (إن وجد)
     */
    @Column(name = "price_from", precision = 15, scale = 2)
    private BigDecimal priceFrom;

    @Column(name = "price_to", precision = 15, scale = 2)
    private BigDecimal priceTo;

    @Column(name = "price_unit", length = 50)
    private String priceUnit;

    @Column(length = 3)
    @Builder.Default
    private String currency = "IQD";

    /**
     * المميزات
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> advantages;

    /**
     * العيوب
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> disadvantages;

    /**
     * الاستخدامات المناسبة
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "suitable_uses", columnDefinition = "jsonb")
    private List<String> suitableUses;

    /**
     * المواصفات الفنية
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> specifications;

    /**
     * نصائح وتوصيات
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> tips;

    /**
     * روابط الصور
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "image_urls", columnDefinition = "jsonb")
    private List<String> imageUrls;

    /**
     * رابط فيديو (يوتيوب أو غيره)
     */
    @Column(name = "video_url")
    private String videoUrl;

    /**
     * التقييم (1-5)
     */
    @Column(precision = 2, scale = 1)
    private BigDecimal rating;

    /**
     * عدد المشاهدات
     */
    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
