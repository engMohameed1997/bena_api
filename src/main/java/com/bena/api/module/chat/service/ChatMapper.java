package com.bena.api.module.chat.service;

import com.bena.api.module.chat.entity.Conversation;
import com.bena.api.module.chat.entity.Message;
import com.bena.api.module.chat.entity.UserPresence;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ChatMapper {

    public Map<String, Object> toConversationResponse(Conversation conversation, Map<UUID, UserPresence> presenceByUserId) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", conversation.getId());
        response.put("userId", conversation.getUser().getId().toString());
        response.put("userName", conversation.getUser().getFullName());
        response.put("workerId", conversation.getWorker().getId());
        response.put("workerName", conversation.getWorker().getName());
        response.put("lastMessage", conversation.getLastMessage());
        response.put("lastMessageAt", conversation.getLastMessageAt());
        response.put("userUnreadCount", conversation.getUserUnreadCount());
        response.put("workerUnreadCount", conversation.getWorkerUnreadCount());
        response.put("isActive", conversation.getIsActive());
        response.put("createdAt", conversation.getCreatedAt());

        UserPresence userPresence = presenceByUserId.get(conversation.getUser().getId());
        UUID workerUserId = conversation.getWorker().getUserId();
        UserPresence workerPresence = workerUserId != null ? presenceByUserId.get(workerUserId) : null;

        response.put("userOnline", userPresence != null && Boolean.TRUE.equals(userPresence.getIsOnline()));
        response.put("workerOnline", workerPresence != null && Boolean.TRUE.equals(workerPresence.getIsOnline()));

        return response;
    }

    public Map<String, Object> toMessageResponse(Message message) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", message.getId());
        response.put("conversationId", message.getConversation().getId());
        response.put("senderType", message.getSenderType().name());
        response.put("senderId", message.getSenderId());
        response.put("content", message.getContent());
        response.put("messageType", message.getMessageType().name());
        response.put("attachmentUrl", message.getAttachmentUrl());
        response.put("attachmentName", message.getAttachmentName());
        response.put("attachmentSize", message.getAttachmentSize());
        response.put("isRead", message.getIsRead());
        response.put("isDelivered", message.getIsDelivered());
        response.put("isEdited", message.getIsEdited());
        response.put("editedAt", message.getEditedAt());
        response.put("isDeleted", message.getIsDeleted());
        response.put("createdAt", message.getCreatedAt());

        if (message.getReplyTo() != null && !message.getReplyTo().getIsDeleted()) {
            Map<String, Object> replyTo = new HashMap<>();
            replyTo.put("id", message.getReplyTo().getId());
            replyTo.put("content", message.getReplyTo().getContent().length() > 50
                    ? message.getReplyTo().getContent().substring(0, 50) + "..."
                    : message.getReplyTo().getContent());
            replyTo.put("senderType", message.getReplyTo().getSenderType().name());
            response.put("replyTo", replyTo);
        }

        return response;
    }
}
