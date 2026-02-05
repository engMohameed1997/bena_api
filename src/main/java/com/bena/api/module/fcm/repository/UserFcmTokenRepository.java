package com.bena.api.module.fcm.repository;

import com.bena.api.module.fcm.entity.UserFcmToken;
import com.bena.api.module.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 🗄️ UserFcmTokenRepository
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@Repository
public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, Long> {

    /**
     * Find token by exact match
     */
    Optional<UserFcmToken> findByFcmToken(String fcmToken);

    /**
     * Get all active tokens for a specific user
     */
    @Query("SELECT t FROM UserFcmToken t WHERE t.user.id = :userId AND t.isActive = true")
    List<UserFcmToken> findActiveTokensByUserId(@Param("userId") UUID userId);

    /**
     * Deactivate all tokens for a user (used for logout)
     */
    @Modifying
    @Query("UPDATE UserFcmToken t SET t.isActive = false WHERE t.user.id = :userId")
    void deactivateAllUserTokens(@Param("userId") UUID userId);

    /**
     * Delete inactive tokens older than specified days
     */
    @Modifying
    @Query("DELETE FROM UserFcmToken t WHERE t.isActive = false AND t.lastUsedAt < CURRENT_TIMESTAMP - :days DAY")
    void deleteInactiveTokensOlderThan(@Param("days") int days);
}
