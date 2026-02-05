package com.bena.api.config;

import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayPermanentConfig {

    @Bean
    public FlywayConfigurationCustomizer flywayConfigurationCustomizer() {
        return new FlywayConfigurationCustomizer() {
            @Override
            public void customize(FluentConfiguration configuration) {
                configuration
                        .outOfOrder(true)
                        .baselineOnMigrate(true)
                        .validateOnMigrate(false);
            }
        };
    }
}
