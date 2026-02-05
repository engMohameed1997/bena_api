package com.bena.api.module.chat.event;

import com.bena.api.module.chat.entity.Message;

import java.util.UUID;

public record ChatMessageCreatedEvent(
        Long messageId,
        Long conversationId,
        UUID recipientUserId,
        UUID senderUserId,
        String senderName,
        String contentPreview,
        Message.SenderType senderType,
        String senderId,
        String clientMessageId
) {
}
