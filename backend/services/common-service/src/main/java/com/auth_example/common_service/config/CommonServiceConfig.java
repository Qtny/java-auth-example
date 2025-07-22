package com.auth_example.common_service.config;

import com.auth_example.common_service.core.rest.BaseRestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class CommonServiceConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public BaseRestClient baseRestClient(RestClient.Builder builder, ObjectMapper objectMapper) {
        return new BaseRestClient(builder, objectMapper);
    }
}
