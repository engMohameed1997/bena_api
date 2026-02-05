package com.bena.api.module.auth.repository;

import com.bena.api.module.auth.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    
    Optional<EmailVerificationToken> findByToken(String token);
    
    @Query("SELECT e FROM EmailVerificationToken e WHERE e.user.id = :userId")
    Optional<EmailVerificationToken> findByUserId(UUID userId);
    
    @Modifying
    @Query("DELETE FROM EmailVerificationToken e WHERE e.user.id = :userId")
    void deleteByUserId(UUID userId);
}
