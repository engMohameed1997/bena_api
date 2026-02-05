package com.bena.api.module.offers.repository;

import com.bena.api.module.offers.entity.OfferRequest;
import com.bena.api.module.offers.entity.OfferRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfferRequestRepository extends JpaRepository<OfferRequest, UUID> {

    // طلبات المستخدم
    Page<OfferRequest> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    // طلبات عرض معين
    Page<OfferRequest> findByOfferIdOrderByCreatedAtDesc(UUID offerId, Pageable pageable);

    // طلبات بحالة معينة
    Page<OfferRequest> findByOfferIdAndStatus(UUID offerId, OfferRequestStatus status, Pageable pageable);

    // التحقق من وجود طلب سابق
    boolean existsByOfferIdAndUserId(UUID offerId, UUID userId);

    // الطلبات الواردة للمقاول
    @Query("SELECT r FROM OfferRequest r " +
           "JOIN FETCH r.offer o " +
           "JOIN FETCH r.user u " +
           "WHERE o.worker.id = :workerId " +
           "ORDER BY r.createdAt DESC")
    Page<OfferRequest> findByWorkerId(@Param("workerId") Long workerId, Pageable pageable);

    // الطلبات الواردة بحالة معينة
    @Query("SELECT r FROM OfferRequest r " +
           "JOIN r.offer o " +
           "WHERE o.worker.id = :workerId AND r.status = :status " +
           "ORDER BY r.createdAt DESC")
    List<OfferRequest> findByWorkerIdAndStatus(@Param("workerId") Long workerId, @Param("status") OfferRequestStatus status);

    // إحصائيات
    @Query("SELECT COUNT(r) FROM OfferRequest r JOIN r.offer o WHERE o.worker.id = :workerId")
    Long countByWorkerId(@Param("workerId") Long workerId);

    @Query("SELECT COUNT(r) FROM OfferRequest r JOIN r.offer o WHERE o.worker.id = :workerId AND r.status = :status")
    Long countByWorkerIdAndStatus(@Param("workerId") Long workerId, @Param("status") OfferRequestStatus status);

    // العثور على طلب مع التفاصيل
    @Query("SELECT r FROM OfferRequest r " +
           "JOIN FETCH r.offer o " +
           "JOIN FETCH r.user u " +
           "WHERE r.id = :id")
    Optional<OfferRequest> findByIdWithDetails(@Param("id") UUID id);
}
