package com.bena.api.config;

import com.bena.api.module.audit.entity.AuditLog.AuditAction;
import com.bena.api.module.audit.entity.AuditLog.AuditTargetType;
import com.bena.api.module.audit.service.AuditLogService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Rate Limiting Filter المُحسَّن
 * يطبق حدود مختلفة على أنواع مختلفة من الـ endpoints
 * 
 * التحسينات:
 * - Auth endpoints: 10 طلبات/دقيقة (للحماية من brute force)
 * - Admin endpoints: 60 طلبات/دقيقة (للسماح بالعمل الطبيعي)
 * - Upload endpoints: 20 طلبات/دقيقة (للحماية من spam)
 * - Search endpoints: 30 طلبات/دقيقة
 * - General API: 120 طلبات/دقيقة
 */
@org.springframework.stereotype.Component
@org.springframework.scheduling.annotation.EnableScheduling
@Slf4j
public class RateLimitConfig implements Filter {
    
    // Rate limits لكل نوع من الـ endpoints
    private static final int AUTH_LIMIT = 10;           // صارم للحماية من brute force
    private static final int ADMIN_LIMIT = 60;          // مرن للعمل الإداري
    private static final int UPLOAD_LIMIT = 20;         // محدود لمنع spam الملفات
    private static final int SEARCH_LIMIT = 30;         // محدود لمنع scraping
    private static final int GENERAL_LIMIT = 120;       // عام للتصفح الطبيعي
    
    private static final long WINDOW_SIZE_MS = 60 * 1000; // دقيقة واحدة
    
    // Buckets منفصلة لكل نوع endpoint
    private final Map<String, RateLimitBucket> authBuckets = new ConcurrentHashMap<>();
    private final Map<String, RateLimitBucket> adminBuckets = new ConcurrentHashMap<>();
    private final Map<String, RateLimitBucket> uploadBuckets = new ConcurrentHashMap<>();
    private final Map<String, RateLimitBucket> searchBuckets = new ConcurrentHashMap<>();
    private final Map<String, RateLimitBucket> generalBuckets = new ConcurrentHashMap<>();
    
    @Autowired
    @Lazy  // Lazy لتجنب circular dependency
    private AuditLogService auditLogService;
    
