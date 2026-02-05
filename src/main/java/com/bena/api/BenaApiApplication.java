package com.bena.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

/*
 * الملف الرئيسي لتشغيل تطبيق Bena API باستخدام Spring Boot.
 * مسؤول عن بدء تشغيل التطبيق وتهيئة الإعدادات الأساسية مثل:
 * المنفذ، مسار الـ API، الاتصال بقاعدة البيانات PostgreSQL،
 * إعدادات JPA و Flyway، وتفعيل نقاط المراقبة الأساسية.
 */


@SpringBootApplication
@ComponentScan(basePackages = "com.bena.api")
@EnableAsync
@EnableScheduling
public class BenaApiApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BenaApiApplication.class);

        Map<String, Object> defaults = new HashMap<>();
        defaults.put("server.port", "8080");
        defaults.put("server.servlet.context-path", "");

        defaults.put("spring.datasource.url", "jdbc:postgresql://localhost:5432/bena_db");
        defaults.put("spring.datasource.username", "postgres");
        defaults.put("spring.datasource.password", "1997");
        defaults.put("spring.datasource.driver-class-name", "org.postgresql.Driver");

        defaults.put("spring.jpa.hibernate.ddl-auto", "validate");
        defaults.put("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        defaults.put("spring.flyway.enabled", "true");
        defaults.put("spring.flyway.baseline-on-migrate", "true");
        defaults.put("spring.flyway.out-of-order", "true");
        defaults.put("spring.flyway.locations", "classpath:db/migration");

        defaults.put("management.endpoints.web.exposure.include", "health,info");
        app.setDefaultProperties(defaults);

        app.run(args);
    }
}
