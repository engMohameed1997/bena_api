package com.bena.api.module.buildingsteps.facade;

import com.bena.api.core.facade.AbstractFacade;
import com.bena.api.module.buildingsteps.entity.StepMedia;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StepMediaFacade extends AbstractFacade<StepMedia> {

    public StepMediaFacade() {
        super(StepMedia.class);
    }

    /**
     * جلب الوسائط لخطوة معينة
     */
    public List<StepMedia> findByBuildingStep(Long buildingStepId) {
        return findByQuery(
            "SELECT m FROM StepMedia m WHERE m.buildingStep.id = ?1 ORDER BY m.mediaOrder",
            buildingStepId
        );
    }

    /**
     * جلب الوسائط لخطوة فرعية
     */
    public List<StepMedia> findBySubStep(Long subStepId) {
        return findByQuery(
            "SELECT m FROM StepMedia m WHERE m.subStep.id = ?1 ORDER BY m.mediaOrder",
            subStepId
        );
    }

    /**
     * جلب الوسائط حسب النوع
     */
    public List<StepMedia> findByType(String mediaType) {
        return findByQuery(
            "SELECT m FROM StepMedia m WHERE m.mediaType = ?1 ORDER BY m.createdAt DESC",
            mediaType
        );
    }

    /**
     * جلب الصور فقط
     */
    public List<StepMedia> findImages() {
        return findByType("IMAGE");
    }

    /**
     * جلب الفيديوهات فقط
     */
    public List<StepMedia> findVideos() {
        return findByType("VIDEO");
    }

    /**
     * جلب الرسومات التوضيحية
     */
    public List<StepMedia> findDiagrams() {
        return findByType("DIAGRAM");
    }
}
