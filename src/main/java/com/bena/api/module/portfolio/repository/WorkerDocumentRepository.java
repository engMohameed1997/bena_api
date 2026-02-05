package com.bena.api.module.portfolio.repository;

import com.bena.api.module.portfolio.entity.WorkerDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerDocumentRepository extends JpaRepository<WorkerDocument, Long> {
    List<WorkerDocument> findByWorkerIdOrderByCreatedAtDesc(Long workerId);
    Page<WorkerDocument> findByVerificationStatusOrderByCreatedAtDesc(WorkerDocument.VerificationStatus status, Pageable pageable);
    long countByWorkerIdAndVerificationStatus(Long workerId, WorkerDocument.VerificationStatus status);
}