    /**
     * تنظيف دوري للسجلات القديمة كل ساعة لمنع تسرب الذاكرة
     */
    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 3600000)
    public void cleanupStoredBuckets() {
        long now = System.currentTimeMillis();
        long cutoff = 3600000; // ساعة واحدة
        
        cleanupBucketMap(authBuckets, now, cutoff);
        cleanupBucketMap(adminBuckets, now, cutoff);
        cleanupBucketMap(uploadBuckets, now, cutoff);
        cleanupBucketMap(searchBuckets, now, cutoff);
        cleanupBucketMap(generalBuckets, now, cutoff);
        
        log.debug("🧹 Rate limit buckets cleanup completed");
    }
    
    private void cleanupBucketMap(Map<String, RateLimitBucket> map, long now, long cutoff) {
        map.entrySet().removeIf(entry -> now - entry.getValue().getLastAccessed() > cutoff);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI();
        String clientIP = getClientIP(httpRequest);
        
        // تحديد نوع الـ endpoint والحد المناسب
        RateLimitResult result = checkRateLimit(path, clientIP);
        
        if (result.allowed) {
            chain.doFilter(request, response);
        } else {
            // تسجيل تجاوز الحد
            logRateLimitExceeded(clientIP, path, result.endpointType);
            
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.setHeader("Retry-After", "60");
            httpResponse.setHeader("X-RateLimit-Type", result.endpointType);
            httpResponse.getWriter().write(
                "{\"success\":false,\"message\":\"طلبات كثيرة جداً. يرجى الانتظار دقيقة واحدة.\",\"retryAfter\":60}"
            );
        }
    }
    
    private RateLimitResult checkRateLimit(String path, String clientIP) {
        // Auth endpoints - أكثر صرامة
        if (path.startsWith("/v1/auth/")) {
            return new RateLimitResult(
                checkBucket(authBuckets, clientIP, AUTH_LIMIT),
                "AUTH"
            );
        }
        
        // Admin endpoints
        if (path.startsWith("/v1/admin/") || path.startsWith("/admin/")) {
            return new RateLimitResult(
                checkBucket(adminBuckets, clientIP, ADMIN_LIMIT),
                "ADMIN"
            );
        }
        
        // Upload endpoints
        if (path.contains("/upload") || path.contains("/media")) {
            return new RateLimitResult(
                checkBucket(uploadBuckets, clientIP, UPLOAD_LIMIT),
                "UPLOAD"
            );
        }
        
        // Search endpoints
        if (path.contains("search") || path.contains("filter") || 
            (httpRequestHasSearchParam())) {
            return new RateLimitResult(
                checkBucket(searchBuckets, clientIP, SEARCH_LIMIT),
                "SEARCH"
            );
        }
        
        // Sensitive operations (delete, update on critical resources)
        if (path.matches(".*/users/.*") || path.matches(".*/workers/.*")) {
            return new RateLimitResult(
                checkBucket(generalBuckets, clientIP, GENERAL_LIMIT),
                "GENERAL"
            );
        }
        
        // Public read endpoints - no strict limit, but still track
        if (path.startsWith("/v1/workers") || path.startsWith("/v1/designs") || 
            path.startsWith("/v1/building-steps") || path.startsWith("/v1/cost")) {
            return new RateLimitResult(
                checkBucket(generalBuckets, clientIP, GENERAL_LIMIT * 2), // أكثر سماحية للقراءة
                "PUBLIC_READ"
            );
        }
        
        // Default general limit
        return new RateLimitResult(
            checkBucket(generalBuckets, clientIP, GENERAL_LIMIT),
            "GENERAL"
        );
    }
    
    private boolean checkBucket(Map<String, RateLimitBucket> buckets, String clientIP, int limit) {
        RateLimitBucket bucket = buckets.computeIfAbsent(clientIP, k -> new RateLimitBucket());
        return bucket.tryConsume(limit);
    }
    
    private boolean httpRequestHasSearchParam() {
        try {
            jakarta.servlet.http.HttpServletRequest request = 
                ((org.springframework.web.context.request.ServletRequestAttributes) 
                 org.springframework.web.context.request.RequestContextHolder.getRequestAttributes())
                .getRequest();
            return request.getParameter("search") != null || request.getParameter("query") != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    private void logRateLimitExceeded(String clientIP, String path, String endpointType) {
        log.warn("🚫 Rate limit exceeded: IP={}, path={}, type={}", clientIP, path, endpointType);
        
        try {
            if (auditLogService != null) {
                auditLogService.logSecurityAlert(
                    "Rate limit exceeded: " + endpointType + " - Path: " + path,
                    clientIP
                );
            }
        } catch (Exception e) {
            // Ignore audit logging failures
        }
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        return request.getRemoteAddr();
    }
    
    /**
     * Result object لنتيجة فحص Rate Limit
     */
    private static class RateLimitResult {
        final boolean allowed;
        final String endpointType;
        
        RateLimitResult(boolean allowed, String endpointType) {
            this.allowed = allowed;
            this.endpointType = endpointType;
        }
    }
    
    /**
     * Rate Limit Bucket مُحسَّن
     */
    private static class RateLimitBucket {
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private final AtomicLong windowStart = new AtomicLong(System.currentTimeMillis());
        private final AtomicLong lastAccessed = new AtomicLong(System.currentTimeMillis());
        
        public synchronized boolean tryConsume(int limit) {
            long now = System.currentTimeMillis();
            lastAccessed.set(now);
            long windowStartTime = windowStart.get();
            
            // إذا انتهت النافذة الزمنية، أعد التعيين
            if (now - windowStartTime >= WINDOW_SIZE_MS) {
                requestCount.set(0);
                windowStart.set(now);
            }
            
            // تحقق من الحد
            if (requestCount.get() < limit) {
                requestCount.incrementAndGet();
                return true;
            }
            
            return false;
        }
        
        public long getLastAccessed() {
            return lastAccessed.get();
        }
    }
}
