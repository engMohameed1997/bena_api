package com.bena.api.module.consultation.repository;

import com.bena.api.module.consultation.entity.ConsultationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsultationCategoryRepository extends JpaRepository<ConsultationCategory, UUID> {

    Optional<ConsultationCategory> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT c FROM ConsultationCategory c WHERE c.isActive = true ORDER BY c.displayOrder")
    List<ConsultationCategory> findAllActiveOrderByDisplayOrder();

    @Query("SELECT c FROM ConsultationCategory c WHERE c.isActive = true AND " +
           "(LOWER(c.nameAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.nameEn) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<ConsultationCategory> searchCategories(String search);
}
