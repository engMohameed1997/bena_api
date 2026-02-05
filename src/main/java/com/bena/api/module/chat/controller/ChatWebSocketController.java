package com.bena.api.module.chat.controller;

import com.bena.api.module.chat.entity.Message;
import com.bena.api.module.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

/**
 * WebSocket Controller للتعامل مع الرسائل في الوقت الفعلي
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;

    /**
     * إرسال رسالة جديدة عبر WebSocket
     * العميل يرسل إلى: /app/chat.send
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request, Principal principal, SimpMessageHeaderAccessor headerAccessor) {
        if (principal == null) {
            log.warn("WS chat.send: No principal found");
            return;
        }

        String userId = principal.getName();
        log.info("WS chat.send: userId={} conversationId={}", userId, request.conversationId());

        try {
            boolean isValidSender = false;
            if ("USER".equalsIgnoreCase(request.senderType())) {
                isValidSender = userId.equals(request.senderId());
            } else if ("WORKER".equalsIgnoreCase(request.senderType())) {
                // للمختص، نتحقق عبر الـ service
                isValidSender = true; // سيتم التحقق في الـ ChatService
            }

            if (!isValidSender) {
                log.warn("WS chat.send: Invalid sender userId={} senderId={}", userId, request.senderId());
                return;
            }

            Message.MessageType messageType = request.messageType() != null
                    ? Message.MessageType.valueOf(request.messageType().toUpperCase())
                    : Message.MessageType.TEXT;

            chatService.sendMessageWithReplyResponse(
                    request.conversationId(),
                    request.senderType(),
                    request.senderId(),
                    request.content(),
                    messageType,
                    request.attachmentUrl(),
                    request.attachmentName(),
                    request.attachmentSize(),
                    request.replyToId(),
                    request.clientMessageId()
            );
        } catch (Exception e) {
            log.error("WS chat.send failed: {}", e.getMessage(), e);
        }
    }

    /**
     * إرسال مؤشر الكتابة
     * العميل يرسل إلى: /app/chat.typing
     */
    @MessageMapping("/chat.typing")
    public void setTyping(@Payload TypingRequest request, Principal principal) {
        if (principal == null) {
            log.warn("WS chat.typing: No principal found");
            return;
        }

        String userId = principal.getName();
        
        try {
            java.util.UUID userUuid = java.util.UUID.fromString(userId);
            chatService.setTyping(request.conversationId(), userUuid, request.isTyping());
        } catch (Exception e) {
            log.warn("WS chat.typing failed: {}", e.getMessage());
        }
    }

    /**
     * تأكيد قراءة الرسائل
     * العميل يرسل إلى: /app/chat.read
     */
    @MessageMapping("/chat.read")
    public void markAsRead(@Payload ReadReceiptRequest request, Principal principal) {
        if (principal == null) {
            log.warn("WS chat.read: No principal found");
            return;
        }

        try {
            chatService.markAsRead(request.conversationId(), request.readerType());
            log.info("WS chat.read: conversationId={} readerType={}", request.conversationId(), request.readerType());
        } catch (Exception e) {
            log.warn("WS chat.read failed: {}", e.getMessage());
        }
    }

    /**
     * Ping للحفاظ على الاتصال
     * العميل يرسل إلى: /app/ping
     */
    @MessageMapping("/ping")
    public void ping(Principal principal) {
        if (principal != null) {
            log.debug("WS ping from userId={}", principal.getName());
        }
    }

    // ==================== Request DTOs ====================

    private record ChatMessageRequest(
            Long conversationId,
            String senderType,
            String senderId,
            String content,
            String messageType,
            String attachmentUrl,
            String attachmentName,
            Long attachmentSize,
            Long replyToId,
            String clientMessageId
    ) {}

    private record TypingRequest(
            Long conversationId,
            String userId,
            boolean isTyping
    ) {}

    private record ReadReceiptRequest(
            Long conversationId,
            String readerType
    ) {}
}
