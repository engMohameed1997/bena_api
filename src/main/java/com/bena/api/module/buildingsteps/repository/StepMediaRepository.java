package com.bena.api.module.buildingsteps.repository;

import com.bena.api.module.buildingsteps.entity.StepMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepMediaRepository extends JpaRepository<StepMedia, Long> {
    
    List<StepMedia> findByBuildingStepIdOrderByMediaOrderAsc(Long buildingStepId);
    
    List<StepMedia> findBySubStepIdOrderByMediaOrderAsc(Long subStepId);
    
    void deleteByBuildingStepId(Long buildingStepId);
}
