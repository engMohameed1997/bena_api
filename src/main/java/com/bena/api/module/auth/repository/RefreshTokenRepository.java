package com.bena.api.module.auth.repository;

import com.bena.api.module.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository لإدارة Refresh Tokens
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * البحث عن توكن بواسطة النص
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * البحث عن توكن صالح بواسطة النص
     */
    @Query("SELECT t FROM RefreshToken t WHERE t.token = :token AND t.isRevoked = false AND t.expiresAt > CURRENT_TIMESTAMP")
    Optional<RefreshToken> findValidToken(@Param("token") String token);

    /**
     * جلب جميع التوكنات النشطة للمستخدم
     */
    @Query("SELECT t FROM RefreshToken t WHERE t.user.id = :userId AND t.isRevoked = false AND t.expiresAt > CURRENT_TIMESTAMP ORDER BY t.createdAt DESC")
    List<RefreshToken> findActiveTokensByUserId(@Param("userId") UUID userId);

    /**
     * جلب جميع التوكنات للمستخدم (نشطة وغير نشطة)
     */
    List<RefreshToken> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * إلغاء جميع توكنات المستخدم
     */
    @Modifying
    @Query("UPDATE RefreshToken t SET t.isRevoked = true, t.revokedAt = CURRENT_TIMESTAMP, t.revokedReason = :reason WHERE t.user.id = :userId AND t.isRevoked = false")
    int revokeAllByUserId(@Param("userId") UUID userId, @Param("reason") String reason);

    /**
     * إلغاء توكن معين
     */
    @Modifying
    @Query("UPDATE RefreshToken t SET t.isRevoked = true, t.revokedAt = CURRENT_TIMESTAMP, t.revokedReason = :reason WHERE t.token = :token")
    int revokeByToken(@Param("token") String token, @Param("reason") String reason);

    /**
     * إلغاء جميع التوكنات ما عدا الحالي
     */
    @Modifying
    @Query("UPDATE RefreshToken t SET t.isRevoked = true, t.revokedAt = CURRENT_TIMESTAMP, t.revokedReason = 'Logged out from other devices' WHERE t.user.id = :userId AND t.token != :currentToken AND t.isRevoked = false")
    int revokeAllExceptCurrent(@Param("userId") UUID userId, @Param("currentToken") String currentToken);

    /**
     * عدد الجلسات النشطة للمستخدم
     */
    @Query("SELECT COUNT(t) FROM RefreshToken t WHERE t.user.id = :userId AND t.isRevoked = false AND t.expiresAt > CURRENT_TIMESTAMP")
    long countActiveSessionsByUserId(@Param("userId") UUID userId);

    /**
     * حذف التوكنات المنتهية الصلاحية (للتنظيف الدوري)
     */
    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.expiresAt < :cutoff")
    int deleteExpiredTokens(@Param("cutoff") OffsetDateTime cutoff);

    /**
     * حذف التوكنات الملغاة القديمة (للتنظيف الدوري)
     */
    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.isRevoked = true AND t.revokedAt < :cutoff")
    int deleteOldRevokedTokens(@Param("cutoff") OffsetDateTime cutoff);

    /**
     * التحقق من وجود توكن للمستخدم من نفس الجهاز
     */
    @Query("SELECT t FROM RefreshToken t WHERE t.user.id = :userId AND t.deviceInfo = :deviceInfo AND t.isRevoked = false AND t.expiresAt > CURRENT_TIMESTAMP")
    Optional<RefreshToken> findByUserIdAndDeviceInfo(@Param("userId") UUID userId, @Param("deviceInfo") String deviceInfo);
    @Query("SELECT r FROM RefreshToken r WHERE r.isRevoked = false AND r.expiresAt > CURRENT_TIMESTAMP")
    org.springframework.data.domain.Page<RefreshToken> findAllActiveTokens(org.springframework.data.domain.Pageable pageable);
}
