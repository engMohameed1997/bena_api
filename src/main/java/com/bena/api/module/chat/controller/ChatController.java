package com.bena.api.module.chat.controller;

import com.bena.api.module.chat.entity.Message;
import com.bena.api.module.chat.repository.ConversationRepository;
import com.bena.api.module.chat.service.ChatService;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final WorkerRepository workerRepository;
    private final ConversationRepository conversationRepository;

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(403).body(Map.of(
                "success", false,
                "message", "غير مصرح لك بهذا الإجراء"
        ));
    }

    private Long getWorkerIdForUser(User user) {
        if (user == null || user.getId() == null) return null;
        return workerRepository.findByUserId(user.getId())
                .map(w -> w.getId())
                .orElse(null);
    }

    private boolean isConversationParticipant(Long conversationId, User principal) {
        if (conversationId == null || principal == null || principal.getId() == null) return false;
        boolean allowed = conversationRepository.isParticipant(conversationId, principal.getId());
        if (!allowed) {
            log.warn("Chat access denied: conversationId={} principalId={}", conversationId, principal.getId());
        }
        return allowed;
    }

    private boolean canAccessWorkerId(Long workerId, User principal) {
        if (workerId == null || principal == null || principal.getId() == null) return false;
        Long ownedWorkerId = getWorkerIdForUser(principal);
        return ownedWorkerId != null && ownedWorkerId.equals(workerId);
    }

    /**
     * بدء أو جلب محادثة
     */
    @PostMapping("/conversation")
    public ResponseEntity<?> getOrCreateConversation(
            @AuthenticationPrincipal User principal,
            @RequestParam UUID userId,
            @RequestParam Long workerId) {
        try {
            if (principal == null || principal.getId() == null || !principal.getId().equals(userId)) {
                return forbidden();
            }
            Map<String, Object> conversation = chatService.getOrCreateConversationResponse(userId, workerId);
            return ResponseEntity.ok(Map.of("success", true, "data", conversation));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private record SendMessageRequest(
            Long conversationId,
            String senderType,
            String senderId,
            String content,
            String messageType,
            String attachmentUrl,
            String attachmentName,
            Long attachmentSize,
            Long replyToId
    ) {
    }

    @PostMapping(value = "/messages", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendMessageJson(
            @AuthenticationPrincipal User principal,
            @RequestBody SendMessageRequest body
    ) {
        try {
            if (principal == null || principal.getId() == null) {
                return forbidden();
            }
            if (body == null || body.conversationId() == null || body.senderType() == null || body.senderId() == null || body.content() == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "بيانات غير صحيحة"));
            }

            if (!isConversationParticipant(body.conversationId(), principal)) {
                return forbidden();
            }

            Message.SenderType st = Message.SenderType.valueOf(body.senderType().toUpperCase());
            if (st == Message.SenderType.USER) {
                if (!principal.getId().toString().equals(body.senderId())) return forbidden();
            } else {
                Long workerId = getWorkerIdForUser(principal);
                if (workerId == null) return forbidden();
                if (!workerId.toString().equals(body.senderId())) return forbidden();
            }

            Message.MessageType type = body.messageType() != null ?
                    Message.MessageType.valueOf(body.messageType().toUpperCase()) : Message.MessageType.TEXT;

            Map<String, Object> message = chatService.sendMessageWithReplyResponse(
                    body.conversationId(),
                    body.senderType(),
                    body.senderId(),
                    body.content(),
                    type,
                    body.attachmentUrl(),
                    body.attachmentName(),
                    body.attachmentSize(),
                    body.replyToId()
            );

            return ResponseEntity.ok(Map.of("success", true, "data", message, "message", "تم إرسال الرسالة"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "بيانات غير صحيحة"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * إرسال رسالة
     */
    @PostMapping(value = "/messages", params = {"conversationId", "senderType", "senderId", "content"})
    public ResponseEntity<?> sendMessage(
            @AuthenticationPrincipal User principal,
            @RequestParam Long conversationId,
            @RequestParam String senderType,
            @RequestParam String senderId,
            @RequestParam String content,
            @RequestParam(required = false) String messageType,
            @RequestParam(required = false) String attachmentUrl,
            @RequestParam(required = false) String attachmentName,
            @RequestParam(required = false) Long attachmentSize,
            @RequestParam(required = false) Long replyToId) {
        try {
            if (principal == null || principal.getId() == null) {
                return forbidden();
            }

            if (!isConversationParticipant(conversationId, principal)) {
                return forbidden();
            }

            if (senderType == null) return forbidden();
            Message.SenderType st = Message.SenderType.valueOf(senderType.toUpperCase());

            if (st == Message.SenderType.USER) {
                if (!principal.getId().toString().equals(senderId)) return forbidden();
            } else {
                Long workerId = getWorkerIdForUser(principal);
                if (workerId == null) return forbidden();
                if (!workerId.toString().equals(senderId)) return forbidden();
            }

            Message.MessageType type = messageType != null ? 
                    Message.MessageType.valueOf(messageType.toUpperCase()) : Message.MessageType.TEXT;
            
            Map<String, Object> message = chatService.sendMessageWithReplyResponse(
                    conversationId, senderType, senderId, content, type, 
                    attachmentUrl, attachmentName, attachmentSize, replyToId);
            return ResponseEntity.ok(Map.of("success", true, "data", message, "message", "تم إرسال الرسالة"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * تعديل رسالة
     */
    @PutMapping("/messages/{messageId}")
    public ResponseEntity<?> editMessage(
            @PathVariable Long messageId,
            @RequestParam String content,
            @RequestParam String editorId,
            @AuthenticationPrincipal User principal) {
        try {
            if (principal == null || principal.getId() == null) {
                return forbidden();
            }
            final String principalUserId = principal.getId().toString();

            if (principalUserId.equals(editorId)) {
                // OK as USER
            } else {
                Long workerId = getWorkerIdForUser(principal);
                if (workerId == null || !workerId.toString().equals(editorId)) return forbidden();
            }

            var message = chatService.editMessage(messageId, content, editorId);
            return ResponseEntity.ok(Map.of(
                    "success", true, 
                    "data", chatService.toMessageResponse(message),
                    "message", "تم تعديل الرسالة"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * حذف رسالة
     */
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(
            @PathVariable Long messageId,
            @RequestParam String deleterId,
            @AuthenticationPrincipal User principal) {
        try {
            if (principal == null || principal.getId() == null) {
                return forbidden();
            }
            final String principalUserId = principal.getId().toString();

            if (principalUserId.equals(deleterId)) {
                // OK as USER
            } else {
                Long workerId = getWorkerIdForUser(principal);
                if (workerId == null || !workerId.toString().equals(deleterId)) return forbidden();
            }

            chatService.deleteMessage(messageId, deleterId);
            return ResponseEntity.ok(Map.of("success", true, "message", "تم حذف الرسالة"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * جلب رسائل المحادثة
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<?> getMessages(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal User principal,
            Pageable pageable) {
        try {
            if (principal == null || principal.getId() == null) {
                return forbidden();
            }
            if (!isConversationParticipant(conversationId, principal)) {
                return forbidden();
            }
            Page<Map<String, Object>> messages = chatService.getMessages(conversationId, pageable);
            return ResponseEntity.ok(Map.of("success", true, "data", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * البحث في رسائل محادثة
     */
    @GetMapping("/conversations/{conversationId}/search")
    public ResponseEntity<?> searchMessages(
            @PathVariable Long conversationId,
            @RequestParam String query,
            @AuthenticationPrincipal User principal,
            Pageable pageable) {
        try {
            if (principal == null || principal.getId() == null) {
                return forbidden();
            }
            if (!isConversationParticipant(conversationId, principal)) {
                return forbidden();
            }
            Page<Map<String, Object>> messages = chatService.searchMessages(conversationId, query, pageable);
            return ResponseEntity.ok(Map.of("success", true, "data", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * البحث في جميع محادثات المستخدم
     */
    @GetMapping("/user/{userId}/search")
    public ResponseEntity<?> searchUserMessages(
            @PathVariable UUID userId,
            @RequestParam String query,
            @AuthenticationPrincipal User principal,
            Pageable pageable) {
        try {
            if (principal == null || principal.getId() == null || !principal.getId().equals(userId)) {
                return forbidden();
            }
            Page<Map<String, Object>> messages = chatService.searchUserMessages(userId, query, pageable);
            return ResponseEntity.ok(Map.of("success", true, "data", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * جلب محادثات المستخدم
     */
    @GetMapping("/user/{userId}/conversations")
    public ResponseEntity<?> getUserConversations(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User principal,
            Pageable pageable) {
        try {
            if (principal == null || principal.getId() == null || !principal.getId().equals(userId)) {
                return forbidden();
            }
            Page<Map<String, Object>> conversations = chatService.getUserConversations(userId, pageable);
            return ResponseEntity.ok(Map.of("success", true, "data", conversations));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * جلب محادثات المختص
     */
    @GetMapping("/worker/{workerId}/conversations")
    public ResponseEntity<?> getWorkerConversations(
            @PathVariable Long workerId,
            @AuthenticationPrincipal User principal,
            Pageable pageable) {
        try {
            if (!canAccessWorkerId(workerId, principal)) {
                return forbidden();
            }
            Page<Map<String, Object>> conversations = chatService.getWorkerConversations(workerId, pageable);
            return ResponseEntity.ok(Map.of("success", true, "data", conversations));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * تحديد الرسائل كمقروءة
     */
    @PostMapping(value = "/conversations/{conversationId}/read", params = "readerType")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long conversationId,
            @RequestParam String readerType,
            @AuthenticationPrincipal User principal) {
        try {
            if (principal == null || principal.getId() == null) {
                return forbidden();
            }
            if (!isConversationParticipant(conversationId, principal)) {
                return forbidden();
            }

            if (readerType == null) return forbidden();

            String actualType = conversationRepository.getParticipantType(conversationId, principal.getId());
            if (actualType == null || !actualType.equalsIgnoreCase(readerType)) {
                return forbidden();
            }

            chatService.markAsRead(conversationId, readerType);
            return ResponseEntity.ok(Map.of("success", true, "message", "تم تحديد الرسائل كمقروءة"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/conversations/{conversationId}/read")
    public ResponseEntity<?> markAsReadAuto(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal User principal) {
        try {
            if (principal == null || principal.getId() == null) {
                return forbidden();
            }
            if (!isConversationParticipant(conversationId, principal)) {
                return forbidden();
            }

            String readerType = conversationRepository.getParticipantType(conversationId, principal.getId());

            if (readerType == null) {
                return forbidden();
            }

            chatService.markAsRead(conversationId, readerType);
            return ResponseEntity.ok(Map.of("success", true, "message", "تم تحديد الرسائل كمقروءة"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * تأكيد التسليم
     */
    @PostMapping("/conversations/{conversationId}/delivered")
    public ResponseEntity<?> confirmDelivery(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal User principal) {
        try {
            if (principal == null || principal.getId() == null) {
                return forbidden();
            }
            if (!isConversationParticipant(conversationId, principal)) {
                return forbidden();
            }
            chatService.confirmDelivery(conversationId);
            return ResponseEntity.ok(Map.of("success", true, "message", "تم تأكيد التسليم"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * عدد الرسائل غير المقروءة
     */
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) Long workerId,
            @AuthenticationPrincipal User principal) {
        try {
            if (principal == null || principal.getId() == null) {
                return forbidden();
            }
            long count = 0;
            if (userId != null) {
                if (!principal.getId().equals(userId)) {
                    return forbidden();
                }
                count = chatService.getUserUnreadCount(userId);
            } else if (workerId != null) {
                if (!canAccessWorkerId(workerId, principal)) {
                    return forbidden();
                }
                count = chatService.getWorkerUnreadCount(workerId);
            }
            return ResponseEntity.ok(Map.of("success", true, "count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==================== مؤشر الكتابة ====================

    /**
     * تحديث حالة الكتابة
     */
    @PostMapping("/conversations/{conversationId}/typing")
    public ResponseEntity<?> setTyping(
            @PathVariable Long conversationId,
            @RequestParam UUID userId,
            @RequestParam boolean isTyping,
            @AuthenticationPrincipal User principal) {
        try {
            if (principal == null || principal.getId() == null || !principal.getId().equals(userId)) {
                return forbidden();
            }
            if (!isConversationParticipant(conversationId, principal)) {
                return forbidden();
            }
            chatService.setTyping(conversationId, userId, isTyping);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * جلب المستخدمين الذين يكتبون
     */
    @GetMapping("/conversations/{conversationId}/typing")
    public ResponseEntity<?> getTypingUsers(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal User principal) {
        try {
            if (principal == null || principal.getId() == null) {
                return forbidden();
            }
            if (!isConversationParticipant(conversationId, principal)) {
                return forbidden();
            }
            List<Map<String, Object>> typingUsers = chatService.getTypingUsers(conversationId);
            return ResponseEntity.ok(Map.of("success", true, "data", typingUsers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==================== حالة الاتصال ====================

    /**
     * تحديث حالة الاتصال
     */
    @PostMapping("/presence")
    public ResponseEntity<?> updatePresence(
            @RequestParam UUID userId,
            @RequestParam boolean isOnline,
            @RequestParam(required = false) Long currentConversationId,
            @AuthenticationPrincipal User principal) {
        try {
            if (principal == null || principal.getId() == null || !principal.getId().equals(userId)) {
                return forbidden();
            }
            chatService.updatePresence(userId, isOnline, currentConversationId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * جلب حالة اتصال مستخدم
     */
    @GetMapping("/presence/{userId}")
    public ResponseEntity<?> getUserPresence(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User principal) {
        try {
            if (principal == null || principal.getId() == null || !principal.getId().equals(userId)) {
                return forbidden();
            }
            Map<String, Object> presence = chatService.getUserPresence(userId);
            return ResponseEntity.ok(Map.of("success", true, "data", presence));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * جلب حالة الطرف الآخر في المحادثة
     */
    @GetMapping("/conversations/{conversationId}/other-party-presence")
    public ResponseEntity<?> getOtherPartyPresence(
            @PathVariable Long conversationId,
            @RequestParam UUID currentUserId,
            @AuthenticationPrincipal User principal) {
        try {
            if (principal == null || principal.getId() == null || !principal.getId().equals(currentUserId)) {
                return forbidden();
            }
            if (!isConversationParticipant(conversationId, principal)) {
                return forbidden();
            }
            UUID otherUserId = conversationRepository.getOtherPartyUserId(conversationId, currentUserId);
            if (otherUserId == null) {
                return forbidden();
            }
            Map<String, Object> presence = chatService.getUserPresence(otherUserId);
            return ResponseEntity.ok(Map.of("success", true, "data", presence));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
