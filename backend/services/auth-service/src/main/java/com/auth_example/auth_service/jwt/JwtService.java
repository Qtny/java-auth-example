package com.auth_example.auth_service.jwt;

import com.auth_example.auth_service.exceptions.RefreshTokenExpiredException;
import com.auth_example.auth_service.exceptions.RefreshTokenNotFoundException;
import com.auth_example.auth_service.exceptions.RefreshTokenRevokedException;
import com.auth_example.auth_service.exceptions.TokenCreationException;
import com.auth_example.auth_service.jwt.models.RefreshToken;
import com.auth_example.auth_service.jwt.models.UserToken;
import com.auth_example.common_service.jwt.TokenPurpose;
import com.auth_example.common_service.jwt.TokenType;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.el.parser.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${security.jwt.secret}")
    private String jwtSecret;
    private final KeyPair keyPair;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final int OTP_JWT_TTL_IN_MINUTES = 5;
    private static final int MFA_JWT_TTL_IN_MINUTES = 5;
    private static final int USER_JWT_TTL_IN_MINUTES = 10;
    private static final int REFRESH_JWT_TTL_IN_MINUTES = 10;

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

    public String generateAccessToken(String subject, TokenPurpose purpose) {
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

    public String generateRefreshToken(String subject) {
        try {
            Instant now = Instant.now();
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plus(REFRESH_JWT_TTL_IN_MINUTES, ChronoUnit.DAYS)))
                    .claim("type", TokenType.USER)
                    .claim("purpose", TokenPurpose.REFRESH)
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

    @Transactional
    public UserToken renewRefreshToken(String token) {
        // check refresh token validity
        RefreshToken existingRefreshToken = this.validateTokenExpiry(token);
        // invalidate current refresh token
        existingRefreshToken.setRevoked(true);
        refreshTokenRepository.save(existingRefreshToken);

        // generate new access token
        String accessToken = this.generateAccessToken(existingRefreshToken.getEmail(), TokenPurpose.AUTHORIZATION);
        // generate new refresh token
        String refreshToken = this.generateRefreshToken(existingRefreshToken.getEmail());
        // save refresh token
        RefreshToken newRefreshToken = RefreshToken.builder()
                .email(existingRefreshToken.getEmail())
                .refreshToken(refreshToken)
                .expiryDate(LocalDateTime.now().plusDays(REFRESH_JWT_TTL_IN_MINUTES))
                .build();
        refreshTokenRepository.save(newRefreshToken);

        return new UserToken(accessToken, refreshToken);
    }

    public UserToken generateNewUserToken(String email) {

        // generate new access token
        String accessToken = this.generateAccessToken(email, TokenPurpose.AUTHORIZATION);
        // find if existing unrevoked token
        Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByEmailAndRevokedFalse(email);
        // return if not expired
        if (existingRefreshToken.isPresent()) {
            LocalDateTime tokenExpiry = existingRefreshToken.get().getExpiryDate();
            if (tokenExpiry.isBefore(LocalDateTime.now())) {
                return new UserToken(accessToken, existingRefreshToken.get().getRefreshToken());
            }
        }
        // generate new refresh token
        String refreshToken = this.generateRefreshToken(email);
        RefreshToken newRefreshToken = RefreshToken.builder()
                .email(email)
                .refreshToken(refreshToken)
                .expiryDate(LocalDateTime.now().plusDays(REFRESH_JWT_TTL_IN_MINUTES))
                .build();
        refreshTokenRepository.save(newRefreshToken);


        return new UserToken(accessToken, refreshToken);
    }

    public RefreshToken validateTokenExpiry(String token) {
        // fetch token
        RefreshToken existingToken = refreshTokenRepository.findByRefreshToken(token)
                .orElseThrow(() -> new RefreshTokenNotFoundException("refresh token does not exist"));
        // check revoke
        if (existingToken.isRevoked()) {
            throw new RefreshTokenRevokedException("this refresh token has been revoked and is now unusable");
        }
        // check expiry
        if (existingToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RefreshTokenExpiredException("this refresh token has expired");
        }

        return existingToken;
    }
}
