package com.bena.api.module.buildingsteps.facade;

import com.bena.api.core.facade.AbstractFacade;
import com.bena.api.module.buildingsteps.entity.SubStep;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubStepFacade extends AbstractFacade<SubStep> {

    public SubStepFacade() {
        super(SubStep.class);
    }

    /**
     * جلب الخطوات الفرعية لخطوة معينة
     */
    public List<SubStep> findByBuildingStep(Long buildingStepId) {
        return findByQuery(
            "SELECT s FROM SubStep s WHERE s.buildingStep.id = ?1 AND s.isActive = true ORDER BY s.subStepOrder",
            buildingStepId
        );
    }

    /**
     * عد الخطوات الفرعية لخطوة معينة
     */
    public long countByBuildingStep(Long buildingStepId) {
        return getEntityManager()
            .createQuery("SELECT COUNT(s) FROM SubStep s WHERE s.buildingStep.id = ?1 AND s.isActive = true", Long.class)
            .setParameter(1, buildingStepId)
            .getSingleResult();
    }
}
