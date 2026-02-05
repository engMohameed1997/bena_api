package com.bena.api.module.worker.repository;

import com.bena.api.module.worker.entity.JobRequestOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository لعروض الأسعار
 */
@Repository
public interface JobRequestOfferRepository extends JpaRepository<JobRequestOffer, Long> {

    /**
     * الحصول على جميع العروض لطلب معين
     */
    List<JobRequestOffer> findByJobRequestIdOrderByCreatedAtDesc(Long jobRequestId);

    /**
     * الحصول على آخر عرض لطلب معين
     */
    @Query("SELECT o FROM JobRequestOffer o WHERE o.jobRequest.id = :jobRequestId ORDER BY o.createdAt DESC LIMIT 1")
    Optional<JobRequestOffer> findLatestOfferByJobRequestId(@Param("jobRequestId") Long jobRequestId);

    /**
     * الحصول على العروض حسب الحالة لطلب معين
     */
    List<JobRequestOffer> findByJobRequestIdAndStatus(Long jobRequestId, JobRequestOffer.OfferStatus status);

    /**
     * عدد العروض لطلب معين
     */
    long countByJobRequestId(Long jobRequestId);

    /**
     * الحصول على العروض التي قدمها المختص
     */
    @Query("SELECT o FROM JobRequestOffer o WHERE o.jobRequest.id = :jobRequestId AND o.offeredBy = 'WORKER' ORDER BY o.createdAt DESC")
    List<JobRequestOffer> findWorkerOffersByJobRequestId(@Param("jobRequestId") Long jobRequestId);

    /**
     * الحصول على العروض المضادة من صاحب المنزل
     */
    @Query("SELECT o FROM JobRequestOffer o WHERE o.jobRequest.id = :jobRequestId AND o.offeredBy = 'HOMEOWNER' ORDER BY o.createdAt DESC")
    List<JobRequestOffer> findHomeownerOffersByJobRequestId(@Param("jobRequestId") Long jobRequestId);
}
