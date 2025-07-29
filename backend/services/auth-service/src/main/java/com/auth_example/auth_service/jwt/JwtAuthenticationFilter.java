//package com.auth_example.auth_service.jwt;
//
//import com.auth_example.common_service.jwt.TokenPurpose;
//import com.auth_example.common_service.jwt.TokenType;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//    private static final Map<TokenType, String> TOKEN_ROLE_MAP = Map.of(
//            TokenType.USER, "ROLE_USER"
//    );
//    private static final Map<TokenPurpose, String> TOKEN_PURPOSE_MAP = Map.of(
//            TokenPurpose.VERIFY_REGISTRATION, "ROLE_OTP",
//            TokenPurpose.VERIFY_MFA, "ROLE_MFA"
//    );
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String authHeader = request.getHeader("Authorization");
//
//        // if auth header missing or not bearer token, kick user out
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token = authHeader.substring(7);
//        try {
//            Claims claims = jwtService.parseToken(token);
//
//            // get subject
//            String userEmail = claims.getSubject();
//
//            // get list of authorities based on claim
//            List<GrantedAuthority> authorities = getAuthorityFromClaim(claims);
//
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userEmail, null, authorities);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        } catch (JwtException | IllegalArgumentException e) {
//            // invalid token, clear context and return 401
//            SecurityContextHolder.clearContext();
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Invalid or expired token");
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private List<GrantedAuthority> getAuthorityFromClaim(Claims claims) {
//
//        // get role from token type
//        String role;
//        TokenType tokenType = TokenType.valueOf(claims.get("type", String.class));
//        if (tokenType.equals(TokenType.TRANSITIONAL)) {
//            TokenPurpose purpose = TokenPurpose.valueOf(claims.get("purpose", String.class));
//            role = TOKEN_PURPOSE_MAP.get(purpose);
//            if (role == null) {
//                throw new BadCredentialsException("Unknown purpose for transactional token");
//            }
//        } else {
//            role = TOKEN_ROLE_MAP.get(tokenType);
//            if (role == null) {
//                throw new BadCredentialsException("Unknown token type");
//            }
//        }
//
//        return List.of(new SimpleGrantedAuthority(role));
//    }
//
//    ;
//}
