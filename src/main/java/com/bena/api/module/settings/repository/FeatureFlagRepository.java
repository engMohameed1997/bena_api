package com.bena.api.module.settings.repository;

import com.bena.api.module.settings.entity.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository لإدارة Feature Flags
 */
@Repository
public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {

    /**
     * البحث عن feature flag بواسطة المفتاح
     */
    Optional<FeatureFlag> findByFeatureKey(String featureKey);

    /**
     * التحقق من وجود feature flag
     */
    boolean existsByFeatureKey(String featureKey);

    /**
     * جلب جميع الـ flags المُفعّلة
     */
    List<FeatureFlag> findByIsEnabledTrue();

    /**
     * جلب الـ flags حسب الفئة
     */
    List<FeatureFlag> findByCategory(String category);

    /**
     * جلب جميع الفئات المتاحة
     */
    @Query("SELECT DISTINCT f.category FROM FeatureFlag f WHERE f.category IS NOT NULL ORDER BY f.category")
    List<String> findAllCategories();

    /**
     * جلب الـ flags مرتبة
     */
    List<FeatureFlag> findAllByOrderByCategoryAscNameAsc();
}
