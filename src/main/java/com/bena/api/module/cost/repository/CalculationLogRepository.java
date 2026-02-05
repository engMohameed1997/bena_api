package com.bena.api.module.cost.repository;

import com.bena.api.module.cost.entity.CalculationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CalculationLogRepository extends JpaRepository<CalculationLog, UUID> {

    Page<CalculationLog> findByUserId(UUID userId, Pageable pageable);

    Page<CalculationLog> findByCalculationType(String calculationType, Pageable pageable);

    @Query("SELECT c FROM CalculationLog c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<CalculationLog> findRecentByUserId(UUID userId, Pageable pageable);

    @Query("SELECT c FROM CalculationLog c WHERE c.createdAt >= :since ORDER BY c.createdAt DESC")
    Page<CalculationLog> findAllSince(OffsetDateTime since, Pageable pageable);

    @Query("SELECT c.calculationType, COUNT(c), SUM(c.totalCost) FROM CalculationLog c " +
           "WHERE c.createdAt >= :since GROUP BY c.calculationType")
    List<Object[]> getStatsBySince(OffsetDateTime since);

    @Query("SELECT COUNT(c) FROM CalculationLog c WHERE c.calculationType = :type AND c.createdAt >= :since")
    long countByTypeAndSince(String type, OffsetDateTime since);
}
