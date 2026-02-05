package com.bena.api.module.offers.repository;

import com.bena.api.module.offers.entity.ContractorOffer;
import com.bena.api.module.offers.entity.OfferType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContractorOfferRepository extends JpaRepository<ContractorOffer, UUID> {

    // البحث عن عروض نشطة
    Page<ContractorOffer> findByIsActiveTrue(Pageable pageable);

    // البحث حسب نوع العرض
    Page<ContractorOffer> findByOfferTypeAndIsActiveTrue(OfferType offerType, Pageable pageable);

    // البحث حسب المدينة
    Page<ContractorOffer> findByCityAndIsActiveTrue(String city, Pageable pageable);

    // البحث حسب العامل
    List<ContractorOffer> findByWorkerIdOrderByCreatedAtDesc(Long workerId);

    // العروض المميزة
    List<ContractorOffer> findByIsFeaturedTrueAndIsActiveTrueOrderByCreatedAtDesc();

    // البحث المتقدم مع الفلترة
    @Query(value = "SELECT o FROM ContractorOffer o " +
           "LEFT JOIN o.worker w " +
           "WHERE o.isActive = true " +
           "AND (:offerType IS NULL OR o.offerType = :offerType) " +
           "AND (:minPrice IS NULL OR o.basePrice >= :minPrice) " +
           "AND (:maxPrice IS NULL OR o.basePrice <= :maxPrice) " +
           "AND (:city IS NULL OR :city = '' OR LOWER(o.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
           "AND (:providerId IS NULL OR w.id = :providerId) " +
           "AND (:verifiedOnly = false OR w.isVerified = true)",
           countQuery = "SELECT COUNT(o) FROM ContractorOffer o " +
           "LEFT JOIN o.worker w " +
           "WHERE o.isActive = true " +
           "AND (:offerType IS NULL OR o.offerType = :offerType) " +
           "AND (:minPrice IS NULL OR o.basePrice >= :minPrice) " +
           "AND (:maxPrice IS NULL OR o.basePrice <= :maxPrice) " +
           "AND (:city IS NULL OR :city = '' OR LOWER(o.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
           "AND (:providerId IS NULL OR w.id = :providerId) " +
           "AND (:verifiedOnly = false OR w.isVerified = true)")
    Page<ContractorOffer> findWithFilters(
            @Param("offerType") OfferType offerType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("city") String city,
            @Param("providerId") Long providerId,
            @Param("verifiedOnly") boolean verifiedOnly,
            Pageable pageable
    );

    // العثور على عرض مع تفاصيله الكاملة
    @Query("SELECT o FROM ContractorOffer o " +
           "LEFT JOIN FETCH o.worker w " +
           "LEFT JOIN FETCH o.features f " +
           "LEFT JOIN FETCH o.images i " +
           "WHERE o.id = :id")
    Optional<ContractorOffer> findByIdWithDetails(@Param("id") UUID id);

    // إحصائيات للمقاول
    @Query("SELECT COUNT(o) FROM ContractorOffer o WHERE o.worker.id = :workerId AND o.isActive = true")
    Long countActiveByWorkerId(@Param("workerId") Long workerId);

    @Query("SELECT COALESCE(SUM(o.viewCount), 0) FROM ContractorOffer o WHERE o.worker.id = :workerId")
    Long sumViewsByWorkerId(@Param("workerId") Long workerId);

    // البحث النصي
    @Query("SELECT o FROM ContractorOffer o " +
           "WHERE o.isActive = true " +
           "AND (LOWER(o.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(o.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<ContractorOffer> searchByQuery(@Param("query") String query, Pageable pageable);
}
