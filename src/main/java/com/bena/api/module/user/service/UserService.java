package com.bena.api.module.user.service;

import com.bena.api.common.exception.ResourceNotFoundException;
import com.bena.api.module.user.dto.UserRequest;
import com.bena.api.module.user.dto.UserResponse;
import com.bena.api.module.user.dto.UserUpdateRequest;
import com.bena.api.module.user.dto.ChangePasswordRequest;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.enums.UserRole;
import com.bena.api.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import com.bena.api.common.service.FileUploadService;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadService fileUploadService;

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAllActive(pageable)
                .map(UserResponse::from);
    }

    /**
     * بحث وفلترة المستخدمين (عدا ADMIN)
     * افتراضياً يجلب المستخدمين النشطين فقط
     */
    public Page<UserResponse> searchAndFilterUsers(Pageable pageable, String search, String role, Boolean isActive) {
        log.debug("Searching users with: search={}, role={}, isActive={}", search, role, isActive);

        // إذا لم يتم تحديد isActive، نجلب النشطين فقط افتراضياً
        Boolean effectiveIsActive = (isActive == null) ? true : isActive;

        // إذا لم يكن هناك filters، نجلب الكل النشطين (عدا ADMIN)
        if (search == null && role == null && effectiveIsActive) {
            return userRepository.findAllActiveByRoleNot(UserRole.ADMIN, pageable)
                    .map(UserResponse::from);
        }

        // Build dynamic query - استخدام effectiveIsActive بدلاً من isActive
        return userRepository.searchAndFilter(search, role != null ? UserRole.valueOf(role) : null, effectiveIsActive, pageable)
                .map(UserResponse::from);
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("المستخدم غير موجود"));
        return UserResponse.from(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("المستخدم غير موجود"));
        return UserResponse.from(user);
    }

    public UserResponse createUser(UserRequest request) {
        log.debug("Creating new user with email: {}, role: {}", request.getEmail(), request.getRole());

        // ملاحظة: الأدمن من لوحة التحكم يمكنه إنشاء أي دور (الفحص يتم في الـ Controller عبر @PreAuthorize)

        // التحقق من عدم وجود الإيميل مسبقاً
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("البريد الإلكتروني مستخدم مسبقاً");
        }

        // التحقق من عدم وجود الهاتف مسبقاً
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("رقم الهاتف مستخدم مسبقاً");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .governorate(request.getGovernorate())
                .city(request.getCity())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())  // ✅ استخدام الدور من الـ request
                .isActive(true)
                .profileCompleted(false)
                .documentVerified(false)
                .build();

        user = userRepository.save(user);
        log.info("User created successfully with id: {}, role: {}", user.getId(), user.getRole());

        return UserResponse.from(user);
    }

    public UserResponse updateUser(UUID id, UserUpdateRequest request) {
        log.debug("Updating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("المستخدم غير موجود"));

        // تحديث الحقول إذا تم إرسالها
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("البريد الإلكتروني مستخدم مسبقاً");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new IllegalArgumentException("رقم الهاتف مستخدم مسبقاً");
            }
            user.setPhone(request.getPhone());
        }

        if (request.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        user = userRepository.save(user);
        log.info("User updated successfully with id: {}", user.getId());

        return UserResponse.from(user);
    }

    public void deleteUser(UUID id) {
        log.debug("Soft deleting user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("المستخدم غير موجود"));

        // Soft delete
        user.setIsActive(false);
        userRepository.save(user);

        log.info("User soft deleted successfully with id: {}", id);
    }

    public void hardDeleteUser(UUID id) {
        log.debug("Hard deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("المستخدم غير موجود");
        }

        userRepository.deleteById(id);
        log.info("User hard deleted successfully with id: {}", id);
    }

    public UserResponse updateProfile(UUID userId, String fullName, String phone, String city, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("المستخدم غير موجود"));

        if (fullName != null && !fullName.isBlank()) {
            user.setFullName(fullName);
        }

        if (phone != null && !phone.isBlank() && !phone.equals(user.getPhone())) {
             if (userRepository.existsByPhone(phone)) {
                throw new IllegalArgumentException("رقم الهاتف مستخدم مسبقاً");
            }
            user.setPhone(phone);
        }

        if (city != null) {
            user.setCity(city);
        }

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = fileUploadService.uploadImage(image, "avatars");
                user.setProfilePictureUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("فشل في رفع الصورة", e);
            }
        }

        user = userRepository.save(user);
        return UserResponse.from(user);
    }

    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("المستخدم غير موجود"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("كلمة المرور الحالية غير صحيحة");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("كلمات المرور غير متطابقة");
        }

        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw new IllegalArgumentException("كلمة المرور الجديدة يجب أن تختلف عن الحالية");
        }

        // نفس التحقق المستخدم في التسجيل
        validatePasswordStrength(request.getNewPassword());

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("كلمة المرور يجب أن تكون 8 أحرف على الأقل");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("كلمة المرور يجب أن تحتوي على حرف كبير واحد على الأقل");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("كلمة المرور يجب أن تحتوي على حرف صغير واحد على الأقل");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("كلمة المرور يجب أن تحتوي على رقم واحد على الأقل");
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new IllegalArgumentException("كلمة المرور يجب أن تحتوي على رمز خاص واحد على الأقل");
        }
    }
}
