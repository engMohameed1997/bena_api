package com.bena.api.module.admin.service;

import com.bena.api.module.admin.dto.SystemHealthDto;
import com.bena.api.module.admin.dto.SystemHealthDto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * خدمة مراقبة صحة النظام
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemHealthService {

    private final DataSource dataSource;
    private final Environment environment;

    @Value("${spring.application.name:Bena API}")
    private String applicationName;

    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;

    /**
     * جلب حالة صحة النظام الكاملة
     */
    public SystemHealthDto getSystemHealth() {
        ComponentHealth dbHealth = getDatabaseHealth();
        MemoryInfo memoryInfo = getMemoryInfo();
        DiskInfo diskInfo = getDiskInfo();

        // تحديد الحالة العامة
        String overallStatus = "UP";
        if ("DOWN".equals(dbHealth.getStatus())) {
            overallStatus = "DOWN";
        } else if (memoryInfo.getUsagePercentage() > 90 || diskInfo.getUsagePercentage() > 90) {
            overallStatus = "DEGRADED";
        }

        return SystemHealthDto.builder()
                .status(overallStatus)
                .timestamp(OffsetDateTime.now())
                .database(dbHealth)
                .memory(memoryInfo)
                .disk(diskInfo)
                .system(getSystemInfo())
                .metrics(getMetrics())
                .build();
    }

    /**
     * فحص حالة قاعدة البيانات
     */
    public ComponentHealth getDatabaseHealth() {
        long startTime = System.currentTimeMillis();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            long responseTime = System.currentTimeMillis() - startTime;

            return ComponentHealth.builder()
                    .name("PostgreSQL")
                    .status("UP")
                    .responseTimeMs(responseTime)
                    .version(metaData.getDatabaseProductVersion())
                    .details("Connected to " + metaData.getURL())
                    .lastChecked(OffsetDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("❌ Database health check failed", e);
            return ComponentHealth.builder()
                    .name("PostgreSQL")
                    .status("DOWN")
                    .responseTimeMs(System.currentTimeMillis() - startTime)
                    .details("Error: " + e.getMessage())
                    .lastChecked(OffsetDateTime.now())
                    .build();
        }
    }

    /**
     * معلومات استخدام الذاكرة
     */
    public MemoryInfo getMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long used = total - free;
        long max = runtime.maxMemory();
        
        double usagePercentage = (double) used / max * 100;

        String status = usagePercentage < 70 ? "HEALTHY" : 
                       usagePercentage < 85 ? "WARNING" : "CRITICAL";

        return MemoryInfo.builder()
                .status(status)
                .totalBytes(total)
                .usedBytes(used)
                .freeBytes(free)
                .maxBytes(max)
                .usagePercentage(Math.round(usagePercentage * 100.0) / 100.0)
                .totalFormatted(formatBytes(total))
                .usedFormatted(formatBytes(used))
                .freeFormatted(formatBytes(free))
                .build();
    }

    /**
     * معلومات استخدام التخزين
     */
    public DiskInfo getDiskInfo() {
        File root = new File("/");
        if (!root.exists()) {
            root = new File("C:\\"); // Windows fallback
        }

        long total = root.getTotalSpace();
        long free = root.getFreeSpace();
        long used = total - free;
        
        double usagePercentage = total > 0 ? (double) used / total * 100 : 0;

        String status = usagePercentage < 70 ? "HEALTHY" : 
                       usagePercentage < 85 ? "WARNING" : "CRITICAL";

        // حجم مجلد uploads
        File uploadsDir = new File("uploads");
        String uploadsSize = uploadsDir.exists() ? formatBytes(getFolderSize(uploadsDir)) : "N/A";

        return DiskInfo.builder()
                .status(status)
                .totalBytes(total)
                .usedBytes(used)
                .freeBytes(free)
                .usagePercentage(Math.round(usagePercentage * 100.0) / 100.0)
                .totalFormatted(formatBytes(total))
                .usedFormatted(formatBytes(used))
                .freeFormatted(formatBytes(free))
                .uploadsFolderSize(uploadsSize)
                .build();
    }

    /**
     * معلومات النظام العامة
     */
    public SystemInfo getSystemInfo() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        long uptime = runtimeBean.getUptime();
        
        return SystemInfo.builder()
                .applicationName(applicationName)
                .applicationVersion(applicationVersion)
                .springBootVersion(SpringBootVersion.getVersion())
                .javaVersion(System.getProperty("java.version"))
                .osName(System.getProperty("os.name"))
                .osVersion(System.getProperty("os.version"))
                .availableProcessors(Runtime.getRuntime().availableProcessors())
                .uptimeMs(uptime)
                .uptimeFormatted(formatDuration(uptime))
                .startTime(OffsetDateTime.now().minusNanos(uptime * 1_000_000))
                .timezone(ZoneId.systemDefault().getId())
                .activeProfiles(String.join(", ", environment.getActiveProfiles()))
                .build();
    }

    /**
     * إحصائيات إضافية
     */
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        metrics.put("jvmProcessors", runtime.availableProcessors());
        metrics.put("threadCount", Thread.activeCount());
        
        return metrics;
    }

    // ==================== Helper Methods ====================

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }

    private String formatDuration(long millis) {
        Duration duration = Duration.ofMillis(millis);
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        
        if (days > 0) {
            return String.format("%d days, %d hours, %d minutes", days, hours, minutes);
        } else if (hours > 0) {
            return String.format("%d hours, %d minutes", hours, minutes);
        } else {
            return String.format("%d minutes", minutes);
        }
    }

    private long getFolderSize(File folder) {
        long size = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length();
                } else {
                    size += getFolderSize(file);
                }
            }
        }
        return size;
    }
}
