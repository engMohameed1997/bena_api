package com.bena.api.module.buildingsteps.facade;

import com.bena.api.core.facade.AbstractFacade;
import com.bena.api.module.buildingsteps.entity.BuildingStep;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Facade لإدارة BuildingStep
 * يرث جميع العمليات الأساسية من AbstractFacade
 */
@Repository
public class BuildingStepFacade extends AbstractFacade<BuildingStep> {

    public BuildingStepFacade() {
        super(BuildingStep.class);
    }

    /**
     * جلب الخطوات حسب التصنيف
     */
    public List<BuildingStep> findByCategory(Long categoryId) {
        return findByQuery(
            "SELECT b FROM BuildingStep b WHERE b.categoryId = ?1 AND b.isActive = true ORDER BY b.stepOrder",
            categoryId
        );
    }

    /**
     * جلب الخطوات النشطة فقط
     */
    public List<BuildingStep> findAllActive() {
        return findByQuery(
            "SELECT b FROM BuildingStep b WHERE b.isActive = true ORDER BY b.stepOrder"
        );
    }

    /**
     * جلب الخطوات حسب الترتيب
     */
    public List<BuildingStep> findAllOrdered() {
        return findByQuery(
            "SELECT b FROM BuildingStep b ORDER BY b.stepOrder"
        );
    }

    /**
     * البحث في الخطوات
     */
    public List<BuildingStep> search(String keyword) {
        return findByQuery(
            "SELECT b FROM BuildingStep b WHERE (LOWER(b.title) LIKE LOWER(?1) OR LOWER(b.description) LIKE LOWER(?1)) AND b.isActive = true",
            "%" + keyword + "%"
        );
    }

    /**
     * تعطيل خطوة (Soft Delete)
     */
    public void deactivate(Long id) {
        executeUpdate(
            "UPDATE BuildingStep b SET b.isActive = false WHERE b.id = ?1",
            id
        );
    }

    /**
     * تفعيل خطوة
     */
    public void activate(Long id) {
        executeUpdate(
            "UPDATE BuildingStep b SET b.isActive = true WHERE b.id = ?1",
            id
        );
    }
}
