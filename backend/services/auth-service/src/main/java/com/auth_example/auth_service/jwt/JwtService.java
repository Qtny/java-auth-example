package com.auth_example.auth_service.jwt;

import com.auth_example.auth_service.exceptions.TokenCreationException;
import com.auth_example.common_service.jwt.TokenPurpose;
import com.auth_example.common_service.jwt.TokenType;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.apache.el.parser.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${security.jwt.secret}")
    private String jwtSecret;
    private final KeyPair keyPair;

    private static final int OTP_JWT_TTL_IN_MINUTES = 5;
    private static final int MFA_JWT_TTL_IN_MINUTES = 5;
    private static final int USER_JWT_TTL_IN_MINUTES = 10;

    // generate otp token
    public String generateTransitionalToken(String subject, TokenPurpose purpose) {
        try {
            Instant now = Instant.now();
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plus(OTP_JWT_TTL_IN_MINUTES, ChronoUnit.MINUTES)))
                    .claim("type", TokenType.TRANSITIONAL)
                    .claim("purpose", purpose)
                    .issuer("auth-service")
                    .build();

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .keyID("auth-key")
                    .type(JOSEObjectType.JWT)
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claims);
            signedJWT.sign(new RSASSASigner(keyPair.getPrivate()));
            return signedJWT.serialize();
        } catch (JOSEException exp) {
            throw new TokenCreationException("user token failed to be created");
        }
    }

    public String generateUserToken(String subject, TokenPurpose purpose) {
         try {
             Instant now = Instant.now();
             JWTClaimsSet claims = new JWTClaimsSet.Builder()
                     .subject(subject)
                     .issueTime(Date.from(now))
                     .expirationTime(Date.from(now.plus(USER_JWT_TTL_IN_MINUTES, ChronoUnit.SECONDS)))
                     .claim("type", TokenType.USER)
                     .claim("purpose", purpose)
                     .issuer("auth-service")
                     .build();

             JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                     .keyID("auth-key")
                     .type(JOSEObjectType.JWT)
                     .build();

             SignedJWT signedJWT = new SignedJWT(header, claims);
             signedJWT.sign(new RSASSASigner(keyPair.getPrivate()));
             return signedJWT.serialize();
         } catch (JOSEException exp) {
             throw new TokenCreationException("user token failed to be created");
         }
    }
}
