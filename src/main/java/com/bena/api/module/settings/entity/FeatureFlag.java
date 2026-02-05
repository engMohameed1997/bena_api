package com.bena.api.module.settings.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * كيان Feature Flag
 * يُستخدم لتفعيل/إيقاف ميزات النظام بدون deploy
 */
@Entity
@Table(name = "feature_flags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * مفتاح الميزة (فريد)
     */
    @Column(name = "feature_key", nullable = false, unique = true, length = 100)
    private String featureKey;

    /**
     * اسم الميزة (للعرض)
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * وصف الميزة
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * هل الميزة مُفعّلة؟
     */
    @Column(name = "is_enabled")
    @Builder.Default
    private Boolean isEnabled = false;

    /**
     * النسبة المئوية للمستخدمين (للـ gradual rollout)
     * null يعني الكل أو لا أحد
     */
    @Column(name = "rollout_percentage")
    private Integer rolloutPercentage;

    /**
     * الفئة/المجموعة
     */
    @Column(name = "category", length = 50)
    private String category;

    /**
     * بيانات إضافية (JSON)
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    /**
     * من قام بآخر تعديل
     */
    @Column(name = "updated_by")
    private UUID updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    /**
     * التحقق من تفعيل الميزة
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(isEnabled);
    }

    /**
     * التحقق من تفعيل الميزة لمستخدم معين (للـ gradual rollout)
     */
    public boolean isEnabledForUser(UUID userId) {
        if (!isActive()) return false;
        if (rolloutPercentage == null || rolloutPercentage >= 100) return true;
        if (rolloutPercentage <= 0) return false;

        // استخدام hash للـ userId لتوزيع عادل
        int hash = Math.abs(userId.hashCode() % 100);
        return hash < rolloutPercentage;
    }
}
