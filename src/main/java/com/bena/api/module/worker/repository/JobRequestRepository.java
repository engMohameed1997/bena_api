package com.bena.api.module.worker.repository;

import com.bena.api.module.worker.entity.JobRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobRequestRepository extends JpaRepository<JobRequest, Long> {
    
    // طلبات المستخدم
    Page<JobRequest> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    // طلبات العامل
    Page<JobRequest> findByWorkerIdOrderByCreatedAtDesc(Long workerId, Pageable pageable);
    
    // طلبات العامل حسب الحالة
    Page<JobRequest> findByWorkerIdAndStatusOrderByCreatedAtDesc(Long workerId, JobRequest.JobStatus status, Pageable pageable);
    
    // عدد الطلبات قيد الانتظار للعامل
    long countByWorkerIdAndStatus(Long workerId, JobRequest.JobStatus status);
    
    
    // طلبات المستخدم حسب الحالة
    List<JobRequest> findByUserIdAndStatus(UUID userId, JobRequest.JobStatus status);

    // إحصائيات حسب الحالة
    long countByStatus(JobRequest.JobStatus status);
}
