package com.bena.api.module.cost.repository;

import com.bena.api.module.cost.entity.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaterialRepository extends JpaRepository<Material, UUID> {

    Optional<Material> findByCode(String code);

    boolean existsByCode(String code);

    List<Material> findByCategory(String category);

    @Query("SELECT m FROM Material m WHERE m.isActive = true AND m.category = :category")
    List<Material> findActiveByCategoryOrderByNameAr(String category);

    @Query("SELECT m FROM Material m WHERE m.isActive = true")
    Page<Material> findAllActive(Pageable pageable);

    @Query("SELECT DISTINCT m.category FROM Material m WHERE m.isActive = true ORDER BY m.category")
    List<String> findAllActiveCategories();

    @Query("SELECT m FROM Material m WHERE m.isActive = true AND " +
           "(LOWER(m.nameAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.nameEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.code) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Material> searchMaterials(String search, Pageable pageable);
}
