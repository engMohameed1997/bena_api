package com.bena.api.module.fcm.service;

import com.bena.api.module.fcm.entity.UserFcmToken;
import com.bena.api.module.fcm.repository.UserFcmTokenRepository;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 🔧 FcmTokenService
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * Responsibilities:
 * - Save/Update FCM tokens
 * - Get active tokens for sending notifications
 * - Handle token expiration
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FcmTokenService {

    private final UserFcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Save or Update FCM Token
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Logic:
     * 1. Check if token already exists (UNIQUE constraint)
     * 2. If exists → update last_used_at + is_active = true
     * 3. If new → insert new record
     * 4. User extracted from SecurityContext (JWT)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @Transactional
    public void saveOrUpdateToken(UUID userId, String fcmToken, String deviceType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        fcmTokenRepository.findByFcmToken(fcmToken)
                .ifPresentOrElse(
                        existingToken -> {
                            // Check if token belongs to another user
                            if (!existingToken.getUser().getId().equals(userId)) {
                                log.warn("♻️ Reassigning FCM token from user {} to user {}", 
                                    existingToken.getUser().getId(), userId);
                                existingToken.setUser(user);
                            }

                            // Update existing token
                            existingToken.setLastUsedAt(OffsetDateTime.now());
                            existingToken.setIsActive(true);
                            existingToken.setDeviceType(deviceType);
                            fcmTokenRepository.save(existingToken);
                            log.info("✅ FCM token updated for user: {}", userId);
                        },
                        () -> {
                            // Create new token
                            UserFcmToken newToken = UserFcmToken.builder()
                                    .user(user)
                                    .fcmToken(fcmToken)
                                    .deviceType(deviceType)
                                    .isActive(true)
                                    .lastUsedAt(OffsetDateTime.now())
                                    .build();
                            fcmTokenRepository.save(newToken);
                            log.info("✅ New FCM token saved for user: {}", userId);
                        }
                );
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Get Active Tokens for User
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Used when sending notifications
     * Returns list of active FCM tokens
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @Transactional(readOnly = true)
    public List<String> getActiveTokens(UUID userId) {
        return fcmTokenRepository.findActiveTokensByUserId(userId)
                .stream()
                .map(UserFcmToken::getFcmToken)
                .collect(Collectors.toList());
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Deactivate All User Tokens
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Called on logout
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @Transactional
    public void deactivateUserTokens(UUID userId) {
        fcmTokenRepository.deactivateAllUserTokens(userId);
        log.info("🚫 Deactivated all FCM tokens for user: {}", userId);
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Mark Token as Invalid
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Called when FCM returns invalid token error
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @Transactional
    public void markTokenAsInvalid(String fcmToken) {
        fcmTokenRepository.findByFcmToken(fcmToken).ifPresent(token -> {
            token.setIsActive(false);
            fcmTokenRepository.save(token);
            log.warn("⚠️ FCM token marked as invalid: {}", fcmToken.substring(0, 20) + "...");
        });
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Cleanup Old Inactive Tokens
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Should be scheduled (e.g., weekly)
     * Deletes inactive tokens older than N days
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @Transactional
    public void cleanupOldTokens(int daysOld) {
        fcmTokenRepository.deleteInactiveTokensOlderThan(daysOld);
        log.info("🧹 Cleaned up inactive FCM tokens older than {} days", daysOld);
    }
}
