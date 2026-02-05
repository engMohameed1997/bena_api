package com.bena.api.module.worker.repository;

import com.bena.api.module.worker.entity.Worker;
import com.bena.api.module.worker.entity.WorkerCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {

    // جلب عامل مع معرض أعماله (لصفحة التفاصيل)
    @Query("SELECT w FROM Worker w LEFT JOIN FETCH w.mediaGallery WHERE w.id = :id")
    Optional<Worker> findByIdWithMedia(@Param("id") Long id);

    // البحث بـ userId (للربط مع جدول users)

    // البحث بـ userId (للربط مع جدول users)
    Optional<Worker> findByUserId(UUID userId);

    // جلب المختصين الظاهرين للزبائن: worker نشط + user مكمل ملفه
    @Query(
            value = "SELECT w.* FROM workers w " +
                    "JOIN users u ON u.id = w.user_id " +
                    "WHERE w.is_active = true AND u.profile_completed = true",
            countQuery = "SELECT COUNT(*) FROM workers w " +
                    "JOIN users u ON u.id = w.user_id " +
                    "WHERE w.is_active = true AND u.profile_completed = true",
            nativeQuery = true
    )
    Page<Worker> findVisibleWorkers(Pageable pageable);

    // جلب المختصين الظاهرين حسب الفئة
    @Query(
            value = "SELECT w.* FROM workers w " +
                    "JOIN users u ON u.id = w.user_id " +
                    "WHERE w.is_active = true AND u.profile_completed = true " +
                    "AND (:category IS NULL OR w.category = CAST(:category AS VARCHAR))",
            countQuery = "SELECT COUNT(*) FROM workers w " +
                    "JOIN users u ON u.id = w.user_id " +
                    "WHERE w.is_active = true AND u.profile_completed = true " +
                    "AND (:category IS NULL OR w.category = CAST(:category AS VARCHAR))",
            nativeQuery = true
    )
    Page<Worker> findVisibleWorkersWithCategory(@Param("category") String category, Pageable pageable);

    // جلب العمال المميزين الظاهرين للزبائن
    @Query(
            value = "SELECT w.* FROM workers w " +
                    "JOIN users u ON u.id = w.user_id " +
                    "WHERE w.is_active = true AND w.is_featured = true AND u.profile_completed = true",
            nativeQuery = true
    )
    List<Worker> findVisibleFeaturedWorkers();

    // جلب العمال حسب الفئة
    Page<Worker> findByCategoryAndIsActiveTrue(WorkerCategory category, Pageable pageable);

    // جلب العمال النشطين
    Page<Worker> findByIsActiveTrue(Pageable pageable);

    // جلب العمال المميزين
    List<Worker> findByIsFeaturedTrueAndIsActiveTrue();

    // البحث بالاسم
    @Query(
            value = "SELECT w.* FROM workers w " +
                    "JOIN users u ON u.id = w.user_id " +
                    "WHERE w.is_active = true AND u.profile_completed = true AND (" +
                    "LOWER(w.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                    "LOWER(w.description) LIKE LOWER(CONCAT('%', :query, '%')))",
            countQuery = "SELECT COUNT(*) FROM workers w " +
                    "JOIN users u ON u.id = w.user_id " +
                    "WHERE w.is_active = true AND u.profile_completed = true AND (" +
                    "LOWER(w.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                    "LOWER(w.description) LIKE LOWER(CONCAT('%', :query, '%')))",
            nativeQuery = true
    )
    Page<Worker> searchWorkers(@Param("query") String query, Pageable pageable);

    // البحث مع الفلترة
    @Query(
            value = "SELECT w.* FROM workers w " +
                    "JOIN users u ON u.id = w.user_id " +
                    "WHERE w.is_active = true AND u.profile_completed = true " +
                    "AND (:category IS NULL OR w.category = CAST(:category AS VARCHAR)) " +
                    "AND (:minRating IS NULL OR w.average_rating >= :minRating) " +
                    "AND (:location IS NULL OR LOWER(w.location) LIKE LOWER(CONCAT('%', :location, '%')))",
            countQuery = "SELECT COUNT(*) FROM workers w " +
                    "JOIN users u ON u.id = w.user_id " +
                    "WHERE w.is_active = true AND u.profile_completed = true " +
                    "AND (:category IS NULL OR w.category = CAST(:category AS VARCHAR)) " +
                    "AND (:minRating IS NULL OR w.average_rating >= :minRating) " +
                    "AND (:location IS NULL OR LOWER(w.location) LIKE LOWER(CONCAT('%', :location, '%')))",
            nativeQuery = true
    )
    Page<Worker> findWithFilters(
            @Param("category") String category,
            @Param("minRating") Double minRating,
            @Param("location") String location,
            Pageable pageable
    );

    // عدد العمال حسب الفئة
    @Query(
            value = "SELECT COUNT(*) FROM workers w " +
                    "JOIN users u ON u.id = w.user_id " +
                    "WHERE w.is_active = true AND u.profile_completed = true AND w.category = CAST(:category AS VARCHAR)",
            nativeQuery = true
    )
    long countVisibleByCategory(@Param("category") String category);

    @Query(
            value = "SELECT " +
                    "w.id AS id, " +
                    "w.name AS name, " +
                    "w.category AS category, " +
                    "w.profile_image_url AS profileImageUrl, " +
                    "w.profile_image AS profileImage, " +
                    "w.profile_image_type AS profileImageType, " +
                    "w.experience_years AS experienceYears, " +
                    "w.specialized_experience_years AS specializedExperienceYears, " +
                    "w.specialization AS specialization, " +
                    "w.city AS city, " +
                    "w.area AS area, " +
                    "w.average_rating AS averageRating, " +
                    "w.review_count AS reviewCount, " +
                    "w.is_featured AS isFeatured, " +
                    "COALESCE(p.is_online, false) AS isOnline " +
                    "FROM workers w " +
                    "JOIN users u ON u.id = w.user_id " +
                    "LEFT JOIN user_presence p ON p.user_id = u.id " +
                    "WHERE w.is_active = true AND u.profile_completed = true " +
                    "AND (:category IS NULL OR w.category = CAST(:category AS VARCHAR)) " +
                    "AND (:city IS NULL OR LOWER(w.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
                    "AND (:area IS NULL OR LOWER(w.area) LIKE LOWER(CONCAT('%', :area, '%'))) " +
                    "AND (:q IS NULL OR " +
                    "LOWER(w.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
                    "LOWER(w.specialization) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
                    "LOWER(w.description) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
                    "LOWER(w.city) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
                    "LOWER(w.area) LIKE LOWER(CONCAT('%', :q, '%'))) " +
                    "AND (:availableNow IS NULL OR :availableNow = false OR p.is_online = true) " +
                    "ORDER BY " +
                    "COALESCE(p.is_online, false) DESC, " +
                    "w.is_featured DESC, " +
                    "w.average_rating DESC NULLS LAST, " +
                    "w.review_count DESC, " +
                    "w.id DESC",
            countQuery = "SELECT COUNT(*) " +
                    "FROM workers w " +
                    "JOIN users u ON u.id = w.user_id " +
                    "LEFT JOIN user_presence p ON p.user_id = u.id " +
                    "WHERE w.is_active = true AND u.profile_completed = true " +
                    "AND (:category IS NULL OR w.category = CAST(:category AS VARCHAR)) " +
                    "AND (:city IS NULL OR LOWER(w.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
                    "AND (:area IS NULL OR LOWER(w.area) LIKE LOWER(CONCAT('%', :area, '%'))) " +
                    "AND (:q IS NULL OR " +
                    "LOWER(w.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
                    "LOWER(w.specialization) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
                    "LOWER(w.description) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
                    "LOWER(w.city) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
                    "LOWER(w.area) LIKE LOWER(CONCAT('%', :q, '%'))) " +
                    "AND (:availableNow IS NULL OR :availableNow = false OR p.is_online = true)",
            nativeQuery = true
    )
    org.springframework.data.domain.Page<ConsultationSpecialistProjection> findConsultationSpecialists(
            @Param("category") String category,
            @Param("q") String q,
            @Param("city") String city,
            @Param("area") String area,
            @Param("availableNow") Boolean availableNow,
            org.springframework.data.domain.Pageable pageable
    );
}
