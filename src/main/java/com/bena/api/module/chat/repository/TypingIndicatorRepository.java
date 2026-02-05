package com.bena.api.module.chat.repository;

import com.bena.api.module.chat.entity.TypingIndicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TypingIndicatorRepository extends JpaRepository<TypingIndicator, Long> {
    
    Optional<TypingIndicator> findByConversation_IdAndUser_Id(Long conversationId, UUID userId);
    
    List<TypingIndicator> findByConversation_IdAndIsTypingTrue(Long conversationId);

    @Modifying
    @Query("DELETE FROM TypingIndicator t WHERE t.startedAt < :threshold")
    void deleteExpiredIndicators(@Param("threshold") LocalDateTime threshold);
}
