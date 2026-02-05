package com.bena.api.module.settings.service;

import com.bena.api.module.audit.entity.AuditLog.AuditAction;
import com.bena.api.module.audit.entity.AuditLog.AuditTargetType;
import com.bena.api.module.audit.service.AuditLogService;
import com.bena.api.module.settings.entity.FeatureFlag;
import com.bena.api.module.settings.repository.FeatureFlagRepository;
import com.bena.api.module.user.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * خدمة إدارة Feature Flags
 * تستخدم caching للأداء مع تحديث دوري
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FeatureFlagService {

    private final FeatureFlagRepository featureFlagRepository;
    private final AuditLogService auditLogService;

    // Cache للأداء
    private final Map<String, FeatureFlag> flagCache = new ConcurrentHashMap<>();
    private volatile boolean cacheInitialized = false;

    /**
     * تهيئة الـ cache عند بدء التطبيق
     */
    @PostConstruct
    public void initializeCache() {
        refreshCache();
        log.info("🚩 Feature flags cache initialized with {} flags", flagCache.size());
    }

    /**
     * تحديث الـ cache كل 5 دقائق
     */
    @Scheduled(fixedRate = 300000) // 5 دقائق
    public void refreshCache() {
        try {
            List<FeatureFlag> flags = featureFlagRepository.findAll();
            Map<String, FeatureFlag> newCache = new ConcurrentHashMap<>();
            flags.forEach(flag -> newCache.put(flag.getFeatureKey(), flag));
            flagCache.clear();
            flagCache.putAll(newCache);
            cacheInitialized = true;
            log.debug("🔄 Feature flags cache refreshed");
        } catch (Exception e) {
            log.error("❌ Failed to refresh feature flags cache", e);
        }
    }

    // ==================== Check Methods ====================

    /**
     * التحقق من تفعيل ميزة (عام)
     */
    public boolean isEnabled(String featureKey) {
        FeatureFlag flag = getFlag(featureKey);
        return flag != null && flag.isActive();
    }

    /**
     * التحقق من تفعيل ميزة لمستخدم معين (للـ gradual rollout)
     */
    public boolean isEnabledForUser(String featureKey, UUID userId) {
        FeatureFlag flag = getFlag(featureKey);
        return flag != null && flag.isEnabledForUser(userId);
    }

    /**
     * التحقق من تفعيل ميزة للمستخدم الحالي
     */
    public boolean isEnabledForCurrentUser(String featureKey) {
        FeatureFlag flag = getFlag(featureKey);
        if (flag == null || !flag.isActive()) return false;

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User user) {
                return flag.isEnabledForUser(user.getId());
            }
        } catch (Exception e) {
            // Fall back to general check
        }
        return flag.isActive();
    }

    /**
     * جلب flag من الـ cache
     */
    private FeatureFlag getFlag(String featureKey) {
        if (!cacheInitialized) {
            refreshCache();
        }
        return flagCache.get(featureKey);
    }

    // ==================== Admin Methods ====================

    /**
     * جلب جميع الـ flags
     */
    public List<FeatureFlag> getAllFlags() {
        return featureFlagRepository.findAllByOrderByCategoryAscNameAsc();
    }

    /**
     * جلب flag حسب المفتاح
     */
    public Optional<FeatureFlag> getByKey(String featureKey) {
        return featureFlagRepository.findByFeatureKey(featureKey);
    }

    /**
     * جلب الـ flags المُفعّلة
     */
    public List<FeatureFlag> getEnabledFlags() {
        return featureFlagRepository.findByIsEnabledTrue();
    }

    /**
     * جلب الـ flags حسب الفئة
     */
    public List<FeatureFlag> getFlagsByCategory(String category) {
        return featureFlagRepository.findByCategory(category);
    }

    /**
     * جلب جميع الفئات
     */
    public List<String> getAllCategories() {
        return featureFlagRepository.findAllCategories();
    }

    /**
     * إنشاء feature flag جديد
     */
    @Transactional
    public FeatureFlag create(String featureKey, String name, String description, String category) {
        if (featureFlagRepository.existsByFeatureKey(featureKey)) {
            throw new IllegalArgumentException("Feature flag already exists: " + featureKey);
        }

        FeatureFlag flag = FeatureFlag.builder()
                .featureKey(featureKey)
                .name(name)
                .description(description)
                .category(category)
                .isEnabled(false)
                .updatedBy(getCurrentUserId())
                .build();

        FeatureFlag saved = featureFlagRepository.save(flag);
        flagCache.put(featureKey, saved);

        auditLogService.logAsync(
            AuditAction.FEATURE_FLAG_TOGGLE,
            AuditTargetType.SETTINGS,
            featureKey,
            "Created feature flag: " + name
        );

        log.info("🚩 Created feature flag: {}", featureKey);
        return saved;
    }

    /**
     * تفعيل/إيقاف feature flag
     */
    @Transactional
    public FeatureFlag toggle(String featureKey) {
        FeatureFlag flag = featureFlagRepository.findByFeatureKey(featureKey)
                .orElseThrow(() -> new IllegalArgumentException("Feature flag not found: " + featureKey));

        boolean oldValue = Boolean.TRUE.equals(flag.getIsEnabled());
        flag.setIsEnabled(!oldValue);
        flag.setUpdatedBy(getCurrentUserId());

        FeatureFlag saved = featureFlagRepository.save(flag);
        flagCache.put(featureKey, saved);

        auditLogService.logWithChanges(
            AuditAction.FEATURE_FLAG_TOGGLE,
            AuditTargetType.SETTINGS,
            featureKey,
            "Toggled feature flag: " + flag.getName(),
            oldValue,
            !oldValue
        );

        log.info("🚩 Toggled feature flag '{}': {} -> {}", featureKey, oldValue, !oldValue);
        return saved;
    }

    /**
     * تحديث نسبة الـ rollout
     */
    @Transactional
    public FeatureFlag updateRolloutPercentage(String featureKey, Integer percentage) {
        if (percentage != null && (percentage < 0 || percentage > 100)) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }

        FeatureFlag flag = featureFlagRepository.findByFeatureKey(featureKey)
                .orElseThrow(() -> new IllegalArgumentException("Feature flag not found: " + featureKey));

        Integer oldPercentage = flag.getRolloutPercentage();
        flag.setRolloutPercentage(percentage);
        flag.setUpdatedBy(getCurrentUserId());

        FeatureFlag saved = featureFlagRepository.save(flag);
        flagCache.put(featureKey, saved);

        auditLogService.logWithChanges(
            AuditAction.FEATURE_FLAG_TOGGLE,
            AuditTargetType.SETTINGS,
            featureKey,
            "Updated rollout percentage for: " + flag.getName(),
            oldPercentage,
            percentage
        );

        log.info("🚩 Updated rollout for '{}': {}% -> {}%", featureKey, oldPercentage, percentage);
        return saved;
    }

    /**
     * حذف feature flag
     */
    @Transactional
    public void delete(String featureKey) {
        FeatureFlag flag = featureFlagRepository.findByFeatureKey(featureKey)
                .orElseThrow(() -> new IllegalArgumentException("Feature flag not found: " + featureKey));

        featureFlagRepository.delete(flag);
        flagCache.remove(featureKey);

        auditLogService.logAsync(
            AuditAction.FEATURE_FLAG_TOGGLE,
            AuditTargetType.SETTINGS,
            featureKey,
            "Deleted feature flag: " + flag.getName()
        );

        log.info("🚩 Deleted feature flag: {}", featureKey);
    }

    // ==================== Predefined Feature Keys ====================

    /**
     * Feature keys معرّفة مسبقاً للاستخدام في الكود
     */
    public static final class Features {
        public static final String AI_IMAGE_GENERATION = "ai.image.generation";
        public static final String AI_TEXT_GENERATION = "ai.text.generation";
        public static final String CHAT_ENABLED = "chat.enabled";
        public static final String VIDEO_CALL = "video.call";
        public static final String PAYMENT_ESCROW = "payment.escrow";
        public static final String WORKER_VERIFICATION = "worker.verification";
        public static final String PUSH_NOTIFICATIONS = "push.notifications";
        public static final String ADVANCED_SEARCH = "search.advanced";
        public static final String DARK_MODE = "ui.dark.mode";
        public static final String MAINTENANCE_MODE = "system.maintenance";
        
        private Features() {} // Prevent instantiation
    }

    // ==================== Helper Methods ====================

    private UUID getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User user) {
                return user.getId();
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }
}
