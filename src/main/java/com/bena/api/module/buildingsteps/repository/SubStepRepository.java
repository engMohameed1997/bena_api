package com.bena.api.module.buildingsteps.repository;

import com.bena.api.module.buildingsteps.entity.SubStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubStepRepository extends JpaRepository<SubStep, Long> {
    
    List<SubStep> findByBuildingStepIdAndIsActiveTrueOrderBySubStepOrderAsc(Long buildingStepId);
}
