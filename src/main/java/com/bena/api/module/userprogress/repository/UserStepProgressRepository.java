package com.bena.api.module.userprogress.repository;

import com.bena.api.module.userprogress.entity.UserStepProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserStepProgressRepository extends JpaRepository<UserStepProgress, Long> {

    List<UserStepProgress> findByUserIdOrderByStepIdAsc(UUID userId);

    Optional<UserStepProgress> findByUserIdAndStepId(UUID userId, Long stepId);

    @Query("SELECT COUNT(p) FROM UserStepProgress p WHERE p.user.id = :userId AND p.isCompleted = true")
    long countCompletedByUserId(@Param("userId") UUID userId);

    @Query("SELECT COALESCE(SUM(p.actualCost), 0) FROM UserStepProgress p WHERE p.user.id = :userId")
    BigDecimal getTotalCostByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(p) FROM UserStepProgress p WHERE p.user.id = :userId AND p.notes IS NOT NULL AND p.notes != ''")
    long countWithNotesByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(p) FROM UserStepProgress p WHERE p.user.id = :userId AND p.actualCost IS NOT NULL AND p.actualCost > 0")
    long countWithCostByUserId(@Param("userId") UUID userId);

    void deleteByUserIdAndStepId(UUID userId, Long stepId);
}
