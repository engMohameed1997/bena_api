package com.bena.api.module.design.repository;

import com.bena.api.module.design.entity.Design;
import com.bena.api.module.design.entity.DesignCategory;
import com.bena.api.module.design.entity.DesignStyle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesignRepository extends JpaRepository<Design, Long> {

    // البحث حسب الفئة
    Page<Design> findByCategoryAndIsActiveTrue(DesignCategory category, Pageable pageable);

    // البحث حسب الفئة والنمط
    Page<Design> findByCategoryAndStyleAndIsActiveTrue(
            DesignCategory category, 
            DesignStyle style, 
            Pageable pageable
    );

    // البحث حسب الفئة والمساحة
    @Query("SELECT d FROM Design d WHERE d.category = :category " +
           "AND d.isActive = true " +
           "AND d.areaInSquareMeters BETWEEN :minArea AND :maxArea")
    Page<Design> findByCategoryAndAreaRange(
            @Param("category") DesignCategory category,
            @Param("minArea") Integer minArea,
            @Param("maxArea") Integer maxArea,
            Pageable pageable
    );

    // البحث المتقدم
    @Query("SELECT d FROM Design d WHERE d.category = :category " +
           "AND d.isActive = true " +
           "AND (:style IS NULL OR d.style = :style) " +
           "AND (:minArea IS NULL OR d.areaInSquareMeters >= :minArea) " +
           "AND (:maxArea IS NULL OR d.areaInSquareMeters <= :maxArea)")
    Page<Design> findByFilters(
            @Param("category") DesignCategory category,
            @Param("style") DesignStyle style,
            @Param("minArea") Integer minArea,
            @Param("maxArea") Integer maxArea,
            Pageable pageable
    );

    // التصاميم المميزة
    List<Design> findByIsFeaturedTrueAndIsActiveTrueOrderByCreatedAtDesc();

    // البحث بالعنوان
    Page<Design> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title, Pageable pageable);

    // عدد التصاميم حسب الفئة
    long countByCategoryAndIsActiveTrue(DesignCategory category);

    // الأكثر مشاهدة
    List<Design> findTop10ByIsActiveTrueOrderByViewCountDesc();

}
