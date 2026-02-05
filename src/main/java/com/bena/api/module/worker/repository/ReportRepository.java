package com.bena.api.module.worker.repository;

import com.bena.api.module.worker.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    // بلاغات عامل معين
    Page<Report> findByWorkerIdOrderByCreatedAtDesc(Long workerId, Pageable pageable);
    
    // بلاغات حسب الحالة
    Page<Report> findByStatusOrderByCreatedAtDesc(Report.ReportStatus status, Pageable pageable);
    
    // عدد البلاغات ضد عامل
    long countByWorkerId(Long workerId);
    
    // التحقق من وجود بلاغ سابق من نفس المستخدم
    boolean existsByReporterIdAndWorkerId(Long reporterId, Long workerId);

    // إحصائيات حسب الحالة
    long countByStatus(Report.ReportStatus status);
}
