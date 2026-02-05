package com.bena.api.module.buildingsteps.facade;

import com.bena.api.core.facade.AbstractFacade;
import com.bena.api.module.buildingsteps.entity.StepCategory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StepCategoryFacade extends AbstractFacade<StepCategory> {

    public StepCategoryFacade() {
        super(StepCategory.class);
    }

    /**
     * جلب التصنيفات النشطة مرتبة
     */
    public List<StepCategory> findAllActiveOrdered() {
        return findByQuery(
            "SELECT c FROM StepCategory c WHERE c.isActive = true ORDER BY c.categoryOrder"
        );
    }

    /**
     * البحث في التصنيفات
     */
    public List<StepCategory> search(String keyword) {
        return findByQuery(
            "SELECT c FROM StepCategory c WHERE LOWER(c.name) LIKE LOWER(?1) AND c.isActive = true",
            "%" + keyword + "%"
        );
    }
}
