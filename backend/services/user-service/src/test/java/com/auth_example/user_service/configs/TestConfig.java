package com.auth_example.user_service.configs;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@TestConfiguration
public class TestConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return Optional::empty;
    }
}
