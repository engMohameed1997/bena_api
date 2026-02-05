package com.bena.api.module.auth.service;

import com.bena.api.common.exception.ResourceNotFoundException;
import com.bena.api.common.service.EmailService;
import com.bena.api.module.audit.service.AuditLogService;
import com.bena.api.module.auth.dto.*;
import com.bena.api.module.auth.entity.EmailVerificationToken;
import com.bena.api.module.auth.entity.PasswordResetToken;
import com.bena.api.module.auth.repository.EmailVerificationTokenRepository;
import com.bena.api.module.auth.repository.PasswordResetTokenRepository;
import com.bena.api.module.user.dto.UserResponse;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.enums.UserRole;
import com.bena.api.module.user.enums.VerificationStatus;
import com.bena.api.module.user.repository.UserRepository;
import com.bena.api.module.worker.entity.Worker;
import com.bena.api.module.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    private final AuditLogService auditLogService;

    private Long resolveWorkerId(User user) {
        return workerRepository.findByUserId(user.getId())
                .map(Worker::getId)
                .orElse(null);
    }

    public AuthResponse register(RegisterRequest request) {
        log.debug("Registering new user with email: {}", request.getEmail());

        // التحقق من قوة كلمة المرور
        validatePasswordStrength(request.getPassword());

        // التحقق من عدم وجود الإيميل مسبقاً
        if (userRepository.existsByEmail(request.getEmail())) {
            // Security: Use generic message to prevent enumeration
            throw new IllegalArgumentException("بيانات التسجيل غير صالحة");
        }

        // التحقق من عدم وجود الهاتف مسبقاً
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
             // Security: Use generic message to prevent enumeration
            throw new IllegalArgumentException("بيانات التسجيل غير صالحة");
        }

        // تحديد الدور
        UserRole userRole = UserRole.USER;
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            try {
                UserRole requestedRole = UserRole.valueOf(request.getRole().toUpperCase());
                // Security Fix: Prevent ADMIN/SUPER_ADMIN registration via public endpoint
                if (requestedRole == UserRole.ADMIN || requestedRole == UserRole.SUPER_ADMIN) {
                    log.warn("Security Alert: Attempt to register as {} blocked for email: {}", requestedRole, request.getEmail());
                    userRole = UserRole.USER;
                } else {
                    userRole = requestedRole;
                }
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role provided: {}, defaulting to USER", request.getRole());
            }
        }

        // إنشاء المستخدم
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .governorate(request.getGovernorate())
                .city(request.getCity())
                .isActive(true)
                .build();

        // المستخدم العادي والخلفة مكتمل الملف مباشرة
        if (userRole == UserRole.USER || userRole == UserRole.CLIENT || userRole == UserRole.WORKER) {
            user.setProfileCompleted(true);
            user.setVerificationStatus(VerificationStatus.APPROVED);
            user.setDocumentVerified(true);
        } else {
            // المقاول والمهندس والمصمم يحتاجون إكمال الملف
            user.setProfileCompleted(false);
            user.setVerificationStatus(VerificationStatus.PENDING);
            user.setDocumentVerified(false);
        }

        user = userRepository.save(user);
        log.info("User registered successfully with id: {}", user.getId());

        // ✅ تسجيل في Audit Log
        auditLogService.logAsync(
            com.bena.api.module.audit.entity.AuditLog.AuditAction.USER_REGISTER,
            com.bena.api.module.audit.entity.AuditLog.AuditTargetType.USER,
            user.getId().toString(),
            "New user registered: " + user.getEmail() + " (Role: " + user.getRole() + ")"
        );

        // إنشاء التوكن
        String token = jwtService.generateToken(user.getId(), user.getRole().name());

        return AuthResponse.of(token, jwtService.getExpirationTime(), UserResponse.from(user, resolveWorkerId(user)));
    }

    public AuthResponse login(LoginRequest request) {
        log.debug("Login attempt for email: {}", request.getEmail());

        // البحث عن المستخدم
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("البريد الإلكتروني أو كلمة المرور غير صحيحة"));

        // التحقق من أن الحساب غير مقفل
        if (!user.isAccountNonLocked()) {
            throw new IllegalArgumentException("تم قفل الحساب مؤقتاً بسبب تكرار المحاولات الخاطئة. يرجى المحاولة لاحقاً.");
        }

        // التحقق من كلمة المرور
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            // تسجيل محاولة فاشلة
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= 5) {
                user.setLockTime(java.time.OffsetDateTime.now().plusMinutes(15)); // قفل لمدة 15 دقيقة
            }
            userRepository.save(user);
            
            // ✅ تسجيل محاولة الدخول الفاشلة
            auditLogService.logFailedLogin(request.getEmail(), "Invalid password");
            
            throw new IllegalArgumentException("البريد الإلكتروني أو كلمة المرور غير صحيحة");
        }

        // إعادة تعيين محاولات الفشل عند النجاح
        if (user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            user.setLockTime(null);
            userRepository.save(user); // Save resetting of attempts
        }

        // التحقق من أن الحساب نشط
        if (!user.getIsActive()) {
            throw new IllegalArgumentException("الحساب معطل، يرجى التواصل مع الدعم");
        }

        log.info("User logged in successfully: {}", user.getId());

        // ✅ تسجيل تسجيل الدخول الناجح
        auditLogService.logLogin(user.getId(), user.getEmail());

        // إنشاء التوكن
        String token = jwtService.generateToken(user.getId(), user.getRole().name());

        return AuthResponse.of(token, jwtService.getExpirationTime(), UserResponse.from(user, resolveWorkerId(user)));
    }

    public UserResponse getCurrentUser(String token) {
        String userId = jwtService.extractUserId(token);
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("المستخدم غير موجود"));
        return UserResponse.from(user, resolveWorkerId(user));
    }
    
    /**
     * طلب إعادة تعيين كلمة المرور - يدعم البريد الإلكتروني ورقم الهاتف
     */
    public void forgotPassword(ForgotPasswordRequest request) {
        String identifier = request.getIdentifier();
        log.debug("Password reset requested for: {}", identifier);
        
        User user = null;
        
        // البحث حسب النوع
        if (request.isEmail()) {
            user = userRepository.findByEmail(identifier).orElse(null);
        } else if (request.isPhone()) {
            user = userRepository.findByPhone(identifier).orElse(null);
        } else {
            // تحديد النوع تلقائياً
            if (identifier.contains("@")) {
                user = userRepository.findByEmail(identifier).orElse(null);
            } else {
                user = userRepository.findByPhone(identifier).orElse(null);
            }
        }
        
        // Security Fix: Prevent Account Enumeration
        // If user does not exist, we just return safely without throwing an exception.
        // The controller will still return "Code sent successfully" to the user.
        if (user == null) {
            log.warn("Password reset requested for non-existent identifier: {}", identifier);
            // Simulate processing time to prevent timing attacks (optional but good practice)
            try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return;
        }
        
        // حذف أي tokens قديمة
        passwordResetTokenRepository.deleteByUserId(user.getId());
        
        // إنشاء token جديد (6 أرقام) باستخدام SecureRandom
        String token = String.format("%06d", new SecureRandom().nextInt(1000000));
        
        PasswordResetToken resetToken = new PasswordResetToken();
        // Security: Hash the token before storing
        resetToken.setToken(hashToken(token));
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(10)); // صالح لمدة 10 دقائق
        
        passwordResetTokenRepository.save(resetToken);
        
        // إرسال الرمز حسب النوع
        if (request.isPhone() || (!request.isEmail() && !identifier.contains("@"))) {
            // إرسال SMS (حالياً نطبع فقط - يمكن ربطه بخدمة SMS لاحقاً)
            // Security: Do not log the actual token
            log.info("SMS OTP sent to {}", user.getPhone());
            emailService.sendSmsOtp(user.getPhone(), token);
        } else {
            // إرسال البريد الإلكتروني
            emailService.sendPasswordResetEmail(user.getEmail(), token, user.getFullName());
            log.info("Password reset email sent to: {}", user.getEmail());
        }
    }
    
    /**
     * إعادة تعيين كلمة المرور
     */
    public void resetPassword(ResetPasswordRequest request) {
        log.debug("Password reset attempt");

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("البيانات المقدمة غير صحيحة"));

        // التحقق من قوة كلمة المرور
        validatePasswordStrength(request.getNewPassword());
        
        // التحقق من تطابق كلمات المرور
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("كلمات المرور غير متطابقة");
        }
        
        // البحث عن الـ token الخاص بالمستخدم
        PasswordResetToken resetToken = passwordResetTokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("لم يتم طلب إعادة تعيين كلمة المرور"));
        
        // Check failed attempts
        if (resetToken.getFailedAttempts() >= 5) {
            passwordResetTokenRepository.delete(resetToken);
            throw new IllegalArgumentException("تم تجاوز عدد المحاولات المسموح بها. يرجى طلب رمز جديد.");
        }

        // Validate Token Hash
        if (!resetToken.getToken().equals(hashToken(request.getToken()))) {
            resetToken.incrementFailedAttempts();
            passwordResetTokenRepository.save(resetToken);
            throw new IllegalArgumentException("رمز التحقق غير صالح");
        }

        // التحقق من صلاحية الـ token
        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("رمز التحقق منتهي الصلاحية");
        }
        
        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("رمز التحقق مستخدم مسبقاً");
        }
        
        // تحديث كلمة المرور
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // تحديث الـ token كمستخدم
        resetToken.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(resetToken);
        
        // ✅ تسجيل تغيير كلمة المرور
        auditLogService.logAsync(
            com.bena.api.module.audit.entity.AuditLog.AuditAction.PASSWORD_RESET,
            com.bena.api.module.audit.entity.AuditLog.AuditTargetType.USER,
            user.getId().toString(),
            "Password reset successful for: " + user.getEmail()
        );
        
        log.info("Password reset successful for user: {}", user.getId());
    }
    
    /**
     * التحقق من صلاحية رمز إعادة التعيين
     */
    public boolean verifyResetToken(String token) {
        // Look up by HASH of the token
        return passwordResetTokenRepository.findByToken(hashToken(token))
                .map(t -> !t.isExpired() && !t.isUsed())
                .orElse(false);
    }
    
    /**
     * إرسال رمز تأكيد البريد الإلكتروني
     */
    public void sendVerificationEmail(java.util.UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("المستخدم غير موجود"));
        
        // حذف أي tokens قديمة
        emailVerificationTokenRepository.deleteByUserId(userId);
        
        // إنشاء token جديد (6 أرقام) باستخدام SecureRandom
        String token = String.format("%06d", new SecureRandom().nextInt(1000000));
        
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        // Security: Hash token
        verificationToken.setToken(hashToken(token));
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(java.time.LocalDateTime.now().plusHours(1)); // تم تقليل الصلاحية لساعة واحدة لدواعي أمنية
        
        emailVerificationTokenRepository.save(verificationToken);
        
        // إرسال البريد
        emailService.sendVerificationEmail(user.getEmail(), token, user.getFullName());
        
        log.info("Verification email sent to: {}", user.getEmail());
    }
    
    /**
     * تأكيد البريد الإلكتروني
     */
    /**
     * تأكيد البريد الإلكتروني
     */
    public void verifyEmail(VerifyEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("البيانات المقدمة غير صحيحة"));

        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("رمز التحقق غير صالح"));

        if (verificationToken.getFailedAttempts() >= 5) {
            emailVerificationTokenRepository.delete(verificationToken);
            throw new IllegalArgumentException("تم تجاوز عدد المحاولات المسموح بها");
        }

        if (!verificationToken.getToken().equals(hashToken(request.getToken()))) {
            verificationToken.incrementFailedAttempts();
            emailVerificationTokenRepository.save(verificationToken);
            throw new IllegalArgumentException("رمز التحقق غير صالح");
        }
        
        if (verificationToken.isExpired()) {
            throw new IllegalArgumentException("رمز التحقق منتهي الصلاحية");
        }
        
        if (verificationToken.isVerified()) {
            throw new IllegalArgumentException("البريد الإلكتروني مؤكد مسبقاً");
        }
        
        // تحديث حالة المستخدم
        user.setEmailVerified(true);
        userRepository.save(user);
        
        // تحديث الـ token
        verificationToken.setVerifiedAt(java.time.LocalDateTime.now());
        emailVerificationTokenRepository.save(verificationToken);
        
        log.info("Email verified for user: {}", user.getId());
    }
    
    /**
     * إعادة إرسال رمز التأكيد
     */
    public void resendVerificationEmail(String email) {
        // Security Fix: Prevent User Enumeration
        // We do not throw exceptions if user is not found or already verified
        userRepository.findByEmail(email).ifPresent(user -> {
            boolean isVerified = user.getEmailVerified() != null && user.getEmailVerified();
            if (!isVerified) {
                sendVerificationEmail(user.getId());
            }
        });
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

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (int i = 0; i < encodedhash.length; i++) {
                String hex = Integer.toHexString(0xff & encodedhash[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
