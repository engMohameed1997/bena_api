package com.bena.api.module.consultation.repository;

import com.bena.api.module.consultation.entity.ConsultationMessageRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsultationMessageRatingRepository extends JpaRepository<ConsultationMessageRating, Long> {

    Optional<ConsultationMessageRating> findByUserIdAndMessageId(UUID userId, Long messageId);
}
