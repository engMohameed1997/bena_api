package com.bena.api.core.facade;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Abstract Facade للعمليات الأساسية CRUD
 * يمكن الوراثة منه لأي Entity للحصول على العمليات الجاهزة
 * 
 * @param <T> نوع الـ Entity
 */
public abstract class AbstractFacade<T> {

    private final Class<T> entityClass;

    @PersistenceContext
    protected EntityManager em;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * إنشاء كيان جديد
     */
    @Transactional
    public T create(T entity) {
        em.persist(entity);
        em.flush();
        return entity;
    }

    /**
     * تحديث كيان موجود
     */
    @Transactional
    public T update(T entity) {
        return em.merge(entity);
    }

    /**
     * حذف كيان
     */
    @Transactional
    public void remove(T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    /**
     * البحث عن كيان بالـ ID
     */
    public Optional<T> find(Object id) {
        return Optional.ofNullable(em.find(entityClass, id));
    }

    /**
     * جلب جميع الكيانات
     */
    public List<T> findAll() {
        CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return em.createQuery(cq).getResultList();
    }

    /**
     * جلب الكيانات مع Pagination
     */
    public List<T> findRange(int first, int max) {
        CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        TypedQuery<T> q = em.createQuery(cq);
        q.setFirstResult(first);
        q.setMaxResults(max);
        return q.getResultList();
    }

    /**
     * عد جميع الكيانات
     */
    public long count() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> rt = cq.from(entityClass);
        cq.select(cb.count(rt));
        return em.createQuery(cq).getSingleResult();
    }

    /**
     * حذف كيان بالـ ID
     */
    @Transactional
    public void removeById(Object id) {
        find(id).ifPresent(this::remove);
    }

    /**
     * التحقق من وجود كيان بالـ ID
     */
    public boolean exists(Object id) {
        return find(id).isPresent();
    }

    /**
     * حذف جميع الكيانات
     */
    @Transactional
    public void removeAll() {
        em.createQuery("DELETE FROM " + entityClass.getSimpleName()).executeUpdate();
    }

    /**
     * جلب الكيانات بشرط معين (JPQL)
     */
    public List<T> findByQuery(String jpql, Object... params) {
        TypedQuery<T> query = em.createQuery(jpql, entityClass);
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }
        return query.getResultList();
    }

    /**
     * جلب كيان واحد بشرط معين (JPQL)
     */
    public Optional<T> findSingleByQuery(String jpql, Object... params) {
        TypedQuery<T> query = em.createQuery(jpql, entityClass);
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }
        List<T> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * تنفيذ استعلام تحديث أو حذف (JPQL)
     */
    @Transactional
    public int executeUpdate(String jpql, Object... params) {
        jakarta.persistence.Query query = em.createQuery(jpql);
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }
        return query.executeUpdate();
    }

    /**
     * الحصول على EntityManager
     */
    protected EntityManager getEntityManager() {
        return em;
    }
}
