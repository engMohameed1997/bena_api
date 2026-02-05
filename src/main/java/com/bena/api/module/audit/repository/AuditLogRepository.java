package com.bena.api.module.audit.repository;

import com.bena.api.module.audit.entity.AuditLog;
import com.bena.api.module.audit.entity.AuditLog.AuditAction;
import com.bena.api.module.audit.entity.AuditLog.AuditTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository لإدارة سجلات التدقيق
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * جلب السجلات حسب المستخدم
     */
    Page<AuditLog> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    /**
     * جلب السجلات حسب نوع العملية
     */
    Page<AuditLog> findByActionOrderByCreatedAtDesc(AuditAction action, Pageable pageable);

    /**
     * جلب السجلات حسب نوع الكيان المستهدف
     */
    Page<AuditLog> findByTargetTypeOrderByCreatedAtDesc(AuditTargetType targetType, Pageable pageable);

    /**
     * جلب السجلات حسب الكيان المستهدف المحدد
     */
    Page<AuditLog> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(
            AuditTargetType targetType, 
            String targetId, 
            Pageable pageable
    );

    /**
     * جلب السجلات في فترة زمنية محددة
     */
    Page<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
            OffsetDateTime startDate, 
            OffsetDateTime endDate, 
            Pageable pageable
    );

    /**
     * جلب السجلات حسب IP
     */
    Page<AuditLog> findByIpAddressOrderByCreatedAtDesc(String ipAddress, Pageable pageable);

    /**
     * بحث متقدم مع فلاتر متعددة
     */
    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:userId IS NULL OR a.userId = :userId) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:targetType IS NULL OR a.targetType = :targetType) AND " +
           "(:targetId IS NULL OR a.targetId = :targetId) AND " +
           "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR a.createdAt <= :endDate) AND " +
           "(:search IS NULL OR LOWER(a.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "                   LOWER(a.userEmail) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY a.createdAt DESC")
    Page<AuditLog> searchAuditLogs(
            @Param("userId") UUID userId,
            @Param("action") AuditAction action,
            @Param("targetType") AuditTargetType targetType,
            @Param("targetId") String targetId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            @Param("search") String search,
            Pageable pageable
    );

    /**
     * إحصائيات العمليات حسب النوع
     */
    @Query("SELECT a.action, COUNT(a) FROM AuditLog a " +
           "WHERE a.createdAt >= :since " +
           "GROUP BY a.action ORDER BY COUNT(a) DESC")
    List<Object[]> getActionStatsSince(@Param("since") OffsetDateTime since);

    /**
     * إحصائيات العمليات حسب المستخدم
     */
    @Query("SELECT a.userId, a.userEmail, COUNT(a) FROM AuditLog a " +
           "WHERE a.createdAt >= :since AND a.userId IS NOT NULL " +
           "GROUP BY a.userId, a.userEmail ORDER BY COUNT(a) DESC")
    List<Object[]> getUserActivitySince(@Param("since") OffsetDateTime since, Pageable pageable);

    /**
     * عدد محاولات الدخول الفاشلة من IP معين
     */
    @Query("SELECT COUNT(a) FROM AuditLog a " +
           "WHERE a.action = 'USER_LOGIN' AND a.status = 'FAILURE' " +
           "AND a.ipAddress = :ipAddress AND a.createdAt >= :since")
    long countFailedLoginAttempts(@Param("ipAddress") String ipAddress, @Param("since") OffsetDateTime since);

    /**
     * آخر نشاط للمستخدم
     */
    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId ORDER BY a.createdAt DESC LIMIT 1")
    AuditLog findLastActivityByUserId(@Param("userId") UUID userId);

    /**
     * حذف السجلات القديمة (للتنظيف الدوري)
     */
    void deleteByCreatedAtBefore(OffsetDateTime beforeDate);
}
