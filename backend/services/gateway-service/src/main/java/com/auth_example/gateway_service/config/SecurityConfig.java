package com.auth_example.gateway_service.config;

import com.auth_example.gateway_service.handlers.CustomAccessDeniedHandler;
import com.auth_example.gateway_service.handlers.CustomAuthenticationEntryPoint;
import com.auth_example.gateway_service.jwt.CustomAuthenticationConverter;
import com.auth_example.gateway_service.jwt.CustomReactiveAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            CustomAuthenticationEntryPoint authenticationEntryPoint,
            CustomAccessDeniedHandler accessDeniedHandler,
            CustomReactiveAuthenticationManager authenticationManager
    ) {
        return http
                .headers(headers -> headers
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives("default-src 'self'; script-src 'self'")
                        )
                        .contentTypeOptions(Customizer.withDefaults())
                )
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
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
//                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(
                                        new ReactiveJwtAuthenticationConverterAdapter(
                                                new CustomAuthenticationConverter()
                                        )
                                )
                        )
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(Collections.singletonList("http://localhost:4200")); // specify your frontend origin here
        corsConfig.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Collections.singletonList("*"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return source;
    }

    @Bean
    public ServerAuthenticationConverter bearerTokenAuthenticationConverter() {
        return new ServerBearerTokenAuthenticationConverter();
    }
}
