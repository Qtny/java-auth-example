package com.auth_example.challenge_service.redis;

import com.auth_example.challenge_service.mfa.BaseMfaChallenge;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface BaseRedisService<
        A extends BaseMfaChallenge
        > {
    void storeMfaEntry(A entry, Duration ttl);

    Optional<A> findMfaById(UUID id);

    Optional<A> findMfaByIdentity(String identity);
}
