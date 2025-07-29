package com.auth_example.gateway_service.jwt;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-1)
public class AuthenticationHeaderPropagationFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .switchIfEmpty(Mono.empty())
                .flatMap(auth -> {
                    ServerHttpRequest.Builder mutatedRequest = exchange.getRequest().mutate();

                    if (auth instanceof JwtAuthenticationToken jwtAuthenticationToken) {
                        Jwt jwt = jwtAuthenticationToken.getToken();

                        String email = jwt.getSubject();
                        if (email != null) mutatedRequest.header("X-User-Email", email);
                    }

                    return chain.filter(exchange.mutate().request(mutatedRequest.build()).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}
