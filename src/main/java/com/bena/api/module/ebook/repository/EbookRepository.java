package com.bena.api.module.ebook.repository;

import com.bena.api.module.ebook.entity.Ebook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface EbookRepository extends JpaRepository<Ebook, UUID> {

    // جلب الكتب المنشورة
    Page<Ebook> findByIsPublishedTrue(Pageable pageable);

    // جلب الكتب حسب التصنيف
    Page<Ebook> findByCategoryAndIsPublishedTrue(String category, Pageable pageable);

    // جلب كتب الناشر
    Page<Ebook> findByPublisherId(UUID publisherId, Pageable pageable);

    // جلب الكتب المميزة
    List<Ebook> findByIsFeaturedTrueAndIsPublishedTrue();

    // بحث في الكتب
    @Query("SELECT e FROM Ebook e WHERE e.isPublished = true AND " +
           "(LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Ebook> searchBooks(@Param("search") String search, Pageable pageable);

    // فلترة حسب السعر
    @Query("SELECT e FROM Ebook e WHERE e.isPublished = true AND " +
           "e.price >= :minPrice AND e.price <= :maxPrice")
    Page<Ebook> findByPriceRange(
        @Param("minPrice") BigDecimal minPrice, 
        @Param("maxPrice") BigDecimal maxPrice, 
        Pageable pageable
    );

    // فلترة متقدمة
    @Query("SELECT e FROM Ebook e WHERE e.isPublished = true " +
           "AND (:category IS NULL OR e.category = :category) " +
           "AND (:minPrice IS NULL OR e.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR e.price <= :maxPrice) " +
           "AND (:search IS NULL OR :search = '' OR LOWER(CAST(e.title AS string)) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))")
    Page<Ebook> findWithFilters(
        @Param("category") String category,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("search") String search,
        Pageable pageable
    );

    // عدد كتب الناشر
    long countByPublisherId(UUID publisherId);

    // قائمة التصنيفات المستخدمة
    @Query("SELECT DISTINCT e.category FROM Ebook e WHERE e.isPublished = true")
    List<String> findAllCategories();
}
