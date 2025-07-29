package com.auth_example.gateway_service.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CustomAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final Map<TokenType, String> TOKEN_ROLE_MAP = Map.of(
            TokenType.USER, "ROLE_USER"
    );

    private static final Map<com.auth_example.gateway_service.jwt.TokenPurpose, String> TOKEN_PURPOSE_MAP = Map.of(
            TokenPurpose.VERIFY_REGISTRATION, "ROLE_OTP",
            TokenPurpose.VERIFY_MFA, "ROLE_MFA"
    );

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String subject = jwt.getSubject();

        String tokenTypeStr = jwt.getClaimAsString("type");
        if (tokenTypeStr == null) {
            throw new BadCredentialsException("missing token type");
        }

        TokenType tokenType = TokenType.valueOf(tokenTypeStr);
        String role;

        if (tokenType == TokenType.TRANSITIONAL) {
            String purposeStr = jwt.getClaimAsString("purpose");
            if (purposeStr == null) {
                throw new BadCredentialsException("missing token purpose");
            }

            TokenPurpose purpose = TokenPurpose.valueOf(purposeStr);
            role = TOKEN_PURPOSE_MAP.get(purpose);
            if (role == null) {
                throw new BadCredentialsException("unknown purpose for transitional token");
            }
        } else {
            role = TOKEN_ROLE_MAP.get(tokenType);
            if (role == null) {
                throw new BadCredentialsException("unknown token type");
            }
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        return new JwtAuthenticationToken(jwt, authorities, subject);
    }
}
