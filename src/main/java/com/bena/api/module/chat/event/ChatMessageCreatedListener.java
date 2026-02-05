package com.bena.api.module.chat.event;

import com.bena.api.module.chat.entity.Message;
import com.bena.api.module.chat.repository.MessageRepository;
import com.bena.api.module.chat.service.WebSocketPresenceService;
import com.bena.api.module.fcm.service.FcmNotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageCreatedListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final WebSocketPresenceService presenceService;
    private final FcmNotificationSender fcmNotificationSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onChatMessageCreated(ChatMessageCreatedEvent event) {
        // 1. إرسال عبر WebSocket
        sendWebSocketNotification(event);

        // 2. إرسال Push Notification عبر Firebase (direct-to-token)
        sendPushNotification(event);
    }

    private void sendWebSocketNotification(ChatMessageCreatedEvent event) {
        try {
            Message message = messageRepository.findById(event.messageId()).orElse(null);

            Map<String, Object> payload = new HashMap<>();
            payload.put("conversationId", event.conversationId());
            payload.put("type", "NEW_MESSAGE");

            if (message != null) {
                payload.put("id", message.getId());
                payload.put("senderType", message.getSenderType() != null ? message.getSenderType().name() : null);
                payload.put("senderId", message.getSenderId());
                payload.put("content", message.getContent());
                payload.put("messageType", message.getMessageType() != null ? message.getMessageType().name() : null);
                payload.put("attachmentUrl", message.getAttachmentUrl());
                payload.put("attachmentName", message.getAttachmentName());
                payload.put("isRead", message.getIsRead());
                payload.put("isDelivered", message.getIsDelivered());
                payload.put("createdAt", message.getCreatedAt() != null ? message.getCreatedAt().toString() : null);
            } else {
                payload.put("id", event.messageId());
                payload.put("senderType", event.senderType() != null ? event.senderType().name() : null);
                payload.put("senderId", event.senderId());
                payload.put("content", event.contentPreview());
            }

            if (event.clientMessageId() != null && !event.clientMessageId().isBlank()) {
                payload.put("clientMessageId", event.clientMessageId());
            }

            log.info(
                    "WS push chat message: messageId={} conversationId={} recipientUserId={} senderUserId={} ",
                    event.messageId(),
                    event.conversationId(),
                    event.recipientUserId(),
                    event.senderUserId()
            );

            // 1) إرسال للمستلم
            if (event.recipientUserId() != null) {
                messagingTemplate.convertAndSendToUser(
                        event.recipientUserId().toString(),
                        "/queue/messages",
                        payload
                );
            }

            // 2) Echo للمرسل (لإزالة pending عند الإرسال عبر WebSocket)
            if (event.senderUserId() != null && (event.recipientUserId() == null || !event.senderUserId().equals(event.recipientUserId()))) {
                messagingTemplate.convertAndSendToUser(
                        event.senderUserId().toString(),
                        "/queue/messages",
                        payload
                );
            }
        } catch (Exception e) {
            log.warn("Failed to push chat message over WebSocket: {}", e.getMessage());
        }
    }

    private void sendPushNotification(ChatMessageCreatedEvent event) {
        try {
            String title = "رسالة جديدة من " + event.senderName() + " 💬";

            Map<String, String> data = new HashMap<>();
            data.put("type", "chat_message");
            data.put("chatId", event.conversationId().toString());
            data.put("messageId", event.messageId().toString());
            data.put("senderId", event.senderId() != null ? event.senderId().toString() : "");
            data.put("senderName", event.senderName());

            // Direct-to-token FCM (sends to all active tokens)
            fcmNotificationSender.sendChatNotification(
                    event.recipientUserId(),
                    event.senderName(),
                    event.contentPreview(),
                    event.conversationId()
            );

            log.info("FCM notification sent for message {} to user {}",
                    event.messageId(), event.recipientUserId());
        } catch (Exception e) {
            log.warn("Failed to send FCM for new message: {}", e.getMessage());
        }
    }
}
