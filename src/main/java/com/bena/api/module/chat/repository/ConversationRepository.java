package com.bena.api.module.chat.repository;

import com.bena.api.module.chat.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    Optional<Conversation> findByUser_IdAndWorker_Id(UUID userId, Long workerId);
    
    Page<Conversation> findByUser_IdOrderByLastMessageAtDesc(UUID userId, Pageable pageable);
    
    Page<Conversation> findByWorker_IdOrderByLastMessageAtDesc(Long workerId, Pageable pageable);
    
    @Query("SELECT SUM(c.userUnreadCount) FROM ChatConversation c WHERE c.user.id = :userId")
    Long getTotalUserUnreadCount(@Param("userId") UUID userId);
    
    @Query("SELECT SUM(c.workerUnreadCount) FROM ChatConversation c WHERE c.worker.id = :workerId")
    Long getTotalWorkerUnreadCount(@Param("workerId") Long workerId);

    @Query("SELECT COUNT(c) > 0 FROM ChatConversation c WHERE c.id = :conversationId AND (c.user.id = :principalId OR c.worker.userId = :principalId)")
    boolean isParticipant(@Param("conversationId") Long conversationId, @Param("principalId") UUID principalId);

    @Query("SELECT CASE WHEN c.user.id = :principalId THEN 'USER' WHEN c.worker.userId = :principalId THEN 'WORKER' ELSE NULL END FROM ChatConversation c WHERE c.id = :conversationId")
    String getParticipantType(@Param("conversationId") Long conversationId, @Param("principalId") UUID principalId);

    @Query("SELECT CASE WHEN c.user.id = :currentUserId THEN c.worker.userId ELSE c.user.id END FROM ChatConversation c WHERE c.id = :conversationId AND (c.user.id = :currentUserId OR c.worker.userId = :currentUserId)")
    UUID getOtherPartyUserId(@Param("conversationId") Long conversationId, @Param("currentUserId") UUID currentUserId);
}
