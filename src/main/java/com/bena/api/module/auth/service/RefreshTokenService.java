package com.bena.api.module.auth.service;

import com.bena.api.module.audit.entity.AuditLog.AuditAction;
import com.bena.api.module.audit.entity.AuditLog.AuditTargetType;
import com.bena.api.module.audit.service.AuditLogService;
import com.bena.api.module.auth.entity.RefreshToken;
import com.bena.api.module.auth.repository.RefreshTokenRepository;
import com.bena.api.module.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * خدمة إدارة Refresh Tokens
 * توفر وظائف إنشاء، تجديد، وإلغاء التوكنات
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    // مدة صلاحية Refresh Token (افتراضي: 30 يوم)
    @Value("${jwt.refresh-expiration:2592000000}")
    private long refreshTokenExpiration;

    // الحد الأقصى للجلسات النشطة للمستخدم الواحد
    @Value("${jwt.max-sessions:5}")
    private int maxActiveSessions;

    /**
     * إنشاء Refresh Token جديد
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // التحقق من عدد الجلسات النشطة
        long activeSessions = refreshTokenRepository.countActiveSessionsByUserId(user.getId());
        if (activeSessions >= maxActiveSessions) {
            // إلغاء أقدم جلسة
            revokeOldestSession(user.getId());
        }

        // توليد توكن فريد
        String tokenValue = generateSecureToken();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .user(user)
                .deviceInfo(getDeviceInfo())
                .ipAddress(getClientIP())
                .expiresAt(OffsetDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .build();

        RefreshToken saved = refreshTokenRepository.save(refreshToken);

        log.info("🔑 Created refresh token for user: {}", user.getEmail());
        return saved;
    }

    /**
     * التحقق من صلاحية Refresh Token وتجديد Access Token
     */
    @Transactional
    public Optional<TokenPair> refreshAccessToken(String refreshTokenValue) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findValidToken(refreshTokenValue);

        if (tokenOpt.isEmpty()) {
            log.warn("⚠️ Invalid or expired refresh token attempt");
            return Optional.empty();
        }

        RefreshToken refreshToken = tokenOpt.get();
        User user = refreshToken.getUser();

        // تحديث وقت آخر استخدام
        refreshToken.setLastUsedAt(OffsetDateTime.now());
        refreshTokenRepository.save(refreshToken);

        // توليد Access Token جديد
        String accessToken = jwtService.generateToken(user.getId(), user.getRole().name());

        log.info("🔄 Refreshed access token for user: {}", user.getEmail());

        return Optional.of(new TokenPair(accessToken, refreshTokenValue));
    }

    /**
     * تبديل Refresh Token (Rotation)
     * يُلغي التوكن القديم ويُنشئ واحداً جديداً
     */
    @Transactional
    public Optional<TokenPair> rotateRefreshToken(String oldRefreshToken) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findValidToken(oldRefreshToken);

        if (tokenOpt.isEmpty()) {
            log.warn("⚠️ Attempted rotation with invalid token");
            return Optional.empty();
        }

        RefreshToken oldToken = tokenOpt.get();
        User user = oldToken.getUser();

        // إلغاء التوكن القديم
        oldToken.revoke("Token rotated");
        refreshTokenRepository.save(oldToken);

        // إنشاء توكن جديد
        RefreshToken newRefreshToken = createRefreshToken(user);
        String accessToken = jwtService.generateToken(user.getId(), user.getRole().name());

        log.info("🔄 Rotated refresh token for user: {}", user.getEmail());

        return Optional.of(new TokenPair(accessToken, newRefreshToken.getToken()));
    }

    /**
     * تسجيل الخروج - إلغاء Refresh Token الحالي
     */
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.revokeByToken(refreshToken, "User logged out");
        log.info("🚪 User logged out, token revoked");
    }

    /**
     * تسجيل الخروج من جميع الأجهزة
     */
    @Transactional
    public void logoutAllDevices(UUID userId) {
        int revokedCount = refreshTokenRepository.revokeAllByUserId(userId, "Logged out from all devices");
        log.info("🚪 Revoked {} refresh tokens for user: {}", revokedCount, userId);
        
        auditLogService.logAsync(
            AuditAction.USER_LOGOUT,
            AuditTargetType.USER,
            userId.toString(),
            "Logged out from all devices (" + revokedCount + " sessions)"
        );
    }

    /**
     * تسجيل الخروج من جميع الأجهزة ما عدا الحالي
     */
    @Transactional
    public void logoutOtherDevices(UUID userId, String currentToken) {
        int revokedCount = refreshTokenRepository.revokeAllExceptCurrent(userId, currentToken);
        log.info("🚪 Revoked {} other sessions for user: {}", revokedCount, userId);
    }

    /**
     * جلب الجلسات النشطة
     */
    public List<RefreshToken> getActiveSessions(UUID userId) {
        return refreshTokenRepository.findActiveTokensByUserId(userId);
    }

    /**
     * عدد الجلسات النشطة
     */
    public long countActiveSessions(UUID userId) {
        return refreshTokenRepository.countActiveSessionsByUserId(userId);
    }

    /**
     * جلب جميع الجلسات النشطة في النظام (للإدارة)
     */
    public org.springframework.data.domain.Page<RefreshToken> getAllActiveSessions(org.springframework.data.domain.Pageable pageable) {
        return refreshTokenRepository.findAllActiveTokens(pageable);
    }

    /**
     * إلغاء التوكنات عند تغيير كلمة المرور (أمان)
     */
    @Transactional
    public void revokeAllOnPasswordChange(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId, "Password changed");
        log.info("🔐 Revoked all tokens due to password change for user: {}", userId);
    }

    /**
     * تنظيف دوري للتوكنات المنتهية (كل 24 ساعة)
     */
    @Scheduled(cron = "0 0 3 * * ?") // 3 صباحاً كل يوم
    @Transactional
    public void cleanupExpiredTokens() {
        OffsetDateTime cutoff = OffsetDateTime.now().minusDays(7);
        int deletedExpired = refreshTokenRepository.deleteExpiredTokens(OffsetDateTime.now());
        int deletedRevoked = refreshTokenRepository.deleteOldRevokedTokens(cutoff);
        log.info("🧹 Cleanup: deleted {} expired and {} old revoked tokens", deletedExpired, deletedRevoked);
    }

    // ==================== Helper Methods ====================

    private void revokeOldestSession(UUID userId) {
        List<RefreshToken> activeSessions = refreshTokenRepository.findActiveTokensByUserId(userId);
        if (!activeSessions.isEmpty()) {
            RefreshToken oldest = activeSessions.get(activeSessions.size() - 1);
            oldest.revoke("Exceeded max sessions");
            refreshTokenRepository.save(oldest);
            log.info("🔒 Revoked oldest session for user {} due to max sessions limit", userId);
        }
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    private String getDeviceInfo() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String userAgent = request.getHeader("User-Agent");
                return userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 500)) : "Unknown";
            }
        } catch (Exception e) {
            // ignore
        }
        return "Unknown";
    }

    private String getClientIP() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            // ignore
        }
        return "Unknown";
    }

    /**
     * Record لزوج التوكنات
     */
    public record TokenPair(String accessToken, String refreshToken) {}
}
