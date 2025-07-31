package com.auth_example.challenge_service.mfa.totp;

import com.auth_example.challenge_service.mfa.totp.models.TotpProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TotpMfaRepository extends JpaRepository<TotpProfile, UUID> {
    Optional<TotpProfile> findOneByEmail(String email);
}
