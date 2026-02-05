package com.bena.api.module.chat.repository;

import com.bena.api.module.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    Page<Message> findByConversation_IdAndIsDeletedFalseOrderByCreatedAtDesc(Long conversationId, Pageable pageable);
    
    Page<Message> findByConversation_IdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);
    
    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.conversation.id = :conversationId AND m.senderType = :senderType")
    void markAsReadBySenderType(@Param("conversationId") Long conversationId, @Param("senderType") Message.SenderType senderType);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.isDelivered = true WHERE m.conversation.id = :conversationId AND m.isDelivered = false")
    void markAsDelivered(@Param("conversationId") Long conversationId);

    // البحث في الرسائل
    @Query("SELECT m FROM ChatMessage m WHERE m.conversation.id = :conversationId AND m.isDeleted = false AND LOWER(m.content) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY m.createdAt DESC")
    Page<Message> searchMessages(@Param("conversationId") Long conversationId, @Param("query") String query, Pageable pageable);

    // البحث في جميع محادثات المستخدم
    @Query("SELECT m FROM ChatMessage m WHERE m.conversation.user.id = :userId AND m.isDeleted = false AND LOWER(m.content) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY m.createdAt DESC")
    Page<Message> searchUserMessages(@Param("userId") java.util.UUID userId, @Param("query") String query, Pageable pageable);

    // الرسائل غير المقروءة
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.conversation.id = :conversationId AND m.isRead = false AND m.senderType = :senderType")
    long countUnreadMessages(@Param("conversationId") Long conversationId, @Param("senderType") Message.SenderType senderType);

    // جلب الردود على رسالة معينة
    List<Message> findByReplyTo_IdAndIsDeletedFalse(Long replyToId);
}
