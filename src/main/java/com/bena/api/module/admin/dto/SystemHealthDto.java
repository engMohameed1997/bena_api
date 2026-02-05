package com.bena.api.module.admin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * DTO لحالة صحة النظام
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemHealthDto {

    private String status; // UP, DOWN, DEGRADED
    private OffsetDateTime timestamp;
    private ComponentHealth database;
    private ComponentHealth redis;
    private ComponentHealth firebase;
    private MemoryInfo memory;
    private DiskInfo disk;
    private SystemInfo system;
    private Map<String, Object> metrics;

    /**
     * معلومات حالة مكوّن
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentHealth {
        private String name;
        private String status; // UP, DOWN, UNKNOWN
        private Long responseTimeMs;
        private String version;
        private String details;
        private OffsetDateTime lastChecked;
    }

    /**
     * معلومات الذاكرة
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemoryInfo {
        private String status;
        private Long totalBytes;
        private Long usedBytes;
        private Long freeBytes;
        private Long maxBytes;
        private Double usagePercentage;
        private String totalFormatted;
        private String usedFormatted;
        private String freeFormatted;
    }

    /**
     * معلومات التخزين
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiskInfo {
        private String status;
        private Long totalBytes;
        private Long usedBytes;
        private Long freeBytes;
        private Double usagePercentage;
        private String totalFormatted;
        private String usedFormatted;
        private String freeFormatted;
        private String uploadsFolderSize;
    }

    /**
     * معلومات النظام
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemInfo {
        private String applicationName;
        private String applicationVersion;
        private String springBootVersion;
        private String javaVersion;
        private String osName;
        private String osVersion;
        private Integer availableProcessors;
        private Long uptimeMs;
        private String uptimeFormatted;
        private OffsetDateTime startTime;
        private String timezone;
        private String activeProfiles;
    }
}
