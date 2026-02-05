package com.bena.api.module.worker.repository;

import com.bena.api.module.worker.entity.WorkerMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerMediaRepository extends JpaRepository<WorkerMedia, Long> {

    // جلب وسائط عامل معين مرتبة
    List<WorkerMedia> findByWorkerIdOrderByDisplayOrderAsc(Long workerId);

    // حذف وسائط عامل
    void deleteByWorkerId(Long workerId);
}
