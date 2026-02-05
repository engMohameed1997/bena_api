package com.bena.api.module.worker.repository;

import com.bena.api.module.worker.entity.WorkerReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerReviewRepository extends JpaRepository<WorkerReview, Long> {

    // جلب تقييمات عامل معين
    Page<WorkerReview> findByWorkerIdAndIsApprovedTrue(Long workerId, Pageable pageable);

    // جلب كل تقييمات عامل
    List<WorkerReview> findByWorkerId(Long workerId);

    // عدد التقييمات المعتمدة
    long countByWorkerIdAndIsApprovedTrue(Long workerId);
}
