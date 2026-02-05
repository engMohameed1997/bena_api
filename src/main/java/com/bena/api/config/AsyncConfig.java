package com.bena.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * تكوين Async لتنفيذ المهام غير المتزامنة
 * مثل: Audit Logging, Email Sending, Notifications
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Core pool size (عدد الخيوط الأساسية)
        executor.setCorePoolSize(5);
        
        // Max pool size (الحد الأقصى للخيوط)
        executor.setMaxPoolSize(20);
        
        // Queue capacity (سعة الطابور)
        executor.setQueueCapacity(100);
        
        // Thread name prefix (للتميز في logs)
        executor.setThreadNamePrefix("Async-");
        
        // Initialize
        executor.initialize();
        
        log.info("✅ Async Executor configured: core={}, max={}, queue={}", 
            executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        
        return executor;
    }
}
