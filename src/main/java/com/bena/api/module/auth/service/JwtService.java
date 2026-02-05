package com.bena.api.module.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret:}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    private volatile SecretKey cachedSigningKey;
    private volatile boolean warnedAboutBlankSecret;

    public String generateToken(UUID userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return buildToken(claims, userId.toString(), jwtExpiration);
    }

    public String generateToken(UUID userId, String role, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put("role", role);
        return buildToken(claims, userId.toString(), jwtExpiration);
    }

    private String buildToken(Map<String, Object> claims, String subject, Long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS384)
                .compact();
    }

    public boolean isTokenValid(String token, String userId) {
        final String tokenUserId = extractUserId(token);
        return (tokenUserId.equals(userId)) && !isTokenExpired(token);
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        SecretKey local = cachedSigningKey;
        if (local != null) {
            return local;
        }

        synchronized (this) {
            if (cachedSigningKey != null) {
                return cachedSigningKey;
            }

            byte[] keyBytes;
            if (secretKey == null || secretKey.isBlank()) {
                if (!warnedAboutBlankSecret) {
                    log.warn("jwt.secret is blank; generating a random signing key. Existing tokens will become invalid after restart. Set JWT_SECRET/jwt.secret to a stable value.");
                    warnedAboutBlankSecret = true;
                }
                keyBytes = new byte[48];
                new SecureRandom().nextBytes(keyBytes);
            } else {
                byte[] raw = secretKey.getBytes(StandardCharsets.UTF_8);
                if (raw.length >= 48) {
                    keyBytes = raw;
                } else {
                    try {
                        keyBytes = MessageDigest.getInstance("SHA-384").digest(raw);
                    } catch (Exception e) {
                        keyBytes = new byte[48];
                        new SecureRandom().nextBytes(keyBytes);
                    }
                }
            }

            cachedSigningKey = Keys.hmacShaKeyFor(keyBytes);
            return cachedSigningKey;
        }
    }

    public Long getExpirationTime() {
        return jwtExpiration;
    }
}
