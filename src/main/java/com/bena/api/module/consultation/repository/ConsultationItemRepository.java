package com.bena.api.module.consultation.repository;

import com.bena.api.module.consultation.entity.ConsultationItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsultationItemRepository extends JpaRepository<ConsultationItem, UUID> {

    @Query("SELECT i FROM ConsultationItem i WHERE i.category.code = :categoryCode AND i.isActive = true ORDER BY i.displayOrder")
    List<ConsultationItem> findByCategoryCodeOrderByDisplayOrder(String categoryCode);

    @Query("SELECT i FROM ConsultationItem i WHERE i.category.id = :categoryId AND i.isActive = true ORDER BY i.displayOrder")
    List<ConsultationItem> findByCategoryIdOrderByDisplayOrder(UUID categoryId);

    @Query("SELECT i FROM ConsultationItem i WHERE i.category.code = :categoryCode AND i.code = :itemCode AND i.isActive = true")
    Optional<ConsultationItem> findByCategoryCodeAndCode(String categoryCode, String itemCode);

    @Query("SELECT i FROM ConsultationItem i WHERE i.isActive = true AND i.isFeatured = true ORDER BY i.displayOrder")
    List<ConsultationItem> findFeaturedItems();

    @Query("SELECT i FROM ConsultationItem i WHERE i.isActive = true ORDER BY i.viewCount DESC")
    Page<ConsultationItem> findMostViewed(Pageable pageable);

    @Query("SELECT i FROM ConsultationItem i WHERE i.isActive = true ORDER BY i.rating DESC NULLS LAST")
    Page<ConsultationItem> findTopRated(Pageable pageable);

    @Query("SELECT i FROM ConsultationItem i WHERE i.isActive = true AND " +
           "(LOWER(i.nameAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.nameEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.descriptionAr) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ConsultationItem> searchItems(String search, Pageable pageable);

    @Modifying
    @Query("UPDATE ConsultationItem i SET i.viewCount = i.viewCount + 1 WHERE i.id = :id")
    void incrementViewCount(UUID id);

    @Query("SELECT COUNT(i) FROM ConsultationItem i WHERE i.category.code = :categoryCode AND i.isActive = true")
    long countByCategoryCode(String categoryCode);
}
