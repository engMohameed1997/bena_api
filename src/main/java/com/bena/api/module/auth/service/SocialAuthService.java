package com.bena.api.module.auth.service;

import com.bena.api.module.auth.dto.AppleAuthRequest;
import com.bena.api.module.auth.dto.AuthResponse;
import com.bena.api.module.auth.dto.GoogleAuthRequest;
import com.bena.api.module.audit.service.AuditLogService;
import com.bena.api.module.user.dto.UserResponse;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.enums.UserRole;
import com.bena.api.module.user.enums.VerificationStatus;
import com.bena.api.module.user.repository.UserRepository;
import com.bena.api.module.worker.entity.Worker;
import com.bena.api.module.worker.repository.WorkerRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * خدمة المصادقة الاجتماعية (Google و Apple)
 * Social Authentication Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SocialAuthService {

    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;

    @Value("${apple.client-id:com.bena.app}")
    private String appleClientId;

    private static final String APPLE_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Long resolveWorkerId(User user) {
        return workerRepository.findByUserId(user.getId())
                .map(Worker::getId)
                .orElse(null);
    }

    /**
     * تسجيل الدخول بواسطة Google
     * يتحقق من ID Token مع Firebase ثم ينشئ/يسجل دخول المستخدم
     */
    public AuthResponse authenticateWithGoogle(GoogleAuthRequest request) {
        log.debug("Google authentication attempt");

        try {
            // التحقق من Google ID Token باستخدام Firebase Admin SDK
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.getIdToken());

            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            String googleId = decodedToken.getUid();
            String photoUrl = decodedToken.getPicture();

            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("لا يمكن الحصول على البريد الإلكتروني من Google");
            }

            log.info("Google token verified for email: {}", email);

            // البحث عن المستخدم أو إنشاء حساب جديد
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> createUserFromGoogle(email, name, googleId, photoUrl, request.getRole()));

            // تحديث Google ID إذا لم يكن موجوداً
            if (user.getGoogleId() == null) {
                user.setGoogleId(googleId);
                userRepository.save(user);
            }

            // التحقق من أن الحساب نشط
            if (!user.getIsActive()) {
                throw new IllegalArgumentException("الحساب معطل، يرجى التواصل مع الدعم");
            }

            // تسجيل تسجيل الدخول
            auditLogService.logLogin(user.getId(), user.getEmail());

            // إنشاء JWT Token
            String token = jwtService.generateToken(user.getId(), user.getRole().name());

            log.info("Google authentication successful for user: {}", user.getId());

            return AuthResponse.of(token, jwtService.getExpirationTime(),
                    UserResponse.from(user, resolveWorkerId(user)));

        } catch (FirebaseAuthException e) {
            log.error("Firebase token verification failed: {}", e.getMessage());
            throw new IllegalArgumentException("فشل التحقق من حساب Google: " + e.getMessage());
        }
    }

    /**
     * تسجيل الدخول بواسطة Apple
     * يتحقق من Identity Token مع Apple ثم ينشئ/يسجل دخول المستخدم
     */
    public AuthResponse authenticateWithApple(AppleAuthRequest request) {
        log.debug("Apple authentication attempt");

        try {
            // التحقق من Apple Identity Token
            Claims claims = verifyAppleToken(request.getIdentityToken());

            String appleId = claims.getSubject(); // Apple User ID
            String email = claims.get("email", String.class);

            // Apple قد لا يرسل البريد في كل مرة
            if (email == null && request.getUser() != null) {
                email = request.getUser().getEmail();
            }

            if (email == null || email.isEmpty()) {
                // البحث عن المستخدم بـ Apple ID
                User existingUser = userRepository.findByAppleId(appleId).orElse(null);
                if (existingUser != null) {
                    email = existingUser.getEmail();
                } else {
                    throw new IllegalArgumentException("لا يمكن الحصول على البريد الإلكتروني من Apple");
                }
            }

            // استخراج الاسم
            String fullName = null;
            if (request.getUser() != null && request.getUser().getName() != null) {
                String firstName = request.getUser().getName().getFirstName();
                String lastName = request.getUser().getName().getLastName();
                if (firstName != null || lastName != null) {
                    fullName = ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
                }
            }

            log.info("Apple token verified for email: {}", email);

            // البحث عن المستخدم أو إنشاء حساب جديد
            final String finalEmail = email;
            final String finalFullName = fullName;
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> userRepository.findByAppleId(appleId)
                            .orElseGet(
                                    () -> createUserFromApple(finalEmail, finalFullName, appleId, request.getRole())));

            // تحديث Apple ID إذا لم يكن موجوداً
            if (user.getAppleId() == null) {
                user.setAppleId(appleId);
                userRepository.save(user);
            }

            // التحقق من أن الحساب نشط
            if (!user.getIsActive()) {
                throw new IllegalArgumentException("الحساب معطل، يرجى التواصل مع الدعم");
            }

            // تسجيل تسجيل الدخول
            auditLogService.logLogin(user.getId(), user.getEmail());

            // إنشاء JWT Token
            String token = jwtService.generateToken(user.getId(), user.getRole().name());

            log.info("Apple authentication successful for user: {}", user.getId());

            return AuthResponse.of(token, jwtService.getExpirationTime(),
                    UserResponse.from(user, resolveWorkerId(user)));

        } catch (Exception e) {
            log.error("Apple authentication failed: {}", e.getMessage());
            throw new IllegalArgumentException("فشل التحقق من حساب Apple: " + e.getMessage());
        }
    }

    /**
     * إنشاء مستخدم جديد من بيانات Google
     */
    private User createUserFromGoogle(String email, String name, String googleId, String photoUrl,
            String requestedRole) {
        log.info("Creating new user from Google: {}", email);

        UserRole role = determineRole(requestedRole);

        User user = User.builder()
                .email(email)
                .fullName(name != null ? name : email.split("@")[0])
                .googleId(googleId)
                .profilePicture(photoUrl)
                .role(role)
                .isActive(true)
                .emailVerified(true) // البريد مؤكد من Google
                .build();

        // تعيين حالة الملف الشخصي
        setProfileStatus(user, role);

        user = userRepository.save(user);

        // تسجيل في Audit Log
        auditLogService.logAsync(
                com.bena.api.module.audit.entity.AuditLog.AuditAction.USER_REGISTER,
                com.bena.api.module.audit.entity.AuditLog.AuditTargetType.USER,
                user.getId().toString(),
                "New user registered via Google: " + email + " (Role: " + role + ")");

        return user;
    }

    /**
     * إنشاء مستخدم جديد من بيانات Apple
     */
    private User createUserFromApple(String email, String fullName, String appleId, String requestedRole) {
        log.info("Creating new user from Apple: {}", email);

        UserRole role = determineRole(requestedRole);

        User user = User.builder()
                .email(email)
                .fullName(fullName != null ? fullName : email.split("@")[0])
                .appleId(appleId)
                .role(role)
                .isActive(true)
                .emailVerified(true) // البريد مؤكد من Apple
                .build();

        // تعيين حالة الملف الشخصي
        setProfileStatus(user, role);

        user = userRepository.save(user);

        // تسجيل في Audit Log
        auditLogService.logAsync(
                com.bena.api.module.audit.entity.AuditLog.AuditAction.USER_REGISTER,
                com.bena.api.module.audit.entity.AuditLog.AuditTargetType.USER,
                user.getId().toString(),
                "New user registered via Apple: " + email + " (Role: " + role + ")");

        return user;
    }

    /**
     * تحديد الدور من الطلب
     */
    private UserRole determineRole(String requestedRole) {
        if (requestedRole == null || requestedRole.isEmpty()) {
            return UserRole.USER;
        }

        try {
            UserRole role = UserRole.valueOf(requestedRole.toUpperCase());
            // منع التسجيل كـ ADMIN
            if (role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN) {
                log.warn("Attempted social login with admin role, defaulting to USER");
                return UserRole.USER;
            }
            return role;
        } catch (IllegalArgumentException e) {
            return UserRole.USER;
        }
    }

    /**
     * تعيين حالة الملف الشخصي حسب الدور
     */
    private void setProfileStatus(User user, UserRole role) {
        if (role == UserRole.USER || role == UserRole.CLIENT || role == UserRole.WORKER) {
            user.setProfileCompleted(true);
            user.setVerificationStatus(VerificationStatus.APPROVED);
            user.setDocumentVerified(true);
        } else {
            user.setProfileCompleted(false);
            user.setVerificationStatus(VerificationStatus.PENDING);
            user.setDocumentVerified(false);
        }
    }

    /**
     * التحقق من Apple Identity Token
     */
    private Claims verifyAppleToken(String identityToken) throws Exception {
        // جلب مفاتيح Apple العامة
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(APPLE_KEYS_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode keysNode = objectMapper.readTree(response.body()).get("keys");

        // فك تشفير التوكن للحصول على header
        String[] parts = identityToken.split("\\.");
        String header = new String(Base64.getUrlDecoder().decode(parts[0]));
        JsonNode headerJson = objectMapper.readTree(header);
        String kid = headerJson.get("kid").asText();

        // البحث عن المفتاح المناسب
        RSAPublicKey publicKey = null;
        for (JsonNode keyNode : keysNode) {
            if (kid.equals(keyNode.get("kid").asText())) {
                String n = keyNode.get("n").asText();
                String e = keyNode.get("e").asText();
                publicKey = buildRSAPublicKey(n, e);
                break;
            }
        }

        if (publicKey == null) {
            throw new IllegalArgumentException("لم يتم العثور على مفتاح Apple المناسب");
        }

        // التحقق من التوكن
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(identityToken)
                .getPayload();
    }

    /**
     * بناء مفتاح RSA العام من n و e
     */
    private RSAPublicKey buildRSAPublicKey(String n, String e) throws Exception {
        byte[] nBytes = Base64.getUrlDecoder().decode(n);
        byte[] eBytes = Base64.getUrlDecoder().decode(e);

        BigInteger modulus = new BigInteger(1, nBytes);
        BigInteger exponent = new BigInteger(1, eBytes);

        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) factory.generatePublic(spec);
    }
}
