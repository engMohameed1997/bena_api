package com.bena.api.module.user.repository;

import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

        Optional<User> findByEmail(String email);

        Optional<User> findByPhone(String phone);

        Optional<User> findByGoogleId(String googleId);

        Optional<User> findByAppleId(String appleId);

        boolean existsByEmail(String email);

        boolean existsByPhone(String phone);

        @Query("SELECT u FROM User u WHERE u.isActive = true")
        Page<User> findAllActive(Pageable pageable);

        @Query("SELECT u FROM User u WHERE u.isActive = true AND u.role = :role")
        Page<User> findAllActiveByRole(String role, Pageable pageable);

        long countByIsActiveTrue();

        long countByCreatedAtBetween(java.time.OffsetDateTime start, java.time.OffsetDateTime end);

        /**
         * جلب جميع المستخدمين النشطين عدا دور معين (مثلاً عدا ADMIN)
         */
        @Query("SELECT u FROM User u WHERE u.role != :role AND u.isActive = true")
        Page<User> findAllActiveByRoleNot(@Param("role") UserRole role, Pageable pageable);

        /**
         * جلب جميع المستخدمين عدا دور معين (بما فيهم غير النشطين - للأدمن فقط)
         */
        Page<User> findAllByRoleNot(UserRole role, Pageable pageable);

        /**
         * بحث وفلترة متقدمة للمستخدمين
         * يبحث في: الاسم، الإيميل، الهاتف، المحافظة، المدينة
         * ويفلتر حسب: الدور، الحالة النشطة
         */
        @Query("SELECT u FROM User u WHERE " +
                        "u.role != 'ADMIN' AND " +
                        "(:search IS NULL OR " +
                        "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(u.governorate) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(u.city) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
                        "(:role IS NULL OR u.role = :role) AND " +
                        "(:isActive IS NULL OR u.isActive = :isActive)")
        Page<User> searchAndFilter(
                        @Param("search") String search,
                        @Param("role") UserRole role,
                        @Param("isActive") Boolean isActive,
                        Pageable pageable);
}
