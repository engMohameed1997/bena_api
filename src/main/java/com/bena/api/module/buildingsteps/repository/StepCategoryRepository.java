package com.bena.api.module.buildingsteps.repository;

import com.bena.api.module.buildingsteps.entity.StepCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepCategoryRepository extends JpaRepository<StepCategory, Long> {
    
    List<StepCategory> findByIsActiveTrueOrderByCategoryOrderAsc();
}
