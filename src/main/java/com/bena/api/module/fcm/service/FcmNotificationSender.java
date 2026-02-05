package com.bena.api.module.fcm.service;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 📤 FcmNotificationSender
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * Purpose: Send FCM notifications to users
 * Strategy: Direct-to-token (NO Topics)
 * Multi-device: Sends to all active tokens
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FcmNotificationSender {

    private final FcmTokenService fcmTokenService;

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Send Notification to User
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * @param userId Target user ID
     * @param title Notification title
     * @param body Notification body
     * @param data Custom data payload (chatId, etc.)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    public void sendToUser(UUID userId, String title, String body, Map<String, String> data) {
        List<String> activeTokens = fcmTokenService.getActiveTokens(userId);

        if (activeTokens.isEmpty()) {
            log.warn("⚠️ No active FCM tokens for user: {}", userId);
            return;
        }

        log.info("📤 Sending notification to {} tokens for user: {}", activeTokens.size(), userId);

        for (String token : activeTokens) {
            sendToToken(token, title, body, data);
        }
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Send to Single Token
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    private void sendToToken(String fcmToken, String title, String body, Map<String, String> data) {
        try {
            // Build notification payload
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            // Build message with data payload
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification)
                    .putAllData(data) // Data for navigation
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setSound("default")
                                    .setChannelId("chat_channel") // ✅ Match Flutter channel
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setSound("default")
                                    .build())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("✅ FCM sent successfully: {}", response);

        } catch (FirebaseMessagingException e) {
            handleFcmError(fcmToken, e);
        } catch (Exception e) {
            log.error("❌ Unexpected error sending FCM: {}", e.getMessage());
        }
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Handle FCM Errors
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Mark invalid tokens as inactive
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    private void handleFcmError(String fcmToken, FirebaseMessagingException e) {
        MessagingErrorCode errorCode = e.getMessagingErrorCode();
        log.error("❌ FCM Error [{}]: {}", errorCode, e.getMessage());

        // Mark token as invalid if unregistered or invalid
        if (errorCode == MessagingErrorCode.UNREGISTERED ||
            errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
            
            fcmTokenService.markTokenAsInvalid(fcmToken);
            log.warn("⚠️ Token marked as invalid: {}...", fcmToken.substring(0, 20));
        }
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Send Chat Message Notification
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * Specialized method for chat notifications
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    public void sendChatNotification(UUID userId, String senderName, String message, Long chatId) {
        Map<String, String> data = Map.of(
                "type", "chat_message",
                "chatId", String.valueOf(chatId),
                "senderName", senderName
        );

        sendToUser(
                userId,
                senderName,
                message,
                data
        );
    }
}
