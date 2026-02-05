package com.bena.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bena API - تطبيق بناي")
                        .version("1.0.0")
                        .description("""
                                API لحساب كلفة البناء في العراق - أدوات المقاول
                                
                                ## الأقسام المتاحة:
                                - **حساب الكلفة**: طابوق، صبة، أساس، سقف، وغيرها
                                - **المواد**: إدارة المواد وأسعارها
                                - **المستخدمين**: إدارة المستخدمين (CRUD)
                                
                                ## العملات المدعومة:
                                - IQD (دينار عراقي)
                                - USD (دولار أمريكي)
                                """)
                        .contact(new Contact()
                                .name("Bena Team")
                                .email("support@bena.iq"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https:/.bena.iq")
                                .description("Production Server")
                ));
    }
}
