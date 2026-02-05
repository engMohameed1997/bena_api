package com.bena.api.module.buildingsteps.repository;

import com.bena.api.module.buildingsteps.entity.BuildingStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingStepRepository extends JpaRepository<BuildingStep, Long> {
    
    List<BuildingStep> findByIsActiveTrueOrderByStepOrderAsc();
    
    long countByIsActiveTrue();
    
    List<BuildingStep> findByCategoryIdAndIsActiveTrueOrderByStepOrderAsc(Long categoryId);
    
    @Query("SELECT bs FROM BuildingStep bs LEFT JOIN FETCH bs.subSteps WHERE bs.id = :id")
    BuildingStep findByIdWithSubSteps(Long id);
    
    @Query("SELECT bs FROM BuildingStep bs LEFT JOIN FETCH bs.mediaList WHERE bs.id = :id")
    BuildingStep findByIdWithMedia(Long id);
    
    @Query("SELECT DISTINCT bs FROM BuildingStep bs " +
           "LEFT JOIN FETCH bs.subSteps " +
           "WHERE bs.id = :id")
    BuildingStep findByIdWithSubSteps2(Long id);
    
    @Query("SELECT DISTINCT bs FROM BuildingStep bs " +
           "LEFT JOIN FETCH bs.mediaList " +
           "WHERE bs.id = :id")
    BuildingStep findByIdWithMedia2(Long id);
    
    default BuildingStep findByIdWithAllDetails(Long id) {
        BuildingStep step = findByIdWithSubSteps2(id);
        if (step != null) {
            // جلب الـ media في query منفصل
            step = findByIdWithMedia2(id);
        }
        return step;
    }
    // Simple paginated query for active steps
    Page<BuildingStep> findByIsActiveTrue(Pageable pageable);
    
    // Filtered paginated query - using COALESCE for better null handling
    @Query("SELECT bs FROM BuildingStep bs WHERE " +
           "bs.isActive = true " +
           "AND (COALESCE(:search, '') = '' OR LOWER(bs.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(bs.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (COALESCE(:categoryId, 0) = 0 OR bs.categoryId = :categoryId)")
    org.springframework.data.domain.Page<BuildingStep> findAllWithFilters(@org.springframework.data.repository.query.Param("search") String search, 
                                                                          @org.springframework.data.repository.query.Param("categoryId") Long categoryId, 
                                                                          org.springframework.data.domain.Pageable pageable);
}
