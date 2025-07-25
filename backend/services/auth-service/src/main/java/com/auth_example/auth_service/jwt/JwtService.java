package com.auth_example.auth_service.jwt;

import com.auth_example.common_service.jwt.TokenPurpose;
import com.auth_example.common_service.jwt.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    private static final int OTP_JWT_TTL_IN_MINUTES = 5;
    private static final int MFA_JWT_TTL_IN_MINUTES = 5;
    private static final int USER_JWT_TTL_IN_DAYS = 15;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String genKey() {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64Key = Encoders.BASE64.encode(key.getEncoded());
        return base64Key;
    }

    // generate otp token
    public String generateTransitionalToken(String subject, TokenPurpose purpose) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", TokenType.TRANSITIONAL);
        claims.put("purpose", purpose);
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(subject)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(OTP_JWT_TTL_IN_MINUTES, ChronoUnit.MINUTES)))
                .and()
                .signWith(getSigningKey())
                .compact();
    }

    public String generateUserToken(String subject, TokenPurpose purpose) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", TokenType.USER);
        claims.put("purpose", purpose);
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(subject)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(USER_JWT_TTL_IN_DAYS, ChronoUnit.DAYS)))
                .and()
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

//    public String extractSubject(String token) { return extractClaim(token, Claims::getSubject); };
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimResolver.apply(claims);
//    }
//
//    public Claims extractAllClaims(String token) {
//        return Jwts.parser()
//                .verifyWith(getSigningKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        final String email = extractSubject(token);
//        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    public boolean isTokenExpired(String token) {
//        return extractTokenExpiration(token).before(new Date());
//    }
//
//    public Date extractTokenExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
}
