package com.auth_example.challenge_service.mfa;

import com.auth_example.challenge_service.mfa.models.MfaChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MfaRepository extends JpaRepository<MfaChallenge, Long> {
    MfaChallenge findOneByUserId(UUID userId);
}
