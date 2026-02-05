package com.bena.api.module.auth.repository;

import com.bena.api.module.auth.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    Optional<PasswordResetToken> findByToken(String token);

    @Query("SELECT p FROM PasswordResetToken p WHERE p.user.id = :userId")
    Optional<PasswordResetToken> findByUserId(UUID userId);
    
    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.user.id = :userId")
    void deleteByUserId(UUID userId);
}
