package com.auth_example.auth_service.jwt;

import com.auth_example.auth_service.jwt.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByEmailAndRevokedFalse(String email);

    Optional<RefreshToken> findByRefreshToken(String token);
}
