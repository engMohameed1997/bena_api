package com.bena.api.module.chat.service;

import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.repository.UserRepository;
import com.bena.api.module.worker.entity.Worker;
import com.bena.api.module.worker.repository.WorkerRepository;
import com.bena.api.module.chat.entity.Conversation;
import com.bena.api.module.chat.entity.Message;
import com.bena.api.module.chat.entity.TypingIndicator;
import com.bena.api.module.chat.entity.UserPresence;
import com.bena.api.module.chat.event.ChatMessageCreatedEvent;
import com.bena.api.module.chat.repository.ConversationRepository;
import com.bena.api.module.chat.repository.MessageRepository;
import com.bena.api.module.chat.repository.TypingIndicatorRepository;
import com.bena.api.module.chat.repository.UserPresenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final UserPresenceRepository presenceRepository;
    private final TypingIndicatorRepository typingRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMapper chatMapper;

    /**
     * بدء أو جلب محادثة موجودة
     */
    @Transactional
    public Conversation getOrCreateConversation(UUID userId, Long workerId) {
        return conversationRepository.findByUser_IdAndWorker_Id(userId, workerId)
                .orElseGet(() -> createConversation(userId, workerId));
    }

    public Map<String, Object> getOrCreateConversationResponse(UUID userId, Long workerId) {
        Conversation conversation = getOrCreateConversation(userId, workerId);
        return toConversationResponse(conversation);
    }

    /**
     * إنشاء محادثة جديدة
     */
    @Transactional
    public Conversation createConversation(UUID userId, Long workerId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("المختص غير موجود"));

        Conversation conversation = Conversation.builder()
                .user(user)
                .worker(worker)
                .isActive(true)
                .userUnreadCount(0)
                .workerUnreadCount(0)
                .build();

        return conversationRepository.save(conversation);
    }

    /**
     * إرسال رسالة
     */
    @Transactional
    public Message sendMessage(Long conversationId, String senderType, String senderId,
                               String content, Message.MessageType messageType, String attachmentUrl) {
        return sendMessage(conversationId, senderType, senderId, content, messageType, attachmentUrl, null, null, null);
    }

    /**
     * إرسال رسالة مع مرفق ورد
     */
    @Transactional
    public Message sendMessage(Long conversationId, String senderType, String senderId,
                               String content, Message.MessageType messageType, String attachmentUrl,
                               String attachmentName, Long attachmentSize, Long replyToId) {
        return sendMessage(conversationId, senderType, senderId, content, messageType, attachmentUrl,
                attachmentName, attachmentSize, replyToId, null);
    }

    @Transactional
    public Message sendMessage(Long conversationId, String senderType, String senderId,
                               String content, Message.MessageType messageType, String attachmentUrl,
                               String attachmentName, Long attachmentSize, Long replyToId,
                               String clientMessageId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("المحادثة غير موجودة"));

        Message.SenderType type = Message.SenderType.valueOf(senderType.toUpperCase());

        Message.MessageBuilder messageBuilder = Message.builder()
                .conversation(conversation)
                .senderType(type)
                .senderId(senderId)
                .content(content)
                .messageType(messageType != null ? messageType : Message.MessageType.TEXT)
                .attachmentUrl(attachmentUrl)
                .attachmentName(attachmentName)
                .attachmentSize(attachmentSize)
                .isRead(false)
                .isDelivered(false)
                .isDeleted(false)
                .isEdited(false);

        // إضافة الرد إذا وجد
        if (replyToId != null) {
            Message replyTo = messageRepository.findById(replyToId).orElse(null);
            if (replyTo != null && replyTo.getConversation().getId().equals(conversationId)) {
                messageBuilder.replyTo(replyTo);
            }
        }

        Message message = messageBuilder.build();
        message = messageRepository.save(message);

        // تحديث المحادثة
        conversation.setLastMessage(content.length() > 100 ? content.substring(0, 100) + "..." : content);
        conversation.setLastMessageAt(LocalDateTime.now());

        if (type == Message.SenderType.USER) {
            conversation.setWorkerUnreadCount(conversation.getWorkerUnreadCount() + 1);
        } else {
            conversation.setUserUnreadCount(conversation.getUserUnreadCount() + 1);
        }

        conversationRepository.save(conversation);

        // تحديد المستلم
        UUID recipientId;
        UUID senderUserId;
        String senderName;
        if (type == Message.SenderType.USER) {
            if (conversation.getWorker() == null || conversation.getWorker().getUserId() == null) {
                throw new RuntimeException("المستلم غير موجود");
            }
            recipientId = conversation.getWorker().getUserId();
            senderUserId = conversation.getUser().getId();
            senderName = conversation.getUser().getFullName();
        } else {
            recipientId = conversation.getUser().getId();
            senderUserId = conversation.getWorker() != null ? conversation.getWorker().getUserId() : null;
            senderName = conversation.getWorker() != null ? conversation.getWorker().getName() : "";
        }

        // نشر الحدث
        eventPublisher.publishEvent(new ChatMessageCreatedEvent(
                message.getId(),
                conversationId,
                recipientId,
                senderUserId,
                senderName,
                content.length() > 100 ? content.substring(0, 100) + "..." : content,
                type,
                senderId,
                clientMessageId
        ));

        return message;
    }

    @Transactional
    public Map<String, Object> sendMessageResponse(Long conversationId, String senderType, String senderId,
                                                   String content, Message.MessageType messageType, String attachmentUrl) {
        Message message = sendMessage(conversationId, senderType, senderId, content, messageType, attachmentUrl);
        return toMessageResponse(message);
    }

    @Transactional
    public Map<String, Object> sendMessageWithReplyResponse(Long conversationId, String senderType, String senderId,
                                                            String content, Message.MessageType messageType,
                                                            String attachmentUrl, String attachmentName,
                                                            Long attachmentSize, Long replyToId) {
        Message message = sendMessage(conversationId, senderType, senderId, content, messageType,
                attachmentUrl, attachmentName, attachmentSize, replyToId);
        return toMessageResponse(message);
    }

    @Transactional
    public Map<String, Object> sendMessageWithReplyResponse(Long conversationId, String senderType, String senderId,
                                                            String content, Message.MessageType messageType,
                                                            String attachmentUrl, String attachmentName,
                                                            Long attachmentSize, Long replyToId,
                                                            String clientMessageId) {
        Message message = sendMessage(conversationId, senderType, senderId, content, messageType,
                attachmentUrl, attachmentName, attachmentSize, replyToId, clientMessageId);
        return toMessageResponse(message);
    }

    /**
     * تعديل رسالة
     */
    @Transactional
    public Message editMessage(Long messageId, String newContent, String editorId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("الرسالة غير موجودة"));

        if (!message.getSenderId().equals(editorId)) {
            throw new RuntimeException("لا يمكنك تعديل هذه الرسالة");
        }

        if (message.getIsDeleted()) {
            throw new RuntimeException("لا يمكن تعديل رسالة محذوفة");
        }

        message.setContent(newContent);
        message.setIsEdited(true);
        message.setEditedAt(LocalDateTime.now());

        message = messageRepository.save(message);

        // إرسال تحديث عبر WebSocket
        broadcastMessageUpdate(message, "EDITED");

        return message;
    }

    /**
     * حذف رسالة (Soft Delete)
     */
    @Transactional
    public void deleteMessage(Long messageId, String deleterId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("الرسالة غير موجودة"));

        if (!message.getSenderId().equals(deleterId)) {
            throw new RuntimeException("لا يمكنك حذف هذه الرسالة");
        }

        message.setIsDeleted(true);
        message.setDeletedAt(LocalDateTime.now());
        message.setContent("تم حذف هذه الرسالة");

        messageRepository.save(message);

        // إرسال تحديث عبر WebSocket
        broadcastMessageUpdate(message, "DELETED");
    }

    /**
     * تأكيد التسليم
     */
    @Transactional
    public void confirmDelivery(Long conversationId) {
        messageRepository.markAsDelivered(conversationId);
    }

    /**
     * البحث في الرسائل
     */
    @Transactional(readOnly = true)
    public Page<Map<String, Object>> searchMessages(Long conversationId, String query, Pageable pageable) {
        return messageRepository.searchMessages(conversationId, query, pageable)
                .map(this::toMessageResponse);
    }

    /**
     * البحث في جميع محادثات المستخدم
     */
    @Transactional(readOnly = true)
    public Page<Map<String, Object>> searchUserMessages(UUID userId, String query, Pageable pageable) {
        return messageRepository.searchUserMessages(userId, query, pageable)
                .map(this::toMessageResponse);
    }

    /**
     * جلب رسائل المحادثة
     */
    @Transactional(readOnly = true)
    public Page<Map<String, Object>> getMessages(Long conversationId, Pageable pageable) {
        return messageRepository.findByConversation_IdAndIsDeletedFalseOrderByCreatedAtDesc(conversationId, pageable)
                .map(this::toMessageResponse);
    }

    /**
     * جلب محادثات المستخدم
     */
    @Transactional(readOnly = true)
    public Page<Map<String, Object>> getUserConversations(UUID userId, Pageable pageable) {
        Page<Conversation> page = conversationRepository.findByUser_IdOrderByLastMessageAtDesc(userId, pageable);
        Map<UUID, UserPresence> presenceByUserId = loadPresenceForConversations(page.getContent());
        return page.map(c -> toConversationResponse(c, presenceByUserId));
    }

    /**
     * جلب محادثات المختص
     */
    @Transactional(readOnly = true)
    public Page<Map<String, Object>> getWorkerConversations(Long workerId, Pageable pageable) {
        Page<Conversation> page = conversationRepository.findByWorker_IdOrderByLastMessageAtDesc(workerId, pageable);
        Map<UUID, UserPresence> presenceByUserId = loadPresenceForConversations(page.getContent());
        return page.map(c -> toConversationResponse(c, presenceByUserId));
    }

    /**
     * تحديد الرسائل كمقروءة
     */
    @Transactional
    public void markAsRead(Long conversationId, String readerType) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("المحادثة غير موجودة"));

        Message.SenderType senderType = readerType.equalsIgnoreCase("USER")
                ? Message.SenderType.WORKER
                : Message.SenderType.USER;

        messageRepository.markAsReadBySenderType(conversationId, senderType);

        if (readerType.equalsIgnoreCase("USER")) {
            conversation.setUserUnreadCount(0);
        } else {
            conversation.setWorkerUnreadCount(0);
        }
        conversationRepository.save(conversation);

        // إرسال تحديث حالة القراءة للطرف الآخر عبر WebSocket
        try {
            UUID recipientId;
            if (readerType.equalsIgnoreCase("USER")) {
                // القارئ هو المستخدم، أرسل للمختص
                recipientId = conversation.getWorker().getUserId();
            } else {
                // القارئ هو المختص، أرسل للمستخدم
                recipientId = conversation.getUser().getId();
            }

            if (recipientId != null) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("conversationId", conversationId);
                payload.put("readerType", readerType);
                payload.put("type", "READ_STATUS");

                messagingTemplate.convertAndSendToUser(
                        recipientId.toString(),
                        "/queue/read-status",
                        payload
                );
            }
        } catch (Exception e) {
            log.warn("Failed to send read status via WebSocket: {}", e.getMessage());
        }
    }

    /**
     * عدد الرسائل غير المقروءة للمستخدم
     */
    public long getUserUnreadCount(UUID userId) {
        Long count = conversationRepository.getTotalUserUnreadCount(userId);
        return count != null ? count : 0;
    }

    /**
     * عدد الرسائل غير المقروءة للمختص
     */
    public long getWorkerUnreadCount(Long workerId) {
        Long count = conversationRepository.getTotalWorkerUnreadCount(workerId);
        return count != null ? count : 0;
    }

    // ==================== مؤشر الكتابة ====================

    /**
     * تحديث حالة الكتابة
     */
    @Transactional
    public void setTyping(Long conversationId, UUID userId, boolean isTyping) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("المحادثة غير موجودة"));

        TypingIndicator indicator = typingRepository.findByConversation_IdAndUser_Id(conversationId, userId)
                .orElseGet(() -> TypingIndicator.builder()
                        .conversation(conversation)
                        .user(user)
                        .build());

        indicator.setIsTyping(isTyping);
        indicator.setStartedAt(isTyping ? LocalDateTime.now() : null);
        typingRepository.save(indicator);

        // إرسال عبر WebSocket
        UUID recipientId = conversation.getUser().getId().equals(userId)
                ? conversation.getWorker().getUserId()
                : conversation.getUser().getId();

        Map<String, Object> payload = new HashMap<>();
        payload.put("conversationId", conversationId);
        payload.put("userId", userId.toString());
        payload.put("userName", user.getFullName());
        payload.put("isTyping", isTyping);

        messagingTemplate.convertAndSendToUser(
                recipientId.toString(),
                "/queue/typing",
                payload
        );
    }

    /**
     * جلب المستخدمين الذين يكتبون في محادثة
     */
    public List<Map<String, Object>> getTypingUsers(Long conversationId) {
        return typingRepository.findByConversation_IdAndIsTypingTrue(conversationId)
                .stream()
                .map(t -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", t.getUser().getId().toString());
                    map.put("userName", t.getUser().getFullName());
                    return map;
                })
                .collect(Collectors.toList());
    }

    // ==================== حالة الاتصال ====================

    /**
     * تحديث حالة الاتصال
     */
    @Transactional
    public void updatePresence(UUID userId, boolean isOnline, Long currentConversationId) {
        UserPresence presence = presenceRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
                    return UserPresence.builder()
                            .user(user)
                            .build();
                });

        presence.setIsOnline(isOnline);
        presence.setLastSeenAt(LocalDateTime.now());
        if (currentConversationId != null) {
            presence.setCurrentConversationId(currentConversationId);
        }
        presenceRepository.save(presence);

        // إرسال تحديث الحالة للأصدقاء/المحادثات النشطة (Broadcast)
        // للإختصار، سنرسل تنبيه عام للمستخدم (يمكن تحسينه لإرساله فقط للمهتمين)
        // لكن هنا، العميل (Flutter) يستمع لـ /user/queue/messages بشكل عام
        // سنرسل رسالة "نظام" خفية
        /*
        Map<String, Object> presencePayload = new HashMap<>();
        presencePayload.put("type", "PRESENCE");
        presencePayload.put("userId", userId.toString());
        presencePayload.put("isOnline", isOnline);
        // هذا يتطلب معرفة من يجب إبلاغه (المحادثات المفتوحة مع هذا المستخدم)
        // سيتم الاعتماد على الـ Polling عند فتح المحادثة حالياً لتبسيط الـ Traffic
        */
    }

    /**
     * التحقق من حالة اتصال مستخدم
     */
    public Map<String, Object> getUserPresence(UUID userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId.toString());

        UserPresence presence = presenceRepository.findByUser_Id(userId).orElse(null);
        if (presence != null) {
            result.put("isOnline", presence.getIsOnline());
            result.put("lastSeenAt", presence.getLastSeenAt());
        } else {
            result.put("isOnline", false);
            result.put("lastSeenAt", null);
        }

        return result;
    }

    private Map<UUID, UserPresence> loadPresenceForConversations(List<Conversation> conversations) {
        if (conversations == null || conversations.isEmpty()) {
            return java.util.Map.of();
        }

        List<UUID> userIds = conversations.stream()
                .flatMap(c -> java.util.stream.Stream.of(
                        c.getUser() != null ? c.getUser().getId() : null,
                        c.getWorker() != null ? c.getWorker().getUserId() : null
                ))
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        if (userIds.isEmpty()) {
            return java.util.Map.of();
        }

        return presenceRepository.findAllByUser_IdIn(userIds)
                .stream()
                .filter(p -> p.getUser() != null && p.getUser().getId() != null)
                .collect(java.util.stream.Collectors.toMap(p -> p.getUser().getId(), p -> p, (a, b) -> a));
    }

    /**
     * جلب حالة الطرف الآخر في المحادثة
     */
    public Map<String, Object> getOtherPartyPresence(Long conversationId, UUID currentUserId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("المحادثة غير موجودة"));

        UUID otherUserId = conversation.getUser().getId().equals(currentUserId)
                ? conversation.getWorker().getUserId()
                : conversation.getUser().getId();

        return getUserPresence(otherUserId);
    }

    // ==================== المساعدون ====================

    private void broadcastMessageUpdate(Message message, String action) {
        Conversation conversation = message.getConversation();

        Map<String, Object> payload = toMessageResponse(message);
        payload.put("action", action);

        // إرسال للمستخدم
        messagingTemplate.convertAndSendToUser(
                conversation.getUser().getId().toString(),
                "/queue/message-updates",
                payload
        );

        // إرسال للمختص
        messagingTemplate.convertAndSendToUser(
                conversation.getWorker().getUserId().toString(),
                "/queue/message-updates",
                payload
        );
    }

    public Map<String, Object> toConversationResponse(Conversation conversation) {
        Map<UUID, UserPresence> presenceByUserId = java.util.Map.of();
        return toConversationResponse(conversation, presenceByUserId);
    }

    public Map<String, Object> toConversationResponse(Conversation conversation, Map<UUID, UserPresence> presenceByUserId) {
        return chatMapper.toConversationResponse(conversation, presenceByUserId);
    }

    public Map<String, Object> toMessageResponse(Message message) {
        return chatMapper.toMessageResponse(message);
    }
}
