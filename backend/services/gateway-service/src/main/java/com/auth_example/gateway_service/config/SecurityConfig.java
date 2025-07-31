package com.auth_example.gateway_service.config;

import com.auth_example.gateway_service.handlers.CustomAccessDeniedHandler;
import com.auth_example.gateway_service.handlers.CustomAuthenticationEntryPoint;
import com.auth_example.gateway_service.jwt.CustomAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            CustomAuthenticationEntryPoint authenticationEntryPoint,
            CustomAccessDeniedHandler accessDeniedHandler
    ) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/.well-known/jwks.json")
                        .permitAll()
                        .pathMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/auth/headers"
                        )
                        .permitAll()
                        .pathMatchers(
                                "/api/v1/auth/register/verify"
                        )
                        .hasRole("OTP")
                        .pathMatchers(
                                "/api/v1/auth/mfa/email/initiate",
                                "/api/v1/auth/login/email/verify",
                                "/api/v1/auth/mfa/totp/initiate",
                                "/api/v1/auth/mfa/login/totp/verify"
                        )
                        .hasRole("MFA")
                        .anyExchange().hasRole("USER")
                )
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .oauth2ResourceServer(oath2 -> oath2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(
                                new ReactiveJwtAuthenticationConverterAdapter(
                                        new CustomAuthenticationConverter()
                                )
                        ))
                )
                .build();
    }
}
