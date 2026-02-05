package com.bena.api.module.fcm.controller;

import com.bena.api.module.fcm.dto.FcmTokenRequest;
import com.bena.api.module.fcm.service.FcmTokenService;
import com.bena.api.module.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 🎮 FcmController
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * Security: JWT Required
 * Purpose: Save FCM token after login
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Slf4j
public class FcmController {

    private final FcmTokenService fcmTokenService;

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Save FCM Token
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Security Rules:
     * - JWT Required (extracted by SecurityConfig)
     * - User ID from SecurityContext (NOT from request)
     * - No userId in FcmTokenRequest DTO
     * 
     * Flow:
     * 1. Flutter sends token after successful login
     * 2. Backend extracts userId from JWT
     * 3. Token saved to user_fcm_tokens table
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @PostMapping("/fcm-token")
    public ResponseEntity<?> saveFcmToken(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody FcmTokenRequest request
    ) {
        log.info("📱 Saving FCM token for user: {}", user.getId());

        fcmTokenService.saveOrUpdateToken(
                user.getId(),
                request.getFcmToken(),
                request.getDeviceType()
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "FCM token saved successfully"
        ));
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Deactivate User Tokens (Logout)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    @DeleteMapping("/fcm-token")
    public ResponseEntity<?> deactivateTokens(@AuthenticationPrincipal User user) {
        log.info("🚫 Deactivating FCM tokens for user: {}", user.getId());

        fcmTokenService.deactivateUserTokens(user.getId());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "FCM tokens deactivated"
        ));
    }
}
