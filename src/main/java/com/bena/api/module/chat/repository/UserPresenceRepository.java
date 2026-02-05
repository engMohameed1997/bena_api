package com.bena.api.module.chat.repository;

import com.bena.api.module.chat.entity.UserPresence;
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
public interface UserPresenceRepository extends JpaRepository<UserPresence, Long> {
    
    Optional<UserPresence> findByUser_Id(UUID userId);

    List<UserPresence> findAllByUser_IdIn(List<UUID> userIds);
    
    List<UserPresence> findByIsOnlineTrue();

    @Modifying
    @Query("UPDATE UserPresence p SET p.isOnline = true, p.lastSeenAt = :now WHERE p.user.id = :userId")
    void setOnline(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE UserPresence p SET p.isOnline = false, p.lastSeenAt = :now WHERE p.user.id = :userId")
    void setOffline(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE UserPresence p SET p.currentConversationId = :conversationId WHERE p.user.id = :userId")
    void setCurrentConversation(@Param("userId") UUID userId, @Param("conversationId") Long conversationId);

    @Query("SELECT p.isOnline FROM UserPresence p WHERE p.user.id = :userId")
    Boolean isUserOnline(@Param("userId") UUID userId);
}
