package com.bena.api.module.ebook.repository;

import com.bena.api.module.ebook.entity.EbookPurchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EbookPurchaseRepository extends JpaRepository<EbookPurchase, UUID> {

    // التحقق من الشراء
    boolean existsByUserIdAndEbookId(UUID userId, UUID ebookId);

    // جلب شراء محدد
    Optional<EbookPurchase> findByUserIdAndEbookId(UUID userId, UUID ebookId);

    // جلب مشتريات المستخدم
    Page<EbookPurchase> findByUserId(UUID userId, Pageable pageable);

    // جلب مشترين الكتاب
    Page<EbookPurchase> findByEbookId(UUID ebookId, Pageable pageable);

    // عدد مشتريات كتاب
    long countByEbookId(UUID ebookId);

    // إجمالي مبيعات الناشر
    @Query("SELECT SUM(p.amountPaid) FROM EbookPurchase p " +
           "WHERE p.ebook.publisher.id = :publisherId")
    java.math.BigDecimal getTotalEarningsByPublisher(@Param("publisherId") UUID publisherId);

    // عدد المبيعات للناشر
    @Query("SELECT COUNT(p) FROM EbookPurchase p " +
           "WHERE p.ebook.publisher.id = :publisherId")
    long getTotalSalesByPublisher(@Param("publisherId") UUID publisherId);
}
